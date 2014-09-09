package HTTPServerTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class PagesHandlerTest {

    public PagesHandlerTest() {
    }

    public static int mimeTypeLines;

    @BeforeClass
    public static void setUpClass() throws FileNotFoundException, IOException {
        File file = new File("src/assets/mimes");
        mimeTypeLines = 0;
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
                    mimeTypeLines++;
                }
                line = br.readLine();
            }
        }
    }

    @Before
    public void setUp() {
    }

    @Test
    public void parseMimeTypeLineTest() throws IOException {
        WhiteBoxPagesHandler object = new WhiteBoxPagesHandler();
        String[] result = object.parseMimeTypeLinePublic("text/html:*.html");
        assertEquals(".html", result[1]);
        assertEquals("text/html", result[0]);

    }

    @Test
    public void loadMimeTypesTest() throws IOException {
        WhiteBoxPagesHandler object = new WhiteBoxPagesHandler();
        object.loadMimeTypesPublic();
        Map<String, String> results = object.getMimeTypes();
        assertEquals(mimeTypeLines, results.size());
    }
}
