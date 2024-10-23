package utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CollectionFactory allows to easily switch between using HashMap or TreeMap
 * TreeMap has been used for debugging, as it provides readability
 * HashMap is used for production, as it improves performance
 */
public abstract class CollectionFactory {

    public static final CollectionFactory factory = new UnorderedCollectionFactory();

    public abstract Set createSet();

    public abstract Map createMap();

    private static class OrderedCollectionFactory extends CollectionFactory {

        @Override
        public Set createSet() {
            return new TreeSet();
        }

        @Override
        public Map createMap() {
            return new TreeMap();
        }

    }

    private static class UnorderedCollectionFactory extends CollectionFactory {

        @Override
        public Set createSet() {
            return new HashSet();
        }

        @Override
        public Map createMap() {
            return new ConcurrentHashMap();
        }

    }

}
