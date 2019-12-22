class Main {

    static void main(String[] args) {
        WorkHandler workHandler = args.length < 5 ? new WorkHandler(args[0], args[1], args[2], args[3].toBoolean(), null)
                : new WorkHandler(args[0], args[1], args[2], args[3].toBoolean(), args[4])
        workHandler.run()

    }



}
