import static groovy.io.FileType.FILES

/**
 * WorkHandler class: pre-processes the user's parameters and executes the PatternFinder class accordingly.
 */
class WorkHandler {
    private String directoryPath //user's relative path to where the working directory is
    private String targetPattern //the text/pattern that user wants to found
    private String replacementPattern //the text/pattern that wants to be placed instead
    private boolean isAsync //whether the programme will be executed with various threads or synchronously
    private Map<String, Integer> shiftsMatchTable //input for the boyer moore algorithm in the PatternFinder class
    private List<String> fileList //list of files (and their canonical paths) that the PatternFinder targets
    private boolean withLog //user defined parameter to see logging or not
    private String logPath //user's relative path to where it wants the logs to be
    private def canonicalPath = new File(".").getCanonicalPath() //frequently used reference to the canonical path of programme
    private File mainLog //log for this class

    WorkHandler(String directoryPath, String targetPattern, String replacementPattern, boolean isAsync, String logPath) {
        this.directoryPath = directoryPath
        this.targetPattern = targetPattern.substring(0, targetPattern.size()) //removes first and last characters to obtain the pattern
        this.replacementPattern = replacementPattern.substring(0, replacementPattern.size()) //removes first and last characters to obtain the pattern
        this.isAsync  = isAsync
        this.logPath = logPath != null ? logPath : "."
        if(logPath != null) {
            this.logPath = logPath
            withLog = true
            mainLog = new File(canonicalPath + logPath + "/main_log.txt")
            mainLog.parentFile.mkdirs() //in case the directory does not exist

        } else {
            this.logPath = "."
            withLog = false
            mainLog = new File(".")
        }

        shiftsMatchTable = [:]
        fileList = []
    }

    /**
     * Initiates work handling process by logging initial time, preparing the necessary data structures for the execution and
     * then decides if it will run with multithreading or normally. Finally, it logs the time that this particular class stops
     * running.
     */
    void run() {
        def start = System.currentTimeMillis()
        if(withLog)
            mainLog << ("STARTING TIME (cpu ms): " + start + "\n\n")

        this.setShiftsMatchTable()
        this.setFileList()

        isAsync ? doAsyncWork() : doSyncWork()

        def end = System.currentTimeMillis()
        if(withLog) {
            mainLog << ("ENDING TIME (cpu ms): " + System.currentTimeMillis() + "\n")
            mainLog << ("TOTAL AMOUNT OF TIME (cpu ms): " + (end - start).toString())
        }
    }

    /**
     * Creates and initiates a thread for every file that will be executed
     */
    private void doAsyncWork() {
        fileList.eachWithIndex{
            it, index ->
                PatternFinder patternFinder = new PatternFinder(it, this.targetPattern, this.replacementPattern, this.shiftsMatchTable.clone(),
                        withLog, canonicalPath + logPath + "/log_" + index.toString() + ".txt")
                Thread thread = new Thread(patternFinder)
                thread.start()
        }

    }

    /**
     * Iteratively finds patterns in each of the files
     */
    private void doSyncWork() {
        fileList.eachWithIndex{
            it, index ->
                new PatternFinder(it, this.targetPattern, this.replacementPattern, this.shiftsMatchTable,
                        withLog, canonicalPath + logPath + "/log_" + index.toString() + ".txt").run()

        }

    }

    /**
     * Recursively finds each of the files within the directory and subdirectories and stores each of the canonical paths in
     * a list (except .DS_Store files). Loads the paths into the log in case it's required. This list is used when work is
     * assigned (asynchronously or synchronously)
     *
     */
    private void setFileList() {
        if(withLog) {
            mainLog << ("The following files were added to do the replacement: \n")
        }

        new File(canonicalPath + directoryPath).eachFileRecurse(FILES) {
            if(!it.name.endsWith('.DS_Store')) {
                if(withLog)
                    mainLog << (it.toString()+ "\n" )

                fileList.add(it.toString())

            }
        }
    }

    /**
     * This table is used by the Boyer-Moore string search algorithm in the pattern finder. The same table is used by all the
     * paths within execution time so it was more efficient to create one and then pass it as a parameter to PatternFinder.
     *
     * The table stores each letter of the target pattern as key, and the relative maximum distance of the letter to the end of
     * the word. It basically assigns how much jumps will be taken between words. More information on the following link:
     * https://www.youtube.com/watch?v=3Ft3HMizsCk
     *
     */
    private void setShiftsMatchTable() {
        targetPattern.eachWithIndex {
            it, index ->
                shiftsMatchTable.put(it, Math.max(1, targetPattern.size() - index - 1))
        }
        shiftsMatchTable.put("*", targetPattern.size())
    }
}
