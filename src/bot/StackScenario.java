package bot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import model.Stack;

public class StackScenario extends HashMap<PlayerBot, Stack> implements Comparable<StackScenario> {

    private PlayerBot dummy;
    private PlayerBot _transactionDummy;
    private double bigHashCode;
    private boolean transactionStarted;

    public StackScenario(int id) {
        this.dummy = new PlayerBot(id);
        this._transactionDummy = new PlayerBot(id);
    }

    public StackScenario(Map<PlayerBot, Stack> map) {
//        super(Comparator.comparing(PlayerBot::getID));
        putAll(map);
    }

    public StackScenario(Map<PlayerBot, Stack> map, PlayerBot dummy) {
        this(map);
        this.dummy = dummy;
    }

    public StackScenario(Map<PlayerBot, Stack> map, PlayerBot dummy, long bigHashCode) {
        this(map, dummy);
        this.bigHashCode = bigHashCode;
    }

    /**
     * create scenario from appropriate input string Format is:
     * 0,0,0;1,2,0;3,2,0 i.e. players are ; separated, each card is , separated
     *
     * @param scenarioString
     */
    public StackScenario(String scenarioString) {
        String[] stacks = scenarioString.split(";");
        for (int i = 0; i < stacks.length; i++) {
            Stack stack = new Stack();
            for (String stackEntry : stacks[i].split(",")) {
                stack.add(Integer.valueOf(stackEntry));
            }
            PlayerBot player = new PlayerBot(i);
            super.put(player, stack);
        }
    }

    public String format() {
        String s = new String();
        for (Stack stack : values()) {
            s += stack.format() + ";";
        }
        return s.substring(0, s.length() - 1);
    }

    /**
     * Copy only this map, but neither the keys/values or the dummy
     *
     * @return
     */
    public StackScenario shallowCopy() {
        return new StackScenario(this, this.dummy);//, bigHashCode);
    }

    /**
     * create only a shallow copy of this map, but a deepcopy of the dummy.
     *
     * @return StackScenario
     */
    public StackScenario deepDummyCopy() {
        return new StackScenario(this, this.dummy.deepcopy());
    }

    public PlayerBot getDummy() {
        return dummy;
    }

    public PlayerBot getTransactionDummy() {
        return _transactionDummy;
    }

    public void initTransaction() {
        // whenever a transaction is planned, the dummies must be aligned first.
        // alternatively, we could apply every direct update to dummy also to
        // transaction dummy.
        if (transactionStarted) {
            throw new RuntimeException("Pending transaction! " + this);
        }
        _transactionDummy.setValids(new ArrayList(dummy.getValids()));
        transactionStarted = true;
    }

    public void transactionClear() {
        _transactionDummy.getValids().clear();
    }

    public Iterator<Stack> transactionIterator() {
        return _transactionDummy.getValids().iterator();
    }

    public void transaction() {
        this.dummy.setValids(new ArrayList(_transactionDummy.getValids()));
        transactionStarted = false;
    }

    public boolean contains(StackScenario scenario) {
        for (Map.Entry<PlayerBot, Stack> entry : scenario.entrySet()) {
            if (!contains(entry)) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(Map.Entry<PlayerBot, Stack> element) {
        return contains(element.getKey(), element.getValue());
    }

    public boolean contains(PlayerBot player, Stack s) {
        for (Map.Entry<PlayerBot, Stack> entry : entrySet()) {
            if (entry.getKey().equals(player) && entry.getValue().equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * observers should be missing one player. Get that one's stack
     *
     * @param observers
     * @return Stack, the stack of the missing player in the given collection
     */
    public Stack getObserved(Collection<PlayerBot> observers) {
        for (Map.Entry<PlayerBot, Stack> entry : entrySet()) {
            if (!observers.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        throw new RuntimeException("Missing player not found");
    }

    public static double biggestExp = 0;

    /**
     * put the stack hashCodes per player next to each other via 10^ calculation
     * {(1,2,3),(2,3,4),(0,1,1)} -> 123234011
     *
     * @return
     */
    public double bigHashCode() {
        if (bigHashCode == 0) {
            for (PlayerBot player : keySet()) {
//                bigHashCode += get(player).hashCode() * Math.pow(10, Stack.STACKSIZE * player.getID());
//                if (Math.pow(10, Stack.STACKSIZE * player.getID()) > biggestExp) {
//                    biggestExp = Math.pow(10, Stack.STACKSIZE * player.getID());
//                }
                bigHashCode += get(player).hashCode() * switchLookup(Stack.STACKSIZE * player.getID());
//                System.out.println(bigHashCode);
            }
        }
        return bigHashCode;
    }

    @Override
    public int compareTo(StackScenario o) {
        return Double.valueOf(bigHashCode()).compareTo(o.bigHashCode());
    }

    public static StackScenario sample(int numPlayers, int cardSize) {
        StackScenario scenario = new StackScenario(numPlayers);
        for (int ctr = 0; ctr < numPlayers; ctr++) {
            scenario.put(new PlayerBot(ctr), Stack.generate(cardSize));
        }
        return scenario;
    }

    public static StackScenario sample(int numPlayers) {
        return sample(numPlayers, Stack.CARDSSIZE);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        StackScenario other = (StackScenario) obj;
        return other.bigHashCode() == bigHashCode();
        //return Math.abs(other.bigHashCode() - bigHashCode()) < 0.5;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    public String toFullString() {
        return super.toString() + ": " + dummy.getValids().toString();
    }

    private double switchLookup(int exponent) {
        switch (exponent) {
            case 0: {
                return 1.0;
            }
            case 1: {
                return 10.0;
            }
            case 2: {
                return 100.0;
            }
            case 3: {
                return 1_000.0;
            }
            case 4: {
                return 10_000.0;
            }
            case 5: {
                return 100_000.0;
            }
            case 6: {
                return 1_000_000.0;
            }
            case 7: {
                return 10_000_000.0;
            }
            case 8: {
                return 100_000_000.0;
            }
            case 9: {
                return 1_000_000_000.0;
            }
            case 10: {
                return 10_000_000_000.0;
            }
            case 11: {
                return 100_000_000_000.0;
            }
            case 12: {
                return 1_000_000_000_000.0;
            }
            case 13: {
                return 10_000_000_000_000.0;
            }
            case 14: {
                return 100_000_000_000_000.0;
            }
            case 15: {
                return 1_000_000_000_000_000.0;
            }
        }
        throw new RuntimeException();
    }

}
