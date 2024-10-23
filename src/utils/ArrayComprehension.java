package utils;

import bot.PlayerBot;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import model.Stack;

public class ArrayComprehension {

    public static List<Integer> generateStream(int startInclusive, int endInclusive) {
        return asList(IntStream.rangeClosed(startInclusive, endInclusive).toArray());
    }

    public static List<Integer> asList(int... input) {
        return IntStream.of(input).boxed().collect(Collectors.toList());
    }

    public static int getIndexOfFirstElementLessThan(ArrayList<Integer> array, int threshold) {
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i) < threshold) {
                return i;
            }
        }
        return -1;
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> enumClass) {
        int x = Combinatorics.RND.nextInt(enumClass.getEnumConstants().length);
        return enumClass.getEnumConstants()[x];
    }
    
    public static int add(Collection<Integer> collection){
        int sum = 0;
        for(int i : collection){
            sum += i;
        }
        return sum;
    }

    public static int[] add(int[] a, int[] b) {
        int[] res = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i] + b[i];
        }
        return res;
    }

    public static ArrayList<PlayerBot> subList(PlayerBot[] array, List<Integer> indexes) {
        ArrayList<PlayerBot> sublist = new ArrayList();
        for (Integer idx : indexes) {
            sublist.add(array[idx]);
        }
        return sublist;
    }

    public static Map<PlayerBot, Stack> deepcopyPlayersMap(Map<PlayerBot, Stack> players) throws RuntimeException{
        Map<PlayerBot, Stack> copy;
        try {
            copy = players.getClass().getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            throw new RuntimeException("Could not instantiate map: " + players);
        }
        for (PlayerBot bot : players.keySet()) {
            copy.put(bot.deepcopy(), players.get(bot).deepcopy());
        }
        return copy;
    }
    
}
