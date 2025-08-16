import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AIClientChatsTest extends MiniHttpServerTestBase {

    @Test
    void get_create_update_delete_chats() throws Exception {
        register("POST", "/auth/token", 200, "{\"token\":\"test-token\"}", false);

        register("GET", "/chats", 200, "[{\"id\":\"1\",\"title\":\"Chat1\"}]", true);
        register("POST", "/chats", 200, "{\"id\":\"2\",\"title\":\"NewChat\"}", true);
        register("PUT", "/chats/2", 200, "{\"id\":\"2\",\"title\":\"Renamed\"}", true);
        register("DELETE", "/chats/2", 204, "", true);

        AIClient c = new AIClient(baseUrl);
        c.authenticate("u","p");

        List<Map<String,Object>> chats = c.getChats();
        assertEquals("Chat1", chats.get(0).get("title"));

        Map<String,Object> created = c.createChat(Map.of("title","NewChat"));
        assertEquals("NewChat", created.get("title"));

        Map<String,Object> updated = c.updateChat("2", Map.of("title","Renamed"));
        assertEquals("Renamed", updated.get("title"));

        assertTrue(c.deleteChat("2"));
    }
}
