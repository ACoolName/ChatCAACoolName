package utils;

public class Utility {

    public static String getFilenameExtension(String filename) {
        String[] tokens = filename.split("\\.(?=[^\\.]+$)");
        return tokens[1];
    }
}
