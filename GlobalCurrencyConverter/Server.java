import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class Server {
    static final int PORT = 5500;
    static final String WEB_ROOT = "web";
    static final Map<String, String> MIME = new HashMap<>();
    static {
        MIME.put("html", "text/html; charset=UTF-8");
        MIME.put("css",  "text/css; charset=UTF-8");
        MIME.put("js",   "application/javascript; charset=UTF-8");
        MIME.put("json", "application/json");
        MIME.put("png",  "image/png");
        MIME.put("jpg",  "image/jpeg");
        MIME.put("ico",  "image/x-icon");
        MIME.put("svg",  "image/svg+xml");
        MIME.put("woff2","font/woff2");
    }

    public static void main(String[] args) throws Exception {
        Path root = Path.of(WEB_ROOT).toAbsolutePath();

        if (!Files.exists(root)) {
            System.out.println("ERROR: web/ folder not found at: " + root);
            System.out.println("Make sure you run this from: GlobalCurrencyConverter/");
            return;
        }

        // Create HTTP server bound to all interfaces on PORT
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();

            if (path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }
            Path filePath = root.resolve(path.substring(1)).normalize();
            if (!filePath.startsWith(root)) {
                send(exchange, 403, "text/plain", "403 Forbidden".getBytes());
                return;
            }
            if (Files.isDirectory(filePath)) {
                filePath = filePath.resolve("index.html");
            }

            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                byte[] content = Files.readAllBytes(filePath);
                String ext     = getExtension(filePath.toString());
                String mime    = MIME.getOrDefault(ext, "application/octet-stream");
                send(exchange, 200, mime, content);
            } else {
                String msg = "404 Not Found: " + path;
                send(exchange, 404, "text/plain", msg.getBytes());
            }
        });

        server.setExecutor(null); 
        server.start();

        System.out.println();
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║   Global Currency Converter — Live Server    ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  ✅  Server started on port " + PORT + "            ║");
        System.out.println("║                                              ║");
        System.out.println("║  🌐  http://localhost:" + PORT + "               ║");
        System.out.println("║  🌐  http://localhost:" + PORT + "/login.html    ║");
        System.out.println("║  🌐  http://localhost:" + PORT + "/dashboard.html║");
        System.out.println("║  🌐  http://localhost:" + PORT + "/admin.html    ║");
        System.out.println("║                                              ║");
        System.out.println("║  Press Ctrl+C to stop                        ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Serving files from: " + root);
        System.out.println();
    }

    static void send(HttpExchange ex, int status, String mime, byte[] body) throws IOException {
        ex.getResponseHeaders().set("Content-Type", mime);
        ex.getResponseHeaders().set("Cache-Control", "no-cache");
        ex.sendResponseHeaders(status, body.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(body);
        }
    }

    static String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1).toLowerCase() : "";
    }
}
