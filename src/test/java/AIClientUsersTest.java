import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AIClientUsersTest extends MiniHttpServerTestBase {


    @Test
    void list_create_update_delete_users() throws Exception {
        register("POST", "/auth/token", 200, "{\"token\":\"test-token\"}", false);

        register("GET", "/users", 200, "[{\"id\":\"u1\",\"username\":\"john\"}]", true);
        register("POST", "/users", 200, "{\"id\":\"u2\",\"username\":\"new\"}", true);
        register("PUT", "/users/u2", 200, "{\"id\":\"u2\",\"username\":\"upd\"}", true);
        register("DELETE", "/users/u2", 204, "", true);

        AIClient c = new AIClient(baseUrl);
        c.authenticate("u","p");

        List<Map<String,Object>> users = c.getUsers();
        assertEquals("john", users.get(0).get("username"));

        assertEquals("new", c.createUser(Map.of("username","new")).get("username"));
        assertEquals("upd", c.updateUser("u2", Map.of("username","upd")).get("username"));
        assertTrue(c.deleteUser("u2"));
    }
}
