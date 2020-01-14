package utils;

import java.io.File;

/**
 *
 * @author Kenny
 */
public class FileUtils {

    public static boolean isValidFile(String filepath) {
        File file = new File(filepath);

        boolean isLogFile = file.getName().toLowerCase().endsWith(".log");

        return file.isFile() && isLogFile;
    }
}
