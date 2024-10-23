package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import utils.CollectionFactory;
import utils.Combinatorics;

public class Stack extends ArrayList<Integer> implements Comparable {

    /**
     * Upper bound for the range of card values.
     */
    public static int CARDSSIZE = 4;
    /**
     * Number of cards per player.
     */
    public static final int STACKSIZE = 3;
    private int hashCode;

    public Stack() {

    }

    public Stack(List<Integer> list) {
        list.sort(null);
        this.addAll(list);
    }

    public Stack(int... list) {
        this(Arrays.stream(list).boxed().collect(Collectors.toList()));
    }

    public int countOccurences(int value) {
        int sum = 0;
        for (int i : this) {
            sum += i == value ? 1 : 0;
        }
        return sum;
    }

    public int countOccurences(int... values) {
        int sum = 0;
        for (int i : values) {
            sum += countOccurences(i);
        }
        return sum;
    }

    public boolean distinct() {
        return new HashSet(this).size() == size();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Stack) {
            return o.hashCode() == hashCode();
//            Stack otherCopy = Utilities.deepCopy(other);
//            if (otherCopy.size() == size()) {
//                for (Integer i : this) {
//                    if (!otherCopy.remove(i)) {
//                        return false;
//                    }
//                }
//                return true;
//            }
        }
        return false;
    }

    /*
    just put the numbers as 10th positions
    (1,2,3) -> 123
    +1 because 0 can't be in front
     */
    @Override
    public int hashCode() {
        if (hashCode == 0) {
//            for (int i = 0; i < size(); i++) {
//                hashCode += Math.pow(10, size() - i - 1) * (get(i) + 1);
//            }
            int multiplier = 1;
            for (int i = size() - 1; i >= 0; i--) {
                hashCode += multiplier * (get(i) + 1);
                multiplier *= 10;
            }
        }
        return hashCode;
    }

    @Override
    public String toString() {
//        ArrayList<Integer> shallowCopy = new ArrayList<>(this);
//        shallowCopy.sort(null);
//return hashCode() + "";
        String s = new String();
        for (Integer i : this) {
            s += i + " ";
        }
        return s;
    }

    public String format() {
        String s = new String();
        for (Integer i : this) {
            s += i + ",";
        }
        return s.substring(0, s.length() - 1);
    }

    public Stack deepcopy() {
        List<Integer> copyList = new ArrayList();
        stream().forEach(integer -> copyList.add(integer));
        return new Stack(copyList);
    }

//    public static Stack merge(Collection<Stack> stacks) {
//        Stack stack = new Stack();
//        for (Stack s : stacks) {
//            stack.addAll(s);
//        }
//        return stack;
//    }
    public static Stack generate(int n) {
        return new Stack(Combinatorics.RND.nextInt(n), Combinatorics.RND.nextInt(n), Combinatorics.RND.nextInt(n));
    }

    public static Stack generate() {
        return generate(CARDSSIZE);
    }

    public static Set<Stack> intersection(Set<Stack> stack1, Set<Stack> stack2) {
        Set<Stack> intersection = CollectionFactory.factory.createSet();
        for (Stack s : stack1) {
            if (stack2.contains(s)) {
                intersection.add(s);
            }
        }
        return intersection;
    }

    @Override
    public int compareTo(Object o) {
        return hashCode() < o.hashCode() ? -1 : (equals(o) ? 0 : 1);
    }
}
