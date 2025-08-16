import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AIClientMultiTenancyTest extends MiniHttpServerTestBase {

    @Test
    void list_create_update_delete_multitenancy() throws Exception {
        register("POST", "/auth/token", 200, "{\"token\":\"test-token\"}", false);

        register("GET", "/multitenancy", 200, "[{\"id\":\"m1\",\"setting\":\"enabled\"}]", true);
        register("POST", "/multitenancy", 200, "{\"id\":\"m2\",\"setting\":\"strict\"}", true);
        register("PUT", "/multitenancy/m2", 200, "{\"id\":\"m2\",\"setting\":\"relaxed\"}", true);
        register("DELETE", "/multitenancy/m2", 204, "", true);

        AIClient c = new AIClient(baseUrl);
        c.authenticate("u","p");

        List<Map<String,Object>> list = c.getMultiTenancy();
        assertEquals("enabled", list.get(0).get("setting"));

        assertEquals("strict", c.createMultiTenancy(Map.of("setting","strict")).get("setting"));
        assertEquals("relaxed", c.updateMultiTenancy("m2", Map.of("setting","relaxed")).get("setting"));
        assertTrue(c.deleteMultiTenancy("m2"));
    }
}
