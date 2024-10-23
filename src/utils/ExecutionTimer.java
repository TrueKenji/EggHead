package utils;

import logger.Logger;

public class ExecutionTimer {

    private long startTime;
    protected Logger logger = Logger.getLogger();
    private static final ExecutionTimer singleton = new ExecutionTimer();

    private ExecutionTimer() {
    }

    public static ExecutionTimer get() {
        return singleton;
    }

    public void start() {
        startTime = System.nanoTime();
    }

    public void end() {
        long endTime = System.nanoTime(); // or System.currentTimeMillis();
        long duration = endTime - startTime;
        double seconds = (double) duration / 1_000_000_000.0; // Convert nanoseconds to seconds
        logger.println("Execution time: " + seconds + " seconds", Logger.DEBUG, Logger.UTILITIES);
    }

}
