package HTTPServer;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class HTTPServer {

    static int port = 8080;
    static String ip = "127.0.0.1";

    public static void main(String[] args) throws IOException {
        if (args.length >= 2) {
            ip = args[0];
            port = Integer.parseInt(args[1]);
        }
        InetSocketAddress i = new InetSocketAddress(ip, port);
        HttpServer server = HttpServer.create(i, 0);
        server.createContext("/welcome", new WelcomeHandler());
        server.createContext("/headers", new HeadersHandler());
        server.createContext("/pages/", new PagesHandler("public/"));
        server.setExecutor(null);

        server.start();
        System.out.println("The server has started");
    }

    static class HeadersHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
            String response;
            StringBuilder sb = new StringBuilder();
//            Map map = new HashMap();
//            map = he.getRequestHeaders();
//            Iterator<Map.Entry<Integer, Integer>> entries = map.entrySet().iterator();
//            while (entries.hasNext())
//            {
//                Map.Entry<Integer, Integer> entry = entries.next();
//                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
//            }

            sb.append("<!DOCTYPE html>\n");
            sb.append("<html>\n");
            sb.append("<head>\n");
            sb.append("<title>My fancy Web Site</title>\n");
            sb.append("<meta charset='UTF-8'>\n");
            sb.append("</head>\n");
            sb.append("<body>\n");
            sb.append("<h2>Welcome to my very first home made Web Server :-)</h2>\n");
            sb.append("<p> This is the headers page</p>");
            sb.append("<table border='1'>");
            sb.append("<tr><th> Header");
            sb.append("</th>");
            sb.append("<th> Value </th></tr>");
            Iterator it = he.getRequestHeaders().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Integer> m = (Map.Entry) it.next();
                sb.append("<tr><th>" + m.getKey() + "</th><th>" + m.getValue() + "</th></tr>");
            }

            sb.append("</table>");
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

    static class WelcomeHandler implements HttpHandler {

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

}
