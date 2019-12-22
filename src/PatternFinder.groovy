/**
 * PatternFinder class: given a file, finds each target pattern and replaces it with another pattern using the Boyer-Moore algorithm.
 * Explanations of this algorithm can be found here:
 * https://www.youtube.com/watch?v=3Ft3HMizsCk&t=
 * https://www.youtube.com/watch?v=Tbj8iH9UkSA&t=
 */
class PatternFinder implements Runnable {
    private String filePath //user's relative path to where the target file is
    private String targetPattern //the text/pattern that user wants to found
    private String replacementPattern //the text/pattern that wants to be placed instead
    private  Map<String, Integer> shiftsMatchTable //table that determine letter shifts in the text
    private File log //writes down all occurences and times for this particular search-replace instancde
    private boolean withLog //user defined parameter to see logging or not

    PatternFinder(String filePath, String targetPattern, String replacementPattern, shiftsMatchTable, boolean withLog, String logPath){
        this.filePath = filePath
        this.targetPattern = targetPattern
        this.replacementPattern = replacementPattern
        this.shiftsMatchTable = shiftsMatchTable
        this.withLog = withLog
        if(withLog)
            log = new File(logPath)
    }

    /**
     * Initiates work handling process by logging initial time, and then goes straight into instantiating the file and
     * searching/replacing patterns. Finally, it logs the time that this particular class stops
     * running
     */
    void run() {
        def start = System.currentTimeMillis()
        if(withLog)
            log << ("STARTING TIME (cpu ms): " + start + "\n\n")

        handleFile(filePath)

        def end = System.currentTimeMillis()
        if(withLog) {
            log << ("ENDING TIME (cpu ms): " + System.currentTimeMillis() + "\n")
            log << ("TOTAL AMOUNT OF TIME: (cpu ms) " + (end - start).toString())
        }
    }

    /**
     * Copies the file, logs (if necessary) the paths of the original and copied file, and then proceeds to do the
     * Boyer-Moore search algorithm on the original one.
     * @param filePath the canonical path to the targeted original file
     */
    private void handleFile(String filePath) {
        def originalFile = new File(filePath)
        def copiedFile = new File(filePath + ".copy")
        copiedFile << originalFile.text
        if(withLog)
            log << ("Original file's path: " + filePath + "\n" +
        "Copied file's path: " + filePath + ".copy" + "\n")
        this.boyerMooreSearch(originalFile)
    }

    /**
     * Actual algorithm for the string replacement. Stores file in a String buffer, shifts its index relatively and
     * targets/replaces patterns as necessary
     * @param file target text that will be used to search and replace patterns
     */
    private void boyerMooreSearch(File file) {
        def fileBuffer = file.text.split("(?!^)") //write each character into a buffer
        def lastCharacterFromPattern = targetPattern.substring(targetPattern.size() - 1) //character from file that will
        // be used to compare indexes from the file
        int counter = 0 //counts occurrences in the text
        for(int fileIndex = targetPattern.size() - 1; fileIndex < fileBuffer.size();) { //navigates through file
            def characterFromFile = fileBuffer[fileIndex] //relative character targeted in file
            // (starts with the character whose position is the same as the last character from the pattern
            if(characterFromFile == lastCharacterFromPattern &&
                    this.patternsMatch(fileBuffer, fileIndex - targetPattern.size() + 1, fileIndex)) { //if characters
                // match and they are the same pattern
                if(withLog)
                    log << ("Found match at index " + fileIndex.toString() + "\n")
                counter++ //increase occurences
                fileBuffer = this.replacePattern(fileIndex, fileBuffer) //replace pattern with the new one
                fileIndex -= targetPattern.size() - 1 //send index back to the position where the target word started
                fileIndex += replacementPattern.size() //move the index the amount of positions one position after the
                // replaced word started
            } else
                fileIndex += shiftsMatchTable.get(characterFromFile, targetPattern.size()) //if not found, shift index
            // the amount of times indicated at table


        }

        if(withLog)
            log << ("\n\nTotal amount of replacements: " + counter + "\n")

        file.setText(fileBuffer.join()) //Converts buffer back into text and replaces the old text in the file

    }

    /**
     * Aiding method to the Boyer-Moore string search algorithm that verifies if patterns with the same ending character
     * match entirely. It requires character-by-character comparison since the file text is stored in a buffer.
     *
     * Side note: writing the text inside of the algorithm looked messy and confusing, so for clarifying reasons it was
     * best to extract this particular functionality
     *
     * @param fileBuffer string buffer where all the text is stored at
     * @param startPos starting position of the compared pattern in the buffer
     * @param endPos ending position of the compare pattern in the buffer
     * @return true if they match, false if otherwise
     */
    private boolean patternsMatch(String[] fileBuffer, int startPos, int endPos) {
        def filePattern = fileBuffer[startPos.. endPos]
        boolean matched = true
        targetPattern.eachWithIndex {
            it, index ->
                if(filePattern.get(index) != it)
                    matched = false
        }

        return matched
    }

    /**
     * Aiding method to the Boyer-Moore algorithm that changes the target pattern with the replacement one. It splits
     * the string buffer into two, removes the last characters from the first buffer that are part of the old pattern,
     * inserts the new characters at the same place, and then pastes the two buffers back into one. Probably very cost-
     * ly in terms of memory, but is generic enough to disregard the size differences between the target and replace-
     * ment patterns.
     *
     *
     * @param fileIndex position where the target pattern ends
     * @param fileBuffer string buffer where all the text is stored at
     * @return a fileBuffer with the replaced pattern
     */
    private String[] replacePattern(int fileIndex, String[] fileBuffer) {
        def fileList = fileBuffer.toList()
        def beginningList = fileList.clone().subList(0, fileIndex + 1)
        int listDifference = fileList.size() - beginningList.size()
        def endList = listDifference != 0 ? fileList.clone().subList(fileIndex + 1, fileList.size()) : []
        for(int i = 0; i < targetPattern.size(); i++)
            beginningList.removeLast()

        for(int i = 0; i < replacementPattern.size(); i++)
            beginningList.add(replacementPattern[i])

        fileList = beginningList + endList

        return fileList as String[]

    }


}
