import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.*;

public class AIClientAccessTokenTest {

    private AIClient aiClient;

    @BeforeEach
    void setup() {
        aiClient = new AIClient(
                "https://api.ragnar.saorsa.bg");

    }

    @Test
    void authenticate_shouldReturnAccessToken() {
        String username = "adautev@gmail.com";
        String password = "lkfdjkj!2##!";

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
