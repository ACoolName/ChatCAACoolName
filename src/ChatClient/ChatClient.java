package ChatClient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import Shared.ProtocolStrings;

public class ChatClient extends Thread {

    Socket socket;
    private int port;
    private InetAddress serverAddress;
    private Scanner input;
    private PrintWriter output;
    private List<ChatListener> listeners = new ArrayList<>();

    public void registerChatListener(ChatListener l) {
        listeners.add(l);
    }

    public void unRegisterChatListener(ChatListener l) {
        listeners.remove(l);
    }

    private void notifyListeners(String msg) {
        for (ChatListener l : listeners) {
            l.messageArrived(msg);
        }
    }

    public void connect(String address, int port) throws UnknownHostException, IOException {
        this.port = port;
        serverAddress = InetAddress.getByName(address);
        socket = new Socket(serverAddress, port);
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);  //Set to true, to get auto flush behaviour
    }

    public void send(String msg) {
        output.println(msg);
    }

    
    public void stopp() throws IOException {
        output.println(ProtocolStrings.STOP);
    }


    @Override
    public void run() {
        String msg = input.nextLine();
        while (!msg.equals(ProtocolStrings.STOP)) {
            notifyListeners(msg);
            msg = input.nextLine();
        }

        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {
        int port = 9090;
        String ip = "localhost";
        if (args.length == 2) {
            port = Integer.parseInt(args[0]);
            ip = args[1];
        }
        try {
            
            ChatClient tester = new ChatClient();
            ChatListener listen = new ChatListener() {

                @Override
                public void messageArrived(String data) {
                    System.out.println("Recieved: " + data);
                }
            };
            tester.registerChatListener(listen);
            tester.connect(ip, port);
            tester.start();
            System.out.println("Sending 'Hello world'");
            tester.send("Hello World");
            
            System.out.println("Waiting for a reply");
            
             //Important Blocking call         
            tester.stopp();
            //System.in.read();      
        } catch (UnknownHostException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
