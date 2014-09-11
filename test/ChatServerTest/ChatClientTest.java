package ChatServerTest;

import ChatClient.ChatClient;
import ChatClient.ChatListener;
import ChatServer.ChatServer;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.Utils;

public class ChatClientTest {

    private static final Properties properties = Utils.initProperties("server.properties");
    private static int port;
    private static String ip;
    private static String msg;
    private static CountDownLatch latch;
    private ChatClient client;
    private static int size;

    @BeforeClass
    public static void setUpClass() {
        port = Integer.parseInt(properties.getProperty("port"));
        ip = properties.getProperty("serverIp");
        new Thread(new Runnable() {
            @Override
            public void run() {
                ChatServer.main(null);
            }
        }).start();
    }

    @AfterClass
    public static void tearDownClass() {
        ChatServer.stopServer();
    }

    @Before
    public void setUp() {
        client = new ChatClient();
    }

    @Test
    public void testStopClient() throws IOException, InterruptedException {
        latch = new CountDownLatch(1);

        ChatListener listen = new ChatListener() {

            @Override
            public void messageArrived(String sender, String data) {
            }

            @Override
            public void userListArrived(String[] userList) {
            }

            @Override
            public void stopArrived() {
                msg = "STOP";
                latch.countDown();
            }

        };
        client.connect(ip, port);
        client.registerChatListener(listen);
        client.start();
        client.send("CONNECT#User");
        client.stopClient();
        latch.await(1000, TimeUnit.MILLISECONDS);
        assertEquals("STOP", msg);
    }
    
    @Test
    public void testConnect() throws IOException, InterruptedException {
        latch = new CountDownLatch(1);

        ChatListener listen = new ChatListener() {

            @Override
            public void messageArrived(String sender, String data) {
            }

            @Override
            public void userListArrived(String[] userList) {
                msg = userList[0];
                latch.countDown();
            }

            @Override
            public void stopArrived() {
            }

        };
        client.connect(ip, port);
        client.registerChatListener(listen);
        client.start();
        client.send("CONNECT#User");
        latch.await(1000, TimeUnit.MILLISECONDS);

        assertEquals("User", msg);
    }
    
    
    @Test (expected = IOException.class)
    public void testConnectWrongPort() throws IOException, InterruptedException {
        client.connect(ip, 9999);
    }
    
     @Test
    public void testConnectTwoUsers() throws IOException, InterruptedException {
        latch = new CountDownLatch(2);

        ChatListener listen = new ChatListener() {

            @Override
            public void messageArrived(String sender, String data) {
            }

            @Override
            public void userListArrived(String[] userList) {
                size = userList.length;
                latch.countDown();
            }

            @Override
            public void stopArrived() {
            }

        };
        client.connect(ip, port);
        ChatClient clientTwo = new ChatClient();
        clientTwo.connect(ip, port);
        client.registerChatListener(listen);
        client.start();
        client.send("CONNECT#User");
        clientTwo.send("CONNECT#UserTwo");
        clientTwo.registerChatListener(listen);
        clientTwo.start();
        latch.await(1000, TimeUnit.MILLISECONDS);
        clientTwo.stopClient();
        assertEquals(2, size);
    }

    @Test
    public void testSend() throws IOException, InterruptedException {
        latch = new CountDownLatch(1);

        ChatListener listen = new ChatListener() {

            @Override
            public void messageArrived(String sender, String data) {
                msg = data;
                latch.countDown();
            }

            @Override
            public void userListArrived(String[] userList) {
            }

            @Override
            public void stopArrived() {
            }

        };
        client.connect(ip, port);
        client.registerChatListener(listen);
        client.start();
        client.send("CONNECT#User");
        client.send("SEND#*#Message");
        latch.await(1000, TimeUnit.MILLISECONDS);

        assertEquals("Message", msg);
    }

    @After
    public void tearDown() {
    }

}
