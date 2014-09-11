package ChatServer;

import Shared.ProtocolStrings;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler extends Thread {

    private Scanner input;
    private PrintWriter writer;
    private Socket socket;
    private ChatServer server;
    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public ClientHandler(Socket incomingSocket, ChatServer incomingServer) throws IOException {
        input = new Scanner(incomingSocket.getInputStream());
        writer = new PrintWriter(incomingSocket.getOutputStream(), true);
        this.socket = incomingSocket;
        this.server = incomingServer;

    }

    public void send(String message, String nick) {
        writer.println(ProtocolStrings.MESSAGE + nick + "#" + message);
        Logger.getLogger(ChatServer.class.getName()).log(Level.INFO,
                String.format("Received the message: %1$S ",
                        message.toUpperCase()));
    }

    public void sendOnlineUsers(String name) {
        writer.println(ProtocolStrings.ONLINE + name);
    }

    public void run() {
        try {
            Timer timer = new Timer();
            timer.schedule(new CloseTask(), 1000 * 60 * 15);
            String message = "";
            try {
                message = input.nextLine();
            } catch (OutOfMemoryError x) {
                writer.println(ProtocolStrings.STOP);
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, x);
                try {
                    socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (message.length() > 8 && message.substring(0, 8).equals(ProtocolStrings.CONNECT)
                    && !message.substring(8).matches("^.*[^a-zA-Z0-9 ].*$")
                    && message.substring(8).length() < 12) {
                timer.cancel();
                String name = message.substring(8);
                nickName = name;
                server.addClientHandler(name, this);
                timer = new Timer();
                timer.schedule(new CloseTask(), 1000 * 60 * 15);
                message = input.nextLine();
                while (!message.equals(ProtocolStrings.STOP)) {
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(new CloseTask(), 1000 * 60 * 15);
                    String[] send = message.split("#");
                    if (send[0].equals("SEND") && send.length == 3) {
                        send[2] = send[2].replaceAll("\n", "");
                        if (send[1].equals("*")) {
                            server.sendAll(send[2], this, nickName);
                        } else {
                            String[] names = send[1].split(",");
                            server.send(send[2], names, nickName);
                        }
                        //Logger.getLogger(ChatServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message));

                    }
                    message = input.nextLine(); //IMPORTANT blocking call
                }
            }

            writer.println(ProtocolStrings.STOP);//Chat the stop message back to the client for a nice closedown
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchElementException ex) {
            //Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (nickName != null) {
                server.removeHandler(this.nickName);
            }
        }
        Logger.getLogger(ChatServer.class.getName()).log(Level.INFO, "Closed a Connection");
    }

    public class CloseTask extends TimerTask {

        @Override
        public void run() {
            try {
                socket.close();
                if (nickName != null) {
                    server.removeHandler(nickName);
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
