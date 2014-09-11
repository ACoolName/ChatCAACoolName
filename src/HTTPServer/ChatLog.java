/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package HTTPServer;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChatLog implements HttpHandler{
    
    private List<String> chatLog;

    public ChatLog() {
        chatLog = new ArrayList();
        
    }
    
    private void readLogFile() throws FileNotFoundException{
        chatLog.clear();
        File file = new File("chatLog.txt");
        if (!file.exists()) {
            throw new FileNotFoundException("Log file not found. Aborting.");
        }
        if (!file.canRead()) {
            throw new FileNotFoundException("Log file cannot be read. Aborting.");
        }
        Scanner sr = new Scanner(new FileReader(file));
        while(sr.hasNextLine()) {
            chatLog.add(sr.nextLine());
        }
        sr.close();
    }
    

    @Override
    public void handle(HttpExchange he) throws IOException {
       readLogFile();
       StringBuilder sb = new StringBuilder();
       String[] arr;
       boolean second = false;
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append("<title>Chat log</title>\n");
        sb.append("<meta charset='UTF-8'>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        //date origin level message
        //Sep 11, 2014 11:18:59 AM ChatServer.ChatServer INFO: Sever started
        for (String line : chatLog) {
            sb.append(line + "<br/>");
        }
        sb.append("</table>\n");
        sb.append("</body>\n");
        sb.append("</html>\n");
        String response = sb.toString();
        Headers h = he.getResponseHeaders();
        h.add("Content-Type", "text/html");
        he.sendResponseHeaders(200, response.length());
        try (PrintWriter pw = new PrintWriter(he.getResponseBody())) {
            pw.printf(response);
        }
    }
    
}
