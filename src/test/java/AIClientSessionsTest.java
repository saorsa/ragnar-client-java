import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AIClientSessionsTest extends MiniHttpServerTestBase {

    @Test
    void create_get_update_delete_session() throws Exception {
        register("POST", "/auth/token", 200, "{\"token\":\"test-token\"}", false);

        register("POST", "/sessions", 200, "{\"id\":\"s1\",\"status\":\"active\"}", true);
        register("GET", "/sessions/s1", 200, "{\"id\":\"s1\",\"status\":\"active\"}", true);
        register("PUT", "/sessions/s1", 200, "{\"id\":\"s1\",\"status\":\"paused\"}", true);
        register("DELETE", "/sessions/s1", 204, "", true);

        AIClient c = new AIClient(baseUrl);
        c.authenticate("u","p");

        Map<String,Object> created = c.createSession(Map.of("foo","bar"));
        assertEquals("active", created.get("status"));

        assertEquals("active", c.getSession("s1").get("status"));
        assertEquals("paused", c.updateSession("s1", Map.of("status","paused")).get("status"));
        assertTrue(c.deleteSession("s1"));
    }

    @Test
    void getSession_non200_throws() throws Exception {
        register("POST", "/auth/token", 200, "{\"token\":\"test-token\"}", false);
        register("GET", "/sessions/bad", 404, "{\"error\":\"missing\"}", true);

        AIClient c = new AIClient(baseUrl);
        c.authenticate("u","p");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> c.getSession("bad"));
        assertTrue(ex.getMessage().contains("Failed to get session"));
    }
}
