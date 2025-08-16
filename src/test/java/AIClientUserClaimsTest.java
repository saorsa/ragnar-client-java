import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AIClientUserClaimsTest extends MiniHttpServerTestBase {

    @Test
    void list_create_update_delete_userclaims() throws Exception {
        register("POST", "/auth/token", 200, "{\"token\":\"test-token\"}", false);

        register("GET", "/userclaims", 200, "[{\"id\":\"c1\",\"claim\":\"role:user\"}]", true);
        register("POST", "/userclaims", 200, "{\"id\":\"c2\",\"claim\":\"role:admin\"}", true);
        register("PUT", "/userclaims/c2", 200, "{\"id\":\"c2\",\"claim\":\"role:editor\"}", true);
        register("DELETE", "/userclaims/c2", 204, "", true);

        AIClient c = new AIClient(baseUrl);
        c.authenticate("u","p");

        List<Map<String,Object>> list = c.getUserClaims();
        assertEquals("role:user", list.get(0).get("claim"));

        assertEquals("role:admin", c.createUserClaim(Map.of("claim","role:admin")).get("claim"));
        assertEquals("role:editor", c.updateUserClaim("c2", Map.of("claim","role:editor")).get("claim"));
        assertTrue(c.deleteUserClaim("c2"));
    }
}
