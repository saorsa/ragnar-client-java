import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AIClientTenantsTest extends MiniHttpServerTestBase {

    @Test
    void list_create_update_delete_tenants() throws Exception {
        register("POST", "/auth/token", 200, "{\"token\":\"test-token\"}", false);

        register("GET", "/tenants", 200, "[{\"id\":\"t1\",\"name\":\"Tenant1\"}]", true);
        register("POST", "/tenants", 200, "{\"id\":\"t2\",\"name\":\"NewTenant\"}", true);
        register("PUT", "/tenants/t2", 200, "{\"id\":\"t2\",\"name\":\"UpdTenant\"}", true);
        register("DELETE", "/tenants/t2", 204, "", true);

        AIClient c = new AIClient(baseUrl);
        c.authenticate("u","p");

        List<Map<String,Object>> tenants = c.getTenants();
        assertEquals("Tenant1", tenants.get(0).get("name"));

        assertEquals("NewTenant", c.createTenant(Map.of("name","NewTenant")).get("name"));
        assertEquals("UpdTenant", c.updateTenant("t2", Map.of("name","UpdTenant")).get("name"));
        assertTrue(c.deleteTenant("t2"));
    }
}
