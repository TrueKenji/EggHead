package utils;

import java.util.Comparator;

public interface ArrayIndexComparator extends Comparator<Integer>{
    
    public Integer[] createIndexArray();
    
    public int[] getArray();
    
}
