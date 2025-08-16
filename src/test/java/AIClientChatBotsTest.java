import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AIClientChatBotsTest extends MiniHttpServerTestBase {


    @Test
    void get_create_update_delete_chatbot() throws Exception {
        register("POST", "/auth/token", 200, "{\"token\":\"test-token\"}", false);

        register("GET", "/chatbots", 200, "[{\"id\":\"1\",\"name\":\"Bot1\"}]", true);
        register("POST", "/chatbots", 200, "{\"id\":\"2\",\"name\":\"NewBot\"}", true);
        register("PUT", "/chatbots/2", 200, "{\"id\":\"2\",\"name\":\"UpdatedBot\"}", true);
        register("DELETE", "/chatbots/2", 204, "", true);

        AIClient c = new AIClient(baseUrl);
        c.authenticate("u","p");

        List<Map<String,Object>> bots = c.getChatBots();
        assertEquals("Bot1", bots.get(0).get("name"));

        Map<String,Object> created = c.createChatBot(Map.of("name","NewBot"));
        assertEquals("NewBot", created.get("name"));

        Map<String,Object> updated = c.updateChatBot("2", Map.of("name","UpdatedBot"));
        assertEquals("UpdatedBot", updated.get("name"));

        assertTrue(c.deleteChatBot("2"));
    }
}
