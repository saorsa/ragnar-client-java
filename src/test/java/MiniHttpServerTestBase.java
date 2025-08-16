import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public abstract class MiniHttpServerTestBase {

    protected static class ResponseSpec {
        final int status;
        final String body;
        final boolean requireAuth;

        ResponseSpec(int status, String body, boolean requireAuth) {
            this.status = status;
            this.body = body;
            this.requireAuth = requireAuth;
        }
    }

    protected HttpServer server;
    protected String baseUrl;

    private final Map<String, ResponseSpec> routes = new ConcurrentHashMap<>();

    protected void register(String method, String path, int status, String body, boolean requireAuth) {
        routes.put(method.toUpperCase() + " " + path, new ResponseSpec(status, body, requireAuth));
    }

    @BeforeEach
    void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.setExecutor(Executors.newCachedThreadPool());

        server.createContext("/", ex -> {
            String key = ex.getRequestMethod().toUpperCase() + " " + ex.getRequestURI().getPath();

            ResponseSpec spec = routes.get(key);
            if (spec == null) {
                String msg = "{\"error\":\"no route for " + key + "\"}";
                ex.sendResponseHeaders(404, msg.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream os = ex.getResponseBody()) { os.write(msg.getBytes(StandardCharsets.UTF_8)); }
                return;
            }

            String contentType = ex.getRequestHeaders().getFirst("Content-Type");
            if (!"POST".equals(ex.getRequestMethod()) && !"PUT".equals(ex.getRequestMethod())) {
                // for GET/DELETE we don't require a body, but Content-Type is still set by AIClient
                if (contentType == null || !contentType.contains("application/json")) {
                    // Let it slide for GET/DELETE if null
                }
            }

            if (spec.requireAuth) {
                String auth = ex.getRequestHeaders().getFirst("Authorization");
                if (auth == null || !auth.equals("Bearer test-token")) {
                    String msg = "{\"error\":\"missing or bad auth\"}";
                    ex.sendResponseHeaders(401, msg.getBytes(StandardCharsets.UTF_8).length);
                    try (OutputStream os = ex.getResponseBody()) { os.write(msg.getBytes(StandardCharsets.UTF_8)); }
                    return;
                }
            }

            byte[] bytes = spec.body == null ? new byte[0] : spec.body.getBytes(StandardCharsets.UTF_8);
            ex.sendResponseHeaders(spec.status, bytes.length);
            try (OutputStream os = ex.getResponseBody()) {
                if (bytes.length > 0) os.write(bytes);
            }
        });

        server.start();
        int port = server.getAddress().getPort();
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void stop() {
        if (server != null) server.stop(0);
        routes.clear();
    }
}

