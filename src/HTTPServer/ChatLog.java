/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package HTTPServer;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ChatLog implements HttpHandler{
    
    private List<String> chatLog;
    public ChatLog() {
        chatLog = new ArrayList();
    }
    

    @Override
    public void handle(HttpExchange he) throws IOException {
       StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append("<title>Chat log</title>\n");
        sb.append("<meta charset='UTF-8'>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        //date origin level message
        sb.append("<table border=\"1\">\n");
        sb.append("<tr><td>Date</td><td>Origin</td><td>Level</td><td>Message</td></tr>\n");
//            for (Map.Entry reqObject : req.entrySet()) {
//                sb.append("<tr><td>");
//                sb.append(reqObject.getKey().toString());
//                sb.append("</td><td>");
//                sb.append(reqObject.getValue().toString());
//                sb.append("</td></tr>\n");
//            }
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
