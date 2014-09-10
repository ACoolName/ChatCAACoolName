package HTTPServer;

import ChatServer.ChatServer;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class OnlineUsers implements HttpHandler {

    private ChatServer cs;
    
    public OnlineUsers(ChatServer cs) {
        this.cs = cs;
    }

    @Override
    public void handle(HttpExchange he) throws IOException {
        int onlineUsers = cs.getOnlineUsers().size();
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append("<title>OnlineUsers</title>\n");
        sb.append("<meta charset='UTF-8'>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        sb.append("<h2>Number of online users:" + onlineUsers + "</h2>\n");
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
