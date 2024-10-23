package logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import listeners.Listener;
import listeners.UpdateEvent;

public abstract class Logger implements Listener {

    private static Logger logger;

    protected final boolean LOG = false; // completely turn of logging for computational speed
    protected Set<Level> printTypes = generatePrintTypes();
    public static final Level PROD = Level.PROD;
    public static final Level DEBUG = Level.DEBUG;

    public static final Level MODEL = Level.MODEL;
    public static final Level BOT = Level.BOT;
    public static final Level VIEW = Level.VIEW;
    public static final Level CONTROLLER = Level.CONTROLLER;
    public static final Level UTILITIES = Level.UTILITIES;
    public static final Level LISTENER = Level.LISTENER;
    public static final Level QUESTION = Level.QUESTION;
    public static final Level COMMON_INFORMATION = Level.COMMON_INFORMATION;
    public static final Level MASTER_TABLE = Level.MASTER_TABLE;
    public static final Level PROJECTION_TABLES = Level.PROJECTION_TABLES;
    public static final Level PROJECT_MASTER_TABLE = Level.PROJECT_MASTER_TABLE;
    public static final Level INIT_CONDITIONAL_INFORMATION = Level.INIT_CONDITIONAL_INFORMATION;
    public static final Level INIT_MASTER_TABLE = Level.INIT_MASTER_TABLE;
    public static final Level EGG_HEAD_CONTROLLER = Level.EGG_HEAD_CONTROLLER;
    public static final Level NEW_GAME = Level.NEW_GAME;
    public static final Level UPDATE_VALIDS = Level.UPDATE_VALIDS;
    public static final Level UPDATE_BY_REACTION = Level.UPDATE_BY_REACTION;
    public static final Level RESET = Level.RESET;
    public static final Level UPDATE_BY_ANSWER = Level.UPDATE_BY_ANSWER;
    public static final Level GET_PLAYER_VIEW = Level.GET_PLAYER_VIEW;
    public static final Level PLAYER_BOT_TUPLE = Level.PLAYER_BOT_TUPLE;
    public static final Level STATISTIC = Level.STATISTIC;

    /**
     * Differentiate between different log tags. There are two main categories
     * 1. PROD vs. DEBUG: PROD should be used for final logging messages, which
     * will most likely be logged even in productive run DEBUG can be used
     * arbitrarily for debugging prints
     *
     * 2. Class<?>. Every class from which logger is called should have a level
     * associated with itself and pass that level within the call.
     *
     * Multiple values can be passed to the log methods, this allows for
     * filtering
     */
    public enum Level {
        PROD,
        MODEL,
        BOT,
        VIEW,
        CONTROLLER,
        UTILITIES,
        LISTENER,
        DEBUG,
        DEBUG_RESET,
        DEBUG_UPDATE_VALIDS,
        QUESTION,
        COMMON_INFORMATION,
        MASTER_TABLE,
        PROJECTION_TABLES,
        PROJECT_MASTER_TABLE,
        INIT_CONDITIONAL_INFORMATION,
        INIT_MASTER_TABLE,
        EGG_HEAD_CONTROLLER,
        NEW_GAME,
        UPDATE_VALIDS,
        UPDATE_BY_REACTION,
        RESET,
        UPDATE_BY_ANSWER,
        GET_PLAYER_VIEW,
        PLAYER_BOT_TUPLE,
        STATISTIC
    }

    protected Logger() {
    }

    public static Logger getLogger() {
        if (logger == null) {
            logger = new ConsoleLogger();
        }
        return logger;
    }

    public abstract void println(Object o, Level... levels);

    public abstract void println(String s, Level... levels);

    protected boolean isLog(Level... levels) {
        return LOG && Arrays.asList(levels).stream().allMatch(printTypes::contains);
    }

    @Override
    public void notify(UpdateEvent e) {
        println(formatUpdateEvent(e), Level.MODEL);
    }

    public String formatUpdateEvent(UpdateEvent e) {
        return "Model changed: " + e.getPropertyName() + ": " + e.getOldValue() + " | " + e.getNewValue();
    }

    public String format(String msg, Level... levels) {
        return msg + " | " + Arrays.toString(levels);
    }

    public void modifyLevels(Level level, boolean flg) {
        if (flg) {
            printTypes.add(level);
        } else {
            printTypes.remove(level);
        }
    }

    public static Set<Level> generatePrintTypes() {
        Set<Level> printTypes = new HashSet();
//        printTypes.add(Level.BOT);
//        printTypes.add(Level.CONTROLLER);
        printTypes.add(Level.PROD);
        printTypes.add(Level.DEBUG);
////        printTypes.add(Level.QUESTION);
//        printTypes.add(Level.DEBUG_RESET);
//        printTypes.add(Level.DEBUG_UPDATE_VALIDS);
        printTypes.add(Level.COMMON_INFORMATION);
//        printTypes.add(Level.MASTER_TABLE);
//        printTypes.add(Level.PROJECTION_TABLES);
//        printTypes.add(Level.PROJECT_MASTER_TABLE);
//        printTypes.add(Level.INIT_CONDITIONAL_INFORMATION);
//        printTypes.add(Level.INIT_MASTER_TABLE);
//        printTypes.add(Level.LISTENER);
        printTypes.add(Level.MODEL);
        printTypes.add(Level.UTILITIES);
//        printTypes.add(Level.VIEW);
        printTypes.add(Level.EGG_HEAD_CONTROLLER);
////        printTypes.add(Level.NEW_GAME);
//        printTypes.add(Level.UPDATE_VALIDS);
        printTypes.add(Level.UPDATE_BY_REACTION);
//        printTypes.add(Level.RESET);
        printTypes.add(Level.UPDATE_BY_ANSWER);
//        printTypes.add(Level.GET_PLAYER_VIEW);
//        printTypes.add(Level.PLAYER_BOT_TUPLE);
        printTypes.add(Level.STATISTIC);
        return printTypes;
    }

}
