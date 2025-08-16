import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AIClientAssistantsTest extends MiniHttpServerTestBase {

    @Test
    void list_create_update_delete_assistants() throws Exception {
        register("POST", "/auth/token", 200, "{\"token\":\"test-token\"}", false);

        register("GET", "/assistants", 200, "[{\"id\":\"a1\",\"name\":\"Asst1\"}]", true);
        register("POST", "/assistants", 200, "{\"id\":\"a2\",\"name\":\"NewA\"}", true);
        register("PUT", "/assistants/a2", 200, "{\"id\":\"a2\",\"name\":\"UpdA\"}", true);
        register("DELETE", "/assistants/a2", 204, "", true);

        AIClient c = new AIClient(baseUrl);
        c.authenticate("u","p");

        List<Map<String,Object>> list = c.getAssistants();
        assertEquals("Asst1", list.get(0).get("name"));

        assertEquals("NewA", c.createAssistant(Map.of("name","NewA")).get("name"));
        assertEquals("UpdA", c.updateAssistant("a2", Map.of("name","UpdA")).get("name"));
        assertTrue(c.deleteAssistant("a2"));
    }
}
