import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AIClientTemplatesTest extends MiniHttpServerTestBase {

    @Test
    void get_create_update_delete_templates() throws Exception {
        register("POST", "/auth/token", 200, "{\"token\":\"test-token\"}", false);

        register("GET", "/templates", 200, "[{\"id\":\"1\",\"name\":\"T1\"}]", true);
        register("POST", "/templates", 200, "{\"id\":\"2\",\"name\":\"NewT\"}", true);
        register("PUT", "/templates/2", 200, "{\"id\":\"2\",\"name\":\"UpdT\"}", true);
        register("DELETE", "/templates/2", 204, "", true);

        AIClient c = new AIClient(baseUrl);
        c.authenticate("u","p");

        List<Map<String,Object>> ts = c.getTemplates(null);
        assertEquals("T1", ts.get(0).get("name"));

        assertEquals("NewT", c.createTemplate(Map.of("name","NewT")).get("name"));
        assertEquals("UpdT", c.updateTemplate("2", Map.of("name","UpdT")).get("name"));
        assertTrue(c.deleteTemplate("2"));
    }
}
