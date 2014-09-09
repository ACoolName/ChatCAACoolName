package ChatServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import Shared.ProtocolStrings;

public class ClientHandler extends Thread
{

    private Scanner input;
    private PrintWriter writer;
    private Socket socket;
    private ChatServer server;
    private String nickName;

    public String getNickName()
    {
        return nickName;
    }

    public ClientHandler(Socket socket, ChatServer server) throws IOException
    {
        input = new Scanner(socket.getInputStream());
        writer = new PrintWriter(socket.getOutputStream(), true);
        this.socket = socket;
        this.server = server;

    }

    public void send(String message)
    {
        writer.println(ProtocolStrings.MESSAGE + nickName + "#" + message);
        Logger.getLogger(ChatServer.class.getName()).log(Level.INFO,
                String.format("Received the message: %1$S ",
                        message.toUpperCase()));
    }
    
    public void sendOnlineUsers(String name)
    {
        writer.println(ProtocolStrings.ONLINE+name);
    }

    public void run()
    {
        try
        {
            String message = input.nextLine(); //IMPORTANT blocking call
           
            //connect[0]= connect[0]+"#";
            if (message.length()>8 && message.substring(0,8).equals(ProtocolStrings.CONNECT))
            {
                String name = message.substring(8);
                nickName = name;
                server.addClientHandler(name, this);
                message = input.nextLine();
                while (!message.equals(ProtocolStrings.STOP))
                {
                    String[] send = message.split("#");
                    if (send[1].equals("*"))
                    {
                        server.sendAll(send[2], this);
                    } else
                    {
                        String[] names = send[1].split(",");
                        server.send(send[2], names);
                    }
                    //Logger.getLogger(ChatServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message));
                    message = input.nextLine(); //IMPORTANT blocking call
                }
            }

            writer.println(ProtocolStrings.STOP);//Chat the stop message back to the client for a nice closedown
            try
            {
                socket.close();
            } catch (IOException ex)
            {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchElementException ex)
        {
            //Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally
        {
            if(nickName!=null)
            server.removeHandler(this.nickName);
        }
        Logger.getLogger(ChatServer.class.getName()).log(Level.INFO, "Closed a Connection");
    }
}