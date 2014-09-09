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
import java.util.Properties;
import utils.Utils;

public class ChatClient extends Thread {

    private Socket socket;
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
        System.out.println(msg);
        String[] protocolCrap = msg.split("#");
        if (protocolCrap[0].equals("ONLINE")) {
            for (ChatListener l : listeners) {
                l.userListArrived(protocolCrap[1].split(","));
            }
        } else if (protocolCrap[0].equals("MESSAGE") && protocolCrap.length == 3) {
           for (ChatListener l : listeners) {
                l.messageArrived(protocolCrap[1], protocolCrap[2]);
            }
        }
    }

    public void connect(String address, int port) throws UnknownHostException, IOException {
        socket = new Socket(InetAddress.getByName(address), port);
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);  //Set to true, to get auto flush behaviour
    }

    public void send(String msg) {
        output.println(msg);
    }

    public void stopClient() throws IOException {
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

    }
}
