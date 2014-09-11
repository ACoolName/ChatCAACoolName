package Shared;

import ChatServer.ChatServer;
import HTTPServer.HTTPServer;
import java.io.IOException;

public class StartServers
{
    public static void main(String[] args) throws IOException {
        ChatServer chatServer = new ChatServer();
        HTTPServer httpServer = new HTTPServer(chatServer);
        chatServer.startServer();
    }
}
