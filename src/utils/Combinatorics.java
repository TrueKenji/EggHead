package utils;

import bot.PlayerBot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Combinatorics {

    //public static final int seed = 4;
    /**
     * seed = 0 with 3 players + answeringPlayer leads to a second round where
     * every player solves in turns because of the previous solving player
     * seed = 0 with 4 players + answeringPlayer leads to a first round where
     * no actual information can be extracted, but the commonInfo changes a bit
     */
    public static final int SEED = 2;
    public static final Random RND = new Random();

    /**
     * return all subsets with order with repetition
     *
     * @param k: size of subset
     * @param n: space size {0,...,n-1}
     * @return list of subsets
     */
    public static ArrayList<ArrayList<Integer>> genVariationsWithRepetion(int k, int n) {
        ArrayList<ArrayList<Integer>> list = new ArrayList();
        for (int i = 0; i < Math.pow(n, k); i++) {
            ArrayList<Integer> line = new ArrayList();
            for (int j = 0; j < k; j++) {
                line.add((i / ((int) Math.pow(n, j))) % n);
            }
            list.add(line);
        }
        return list;
    }

    public static ArrayList<ArrayList<Integer>> genCombinationsWithoutRepetition(int k, int n) {
        ArrayList<ArrayList<Integer>> list = genCombinationsWithRepetition(k, n);
        ArrayList<ArrayList<Integer>> res = new ArrayList();
        for (ArrayList<Integer> line : list) {
            if (!containsRepetition(line)) {
                res.add(line);
            }
        }
        return res;
        //        ArrayList<ArrayList<Integer>> list = new ArrayList();
        //        for(int i = 0 ; i < binom(n, k); i++){
        //
        //        }
        //        return list;
    }

    /**
     * return all subsets without order with repetition
     *
     * @param k: size of subsets
     * @param n: number of elements {0,...,n-1}
     * @return list of subsets
     */
    public static ArrayList<ArrayList<Integer>> genCombinationsWithRepetition(int k, int n) {
        ArrayList<ArrayList<Integer>> list = new ArrayList();
        list.add(new ArrayList<>(Collections.nCopies(k, 0)));
        for (int i = 1; i < binom(n + k - 1, k); i++) {
            int idx = ArrayComprehension.getIndexOfFirstElementLessThan(list.get(i - 1), n - 1);
            ArrayList<Integer> line = new ArrayList();
            for (int j = 0; j <= idx; j++) {
                line.add(list.get(i - 1).get(idx) + 1);
            }
            for (int j = idx + 1; j < k; j++) {
                line.add(list.get(i - 1).get(j));
            }
            list.add(line);
        }
        return list;
    }

    public static List<PlayerBot> subList(List<PlayerBot> sourceList, List<Integer> indexes) {
        return indexes.stream()
                .filter(index -> index >= 0 && index < sourceList.size())
                .map(sourceList::get)
                .collect(Collectors.toList());
    }

    public static int binom(int n, int k) {
        if ((n == k) || (k == 0)) {
            return 1;
        } else {
            return binom(n - 1, k) + binom(n - 1, k - 1);
        }
    }

    public static boolean containsRepetition(ArrayList<Integer> line) {
        ArrayList<Integer> repetition = new ArrayList();
        for (Integer i : line) {
            if (repetition.contains(i)) {
                return true;
            } else {
                repetition.add(i);
            }
        }
        return false;
    }

}
