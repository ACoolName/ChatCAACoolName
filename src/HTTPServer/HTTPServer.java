package HTTPServer;

import ChatServer.ChatServer;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HTTPServer {

    static int port = 9090;
    static String ip = "127.0.0.1";
    ChatServer cs;
    public HTTPServer(ChatServer cs) throws IOException {
        System.out.println("The server has started");
        this.cs = cs;
        InetSocketAddress i = new InetSocketAddress(ip, port);
        HttpServer server = HttpServer.create(i, 0);
        server.createContext("/", new PagesHandler("public/"));
        server.createContext("/Online", new OnlineUsers(cs));
        server.createContext("/ChatLog", new ChatLog());
        server.setExecutor(null);
        server.start();
    }


    public static void main(String[] args) throws IOException {
        if (args.length >= 2) {
            ip = args[0];
            port = Integer.parseInt(args[1]);
        }
        ChatServer chatsrv = new ChatServer();
        HTTPServer httpsrv = new HTTPServer(chatsrv);
    }

    



}
