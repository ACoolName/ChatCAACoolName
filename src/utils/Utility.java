package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    public static String getFilenameExtension(String filename) {
        String[] tokens = filename.split("\\.(?=[^\\.]+$)");
        return tokens[1];
    }
}
