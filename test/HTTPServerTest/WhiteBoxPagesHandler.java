package HTTPServerTest;

import HTTPServer.PagesHandler;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WhiteBoxPagesHandler extends PagesHandler {

    public WhiteBoxPagesHandler() {
    }

    public void loadMimeTypesPublic() throws FileNotFoundException, IOException {
        loadMimeTypes();
    }
    
    public String[] parseMimeTypeLinePublic(String line) {
        return parseMimeTypeLine(line);
    }
    
    public String getContentTypePublic(String filename) throws IOException {
        return getContentType(filename);
    }
}
