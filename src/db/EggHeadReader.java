package db;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EggHeadReader {

    public static ArrayList<String> load(String filename, boolean leaveFirstLine) {
        ArrayList<String> list = new ArrayList();
        String s;
        try {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)))) {
                if (leaveFirstLine) {
                    br.readLine();
                }
                while ((s = br.readLine()) != null) {
                    list.add(s);
                }
            }
        } catch (IOException e) {
            System.out.println("Fehler: " + e.getLocalizedMessage());
        }
        return list;
    }

    public static ArrayList<List<String>> load(String filename, String delimiter, boolean leaveFirstLine) {
        ArrayList<String> list = load(filename, leaveFirstLine);
        ArrayList<List<String>> ret = new ArrayList();
        for (String s : list) {
            ret.add(Arrays.asList(s.split(delimiter)));
        }
        return ret;
    }
}
