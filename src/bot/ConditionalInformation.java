package bot;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import utils.CollectionFactory;

/**
 * wrapper collecting all scenarios. toString is overwritten to transform into
 * readable table offers a performant .get method for frequent lookups in
 *
 * Each scenario is a key holding the associated value in itself via dummy
 *
 * @ProjectionTables.fitsReaction
 */
public class ConditionalInformation {

    //private Map<StackScenario, Set<Stack>> table;
    private Set<StackScenario> scenarios = CollectionFactory.factory.createSet();

    // used in @ProjectionTables.initStackMapping
    private Map<Double, StackScenario> hashCodeMap = CollectionFactory.factory.createMap();

    public boolean add(StackScenario scenario) {
        hashCodeMap.put(scenario.bigHashCode(), scenario);
        return scenarios.add(scenario);
    }

    public Iterator<StackScenario> iterator() {
        return scenarios.iterator();
    }

    public void initTransaction() {
        scenarios.parallelStream().forEach(scenario -> scenario.initTransaction());
    }

    public void transaction() {
        scenarios.parallelStream().forEach(scenario -> scenario.transaction());
    }

    /**
     * retrieves the actual StackScenario object from this table based on a
     * (possibly synthetic) StackScenario parameter. Ultimately, the dummy
     * associated with the StackScenario can be obtained this way
     *
     * @param other
     * @return
     */
    public StackScenario get(StackScenario other) {
        return hashCodeMap.get(other.bigHashCode());
    }

    public ConditionalInformation deepDummyCopy() {
        ConditionalInformation copy = new ConditionalInformation();
        for (StackScenario scenario : scenarios) {
            copy.add(scenario.deepDummyCopy());
        }
        return copy;
    }

    @Override
    public String toString() {
        String s = new String();
        for (StackScenario scenario : scenarios) {
            for (PlayerBot player : scenario.keySet()) {
                s += player.toString() + ":" + scenario.get(player) + ",";
            }
            s += " = " + scenario.getDummy().getValids() + System.lineSeparator();
        }
        return s;
    }

}
