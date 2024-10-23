package db;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import logger.Logger;

public class EggHeadWriter {

    private static final Logger logger = Logger.getLogger();

    public static void write(String dateiname, ArrayList<String> feld) {
        Writer fw;
        try {
            fw = new FileWriter(dateiname);
        } catch (IOException e) {
            logger.println("Path doesn't exist: " + e, Logger.PROD);
            return;
        }
        try {
            for (String feld1 : feld) {
                fw.write(feld1);
                fw.append(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            logger.println("Error in creating file: " + e, Logger.PROD);
        } finally {
            try {
                fw.close();
            } catch (IOException e) {
            }
        }
    }

}
