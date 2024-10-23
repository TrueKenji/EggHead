package utils;

public class ArrayIndexDescendingComparator implements ArrayIndexComparator {

    private final int[] array;

    public ArrayIndexDescendingComparator(int[] array) {
        this.array = array;
    }
    
    @Override
    public int[] getArray(){
        return array;
    }

    @Override
    public Integer[] createIndexArray() {
        Integer[] indexes = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            indexes[i] = i; // Autoboxing
        }
        return indexes;
    }

    @Override
    public int compare(Integer index1, Integer index2) {
        // Autounbox from Integer to int to use as array indexes
        return array[index1] < array[index2] ? 1 : (array[index1] == array[index2] ? 0 : -1);
    }
}
