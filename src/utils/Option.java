package utils;

import java.util.HashMap;
import java.util.Map;

public interface Option {

    final Map<Class<? extends Option>, Map<Integer, Option>> viewToOptionMap = createViewToOptionMapping();

    public static enum MODE implements Option {
        CONTINUOUS,
        TIMER_CONTROLLED,
        USER_CONTROLLED
    }

    public static enum VIEW implements Option {
        FRAME,
        CONSOLE,
        NONE
    }

    public static enum PLAYER implements Option {
        MANUAL,
        BOT
    }

    public static enum DIFFICULTY implements Option {
        LEVEL_0,
        LEVEL_1,
        LEVEL_2,
        LEVEL_3
    }

    public static enum SPEED implements Option {
        SLOW,
        NORMAL,
        FAST,
    }

    public static enum STATS implements Option {
        TRUE,
        FALSE
    }

    public static Map<Class<? extends Option>, Option> getDefaultOptions() {
        Map<Class<? extends Option>, Option> options = new HashMap();
        options.put(VIEW.class, VIEW.FRAME);
        options.put(MODE.class, MODE.USER_CONTROLLED);
        options.put(PLAYER.class, PLAYER.BOT);
        options.put(DIFFICULTY.class, DIFFICULTY.LEVEL_3);
        options.put(SPEED.class, SPEED.FAST);
        options.put(STATS.class, STATS.FALSE);
        return options;
    }

    public static Option getOption(Class<? extends Option> type, Integer value) {
        return viewToOptionMap.get(type).get(value);
    }

    public static Integer getOption(Class<? extends Option> type, Option option) {
        for (Map.Entry<Integer, Option> entry : viewToOptionMap.get(type).entrySet()) {
            if (entry.getValue().equals(option)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static Map<Class<? extends Option>, Map<Integer, Option>> createViewToOptionMapping() {
        Map<Class<? extends Option>, Map<Integer, Option>> map = new HashMap();

        Map<Integer, Option> difficultyMap = new HashMap();
        difficultyMap.put(0, DIFFICULTY.LEVEL_0);
        difficultyMap.put(1, DIFFICULTY.LEVEL_1);
        difficultyMap.put(2, DIFFICULTY.LEVEL_2);
        difficultyMap.put(3, DIFFICULTY.LEVEL_3);
        map.put(DIFFICULTY.class, difficultyMap);

        Map<Integer, Option> modeMap = new HashMap();
        modeMap.put(0, MODE.USER_CONTROLLED);
        modeMap.put(1, MODE.TIMER_CONTROLLED);
        modeMap.put(2, MODE.TIMER_CONTROLLED);
        modeMap.put(3, MODE.TIMER_CONTROLLED);
        modeMap.put(4, MODE.CONTINUOUS);
        map.put(MODE.class, modeMap);

        Map<Integer, Option> speedMap = new HashMap();
        speedMap.put(1, SPEED.SLOW);
        speedMap.put(2, SPEED.NORMAL);
        speedMap.put(3, SPEED.FAST);
        map.put(SPEED.class, speedMap);

        Map<Integer, Option> statsMap = new HashMap();
        statsMap.put(0, STATS.FALSE);
        statsMap.put(1, STATS.TRUE);
        map.put(STATS.class, statsMap);

        Map<Integer, Option> playerMap = new HashMap();
        playerMap.put(0, PLAYER.BOT);
        playerMap.put(1, PLAYER.MANUAL);
        map.put(PLAYER.class, playerMap);

        return map;
    }

}
