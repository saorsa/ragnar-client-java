import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AIClientAccessTokenTest {

    private AIClient aiClient;
    private String username;
    private String password;

    @BeforeEach
    void setup() {
        Dotenv dotenv = Dotenv.load();

        String baseUrl = dotenv.get("RAGNAR_URL");
        username = dotenv.get("RAGNAR_USERNAME");
        password = dotenv.get("RAGNAR_PASSWORD");

        aiClient = new AIClient(
                baseUrl);

    }

    @Test
    void authenticate_shouldReturnAccessToken() {
        try {
            aiClient.authenticate(username, password);
            String accessToken = aiClient.getAuthToken();

            assertNotNull(accessToken, "Token should not be null");
            assertFalse(accessToken.isBlank(), "Token should not be blank");

            System.out.println("Access Token: " + accessToken);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Authentication failed: " + e.getMessage());
        }
    }
}
