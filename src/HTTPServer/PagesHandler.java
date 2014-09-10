package HTTPServer;

import com.sun.net.httpserver.Headers;
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
import java.nio.file.Files;
import utils.Utility;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        String uri = he.getRequestURI().getPath();
        uri = uri.substring(7);
        System.out.println("Handle in pages handler   " + uri);
        String filename = "";
        if ("".equals(uri)) {
            filename = "index.html";
        } else {
            filename = uri;
        }
        File file = null;
        byte[] bytesToSend = null;
        try {
            file = new File(contentFolder + filename);
            bytesToSend = new byte[(int) +file.length()];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            bis.read(bytesToSend, 0, bytesToSend.length);
            Headers h = he.getResponseHeaders();
            String type = "text/html";
            type = getContentType(filename);
            h.add("Content-type", type);
            System.out.println("Bytes to send length   " + bytesToSend.length + type);
            he.sendResponseHeaders(200, bytesToSend.length);
        } catch (FileNotFoundException fnfe) {
            System.out.println("file " + contentFolder + filename + " not found.");
            bytesToSend = "Nope, this file does not exist.".getBytes();
            he.sendResponseHeaders(404, bytesToSend.length);
        } catch (IOException ie) {
            ie.printStackTrace();
        } finally {
            try (OutputStream os = he.getResponseBody()) {
                os.write(bytesToSend, 0, bytesToSend.length);
            }
        }
    }

    protected String getContentType(String filename) throws IOException {
        String extension = "";
        int i = filename.lastIndexOf('.');
        if (i >= 0) {
            extension = filename.substring(i);
        }
        return mimeTypes.get(extension);
    }
}
