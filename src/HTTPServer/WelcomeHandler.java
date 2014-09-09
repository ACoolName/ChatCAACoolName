package HTTPServer;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.PrintWriter;

public class WelcomeHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange he) throws IOException {
        String response;
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append("<title>My fancy Web Site</title>\n");
        sb.append("<meta charset='UTF-8'>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        sb.append("<h2>Welcome to my very first home made Web Server :-)</h2>\n");
        sb.append("<a href='/headers'> Go to headers page</a>");
        sb.append("</body>\n");
        sb.append("</html>\n");
        response = sb.toString();
        Headers h = he.getResponseHeaders();
        h.add("Content-Type", "text/html");

        he.sendResponseHeaders(200, response.length());
        try (PrintWriter pw = new PrintWriter(he.getResponseBody())) {
            pw.print(response);
        }

    }

}
