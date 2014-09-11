/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package HTTPServerTest;

import static HTTPServerTest.PagesHandlerTest.mimeTypeLines;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Teo
 */
public class OnlineUsersTest {
    
    public OnlineUsersTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws IOException {
        String[] args = {"127.0.0.1", "8080"};
        HTTPServer.HTTPServer.main(args);
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
    public void onlineusersTest() throws IOException {
        URLConnection connection = new URL("http://localhost:8080/Online").openConnection();
        connection.connect();
        Scanner scancontent = new Scanner(connection.getInputStream());
        String content = "";
        String line = "";
        boolean online = false;
        while(scancontent.hasNextLine()){
            line = scancontent.nextLine();
            if (line.contains("users:0")) {
                online = true;
            }
            content += line;
        }
        assertEquals(142,connection.getContentLength());
        assertTrue(online);
    }
}
