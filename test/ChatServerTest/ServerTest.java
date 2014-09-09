package ChatServerTest;

import ChatClient.ChatListener;
import ChatServer.ChatServer;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ServerTest {

    private Socket socket;
    private Scanner input;
    private PrintWriter output;
    private List<ChatListener> listeners = new ArrayList<>();

    @BeforeClass
    public static void setUpClass() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ChatServer.main(null);
            }
        }).start();
    }

    @Before
    public void setUp() throws IOException {
        socket = new Socket("localhost", 8080);
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);
    }

    @After
    public void tearDown() throws IOException {
        socket.close();
        input.close();
        output.close();
    }

    /**
     *
     */
    @Test
    public void testingConnectProtocolForServer() {
        output.println("CONNECT#User");
        String s = input.nextLine();
        assertEquals("ONLINE#User", s);
    }

    @Test
    public void testingSendProtocolForServer() {
        output.println("CONNECT#User");
        input.nextLine();
        output.println("SEND#User#Message");
        String s = input.nextLine();
        assertEquals("MESSAGE#User#Message", s);
    }

    @Test
    public void testingSendToAllProtocolForServer() {
        output.println("CONNECT#User");
        input.nextLine();
        output.println("SEND#*#Message");
        String s = input.nextLine();
        assertEquals("MESSAGE#User#Message", s);
    }

    @Test
    public void testingCloseProtocolForServer() {
        output.println("CONNECT#User");
        input.nextLine();
        output.println("CLOSE#");
        String s = input.nextLine();
        assertEquals("CLOSE#", s);
    }


}
