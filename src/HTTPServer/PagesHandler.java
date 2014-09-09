package HTTPServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PagesHandler implements HttpHandler {

    String contentFolder = "public/";
    private String mimeTypesFilename = "src/assets/mimes";
    private Map<String, String> mimeTypes = new HashMap();

    public PagesHandler(String contentFolder) throws IOException {
        this.contentFolder = contentFolder;
        try {
            loadMimeTypes();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public PagesHandler() {
    }

    protected void loadMimeTypes() throws FileNotFoundException, IOException {
        File file = new File(mimeTypesFilename);
        String[] mimesType;
        if (!file.exists()) {
            throw new FileNotFoundException("Mime type file not found. Aborting.");
        }
        if (!file.canRead()) {
            throw new FileNotFoundException("Mime type file cannot be read. Aborting.");
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = "#";
            while (line != null) {
                if (!line.startsWith("#")) {
                    mimesType = parseMimeTypeLine(line);
                    mimeTypes.put(mimesType[1], mimesType[0]);
                    
                }
                line = br.readLine();
            }
        }
    }

    public Map<String, String> getMimeTypes() {
        return mimeTypes;
    }

    protected String[] parseMimeTypeLine(String line) {
        String[] result = line.split(":\\*");
        return result;
    }

    @Override
    public void handle(HttpExchange he) throws IOException {
        File file = new File(contentFolder + "index.html");
        byte[] bytesToSend = new byte[(int) file.length()];
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            bis.read(bytesToSend, 0, bytesToSend.length);
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        he.sendResponseHeaders(200, bytesToSend.length);
        try (OutputStream os = he.getResponseBody()) {
            os.write(bytesToSend, 0, bytesToSend.length);
        }
    }
}
