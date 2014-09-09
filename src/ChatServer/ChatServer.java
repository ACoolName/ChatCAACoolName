package ChatServer;

import Shared.ProtocolStrings;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Utils;

public class ChatServer {

    private static boolean keepRunning = true;
    private static ServerSocket serverSocket;
    private static final Properties properties = Utils.initProperties("server.properties");
    private static Map<String, ClientHandler> clients;

    public ChatServer() {
        clients = new ConcurrentHashMap<>();
        int port = Integer.parseInt(properties.getProperty("port"));
        String ip = properties.getProperty("serverIp");
        String logFile = properties.getProperty("logFile");
        Utils.setLogFile(logFile, ChatServer.class.getName());
        Logger.getLogger(ChatServer.class.getName()).log(Level.INFO, "Sever started");
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
            do {
                Socket socket = serverSocket.accept(); //Important Blocking call
                Logger.getLogger(ChatServer.class.getName()).log(Level.INFO, "Connected to a client");
                ClientHandler ch = new ClientHandler(socket, this);
                ch.start();
            } while (keepRunning);
        } catch (IOException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Utils.closeLogger(ChatServer.class.getName());
        }
    }

    public void addClientHandler(String name, ClientHandler ch) {
        clients.put(name, ch);
        sendNickNameList();
    }

    public static void stopServer() {
        keepRunning = false;
    }

    public void removeHandler(String name) {
        clients.remove(name);
        sendNickNameList();
    }

    public synchronized void sendAll(String message, ClientHandler handler) {
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            entry.getValue().send(message);
        }
    }
    
    public synchronized void send(String message, String[] names) {
        for (int i = 0; i < names.length; i++) {
            if(clients.containsKey(names[i]))
                clients.get(names[i]).send(message);
        }
    }
    

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
    }

    private void sendNickNameList() {
        String names = "";
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            names += entry.getKey() + ",";
        }
        if(names.length()!=0)
        {
        names=names.substring(0,names.length()-1);
        }
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            entry.getValue().sendOnlineUsers(names);
        }
    }
}
