package entities;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class RagnarWrapper {

    private final String baseUrl;
    private final HttpClient http;
    private final ObjectMapper json;
    private String authToken;

    public RagnarWrapper(String baseUrl, HttpClient http, ObjectMapper json, String authToken) {
        this.baseUrl = baseUrl;
        this.http = http;
        this.json = json;
        this.authToken = authToken;
    }

    public void authenticate(String username, String password) throws IOException, InterruptedException {

        Map<String, String> credentials = Map.of("username", username, "password", password);

        String body = json.writeValueAsString(credentials);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/auth/token"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Authentication failed: " + response.body());
        }

        this.authToken = json.readTree(response.body()).get("access_token").asText();
    }

    public String getAuthToken() {
        return authToken;
    }

    public HttpRequest.Builder authBuilder(String path) {
        checkAuthToken();

        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json");
    }

    public void checkAuthToken() {
        if (authToken == null) {
            throw new RuntimeException("Auth token is null");
        }
    }

    public List<Map<String, Object>> getChatBots() throws IOException, InterruptedException {

        HttpRequest req = authBuilder("/chatbots").GET().build();

        HttpResponse<String> response = http.send(req, HttpResponse.BodyHandlers.ofString());

        return json.readValue(response.body(),
                json.getTypeFactory().constructCollectionType(List.class, Map.class));
    }

    public Map<String, Object> updateChatBot(String id, Map<String, Object> payload) throws IOException, InterruptedException {
        String body = json.writeValueAsString(payload);
        HttpRequest req = authBuilder("/chatbots/" + id)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), Map.class);
    }

    public boolean deleteChatBot(String id) throws IOException, InterruptedException {
        HttpRequest req = authBuilder("/chatbots/" + id)
                .DELETE()
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return res.statusCode() == 204;
    }

    public List<Map<String, Object>> getChats() throws IOException, InterruptedException {
        HttpRequest req = authBuilder("/chats").GET().build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), json.getTypeFactory().constructCollectionType(List.class, Map.class));
    }

    public Map<String, Object> createChat(Map<String, Object> payload) throws IOException, InterruptedException {
        String body = json.writeValueAsString(payload);
        HttpRequest req = authBuilder("/chats")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), Map.class);
    }

    public Map<String, Object> updateChat(String id, Map<String, Object> payload) throws IOException, InterruptedException {
        String body = json.writeValueAsString(payload);
        HttpRequest req = authBuilder("/chats/" + id)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), Map.class);
    }

    public boolean deleteChat(String id) throws IOException, InterruptedException {
        HttpRequest req = authBuilder("/chats/" + id)
                .DELETE()
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return res.statusCode() == 204;
    }

    public Map<String, Object> createChatBot(Map<String, Object> payload) throws IOException, InterruptedException {

        String body = json.writeValueAsString(payload);

        HttpRequest req = authBuilder("/chatbots")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());

        return json.readValue(res.body(), Map.class);
    }

    public List<Map<String, Object>> getTemplates(String path) throws IOException, InterruptedException {

        HttpRequest req = authBuilder("/templates").GET().build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());

        return json.readValue(res.body(), json.getTypeFactory().constructCollectionType(List.class, Map.class));
    }

    public Map<String, Object> createTemplate(Map<String, Object> payload) throws IOException, InterruptedException {
        String body = json.writeValueAsString(payload);
        HttpRequest req = authBuilder("/templates")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), Map.class);
    }

    public Map<String, Object> updateTemplate(String id, Map<String, Object> payload) throws IOException, InterruptedException {
        String body = json.writeValueAsString(payload);
        HttpRequest req = authBuilder("/templates/" + id)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), Map.class);
    }

    public boolean deleteTemplate(String id) throws IOException, InterruptedException {
        HttpRequest req = authBuilder("/templates/" + id)
                .DELETE()
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return res.statusCode() == 204;
    }

    public Map<String, Object> createSession(Map<String, Object> payload) throws IOException, InterruptedException {

        String body = json.writeValueAsString(payload);

        HttpRequest req = authBuilder("/sessions")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();

        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());

        return json.readValue(res.body(), Map.class);
    }

    public Map<String, Object> getSession(String sessionId) throws IOException, InterruptedException {
        HttpRequest req = authBuilder("/sessions/" + sessionId).GET().build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) {
            throw new RuntimeException("Failed to get session: " + res.body());
        }
        return json.readValue(res.body(), Map.class);
    }


    public Map<String, Object> updateSession(String id, Map<String, Object> payload) throws IOException, InterruptedException {
        String body = json.writeValueAsString(payload);
        HttpRequest req = authBuilder("/sessions/" + id)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), Map.class);
    }

    public boolean deleteSession(String id) throws IOException, InterruptedException {
        HttpRequest req = authBuilder("/sessions/" + id)
                .DELETE()
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return res.statusCode() == 204;
    }

    public List<Map<String, Object>> getAssistants() throws IOException, InterruptedException {
        HttpRequest req = authBuilder("/assistants").GET().build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), json.getTypeFactory().constructCollectionType(List.class, Map.class));
    }

    public Map<String, Object> createAssistant(Map<String, Object> payload) throws IOException, InterruptedException {
        String body = json.writeValueAsString(payload);
        HttpRequest req = authBuilder("/assistants")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), Map.class);
    }

    public Map<String, Object> updateAssistant(String id, Map<String, Object> payload) throws IOException, InterruptedException {
        String body = json.writeValueAsString(payload);
        HttpRequest req = authBuilder("/assistants/" + id)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), Map.class);
    }

    public boolean deleteAssistant(String id) throws IOException, InterruptedException {
        HttpRequest req = authBuilder("/assistants/" + id)
                .DELETE()
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return res.statusCode() == 204;  // Обикновено за успешен delete връща 204 No Content
    }

    public boolean validateToken() throws IOException, InterruptedException {
        HttpRequest req = authBuilder("/auth/validate").GET().build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return res.statusCode() == 200;
    }

    public List<Map<String, Object>> getUserClaims() throws IOException, InterruptedException {
        HttpRequest req = authBuilder("/userclaims").GET().build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), json.getTypeFactory().constructCollectionType(List.class, Map.class));
    }

    public Map<String, Object> createUserClaim(Map<String, Object> payload) throws IOException, InterruptedException {
        String body = json.writeValueAsString(payload);
        HttpRequest req = authBuilder("/userclaims")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), Map.class);
    }

    public Map<String, Object> updateUserClaim(String id, Map<String, Object> payload) throws IOException, InterruptedException {
        String body = json.writeValueAsString(payload);
        HttpRequest req = authBuilder("/userclaims/" + id)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), Map.class);
    }

    public boolean deleteUserClaim(String id) throws IOException, InterruptedException {
        HttpRequest req = authBuilder("/userclaims/" + id)
                .DELETE()
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return res.statusCode() == 204;
    }

    public List<Map<String, Object>> getUsers() throws IOException, InterruptedException {
        HttpRequest req = authBuilder("/users").GET().build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), json.getTypeFactory().constructCollectionType(List.class, Map.class));
    }

    public Map<String, Object> createUser(Map<String, Object> payload) throws IOException, InterruptedException {
        String body = json.writeValueAsString(payload);
        HttpRequest req = authBuilder("/users")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), Map.class);
    }

    public Map<String, Object> updateUser(String id, Map<String, Object> payload) throws IOException, InterruptedException {
        String body = json.writeValueAsString(payload);
        HttpRequest req = authBuilder("/users/" + id)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), Map.class);
    }

    public boolean deleteUser(String id) throws IOException, InterruptedException {
        HttpRequest req = authBuilder("/users/" + id)
                .DELETE()
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return res.statusCode() == 204;
    }

    public List<Map<String, Object>> getTenants() throws IOException, InterruptedException {
        HttpRequest req = authBuilder("/tenants").GET().build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), json.getTypeFactory().constructCollectionType(List.class, Map.class));
    }

    public Map<String, Object> createTenant(Map<String, Object> payload) throws IOException, InterruptedException {
        String body = json.writeValueAsString(payload);
        HttpRequest req = authBuilder("/tenants")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), Map.class);
    }

    public Map<String, Object> updateTenant(String id, Map<String, Object> payload) throws IOException, InterruptedException {
        String body = json.writeValueAsString(payload);
        HttpRequest req = authBuilder("/tenants/" + id)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), Map.class);
    }

    public boolean deleteTenant(String id) throws IOException, InterruptedException {
        HttpRequest req = authBuilder("/tenants/" + id)
                .DELETE()
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return res.statusCode() == 204;
    }

    public List<Map<String, Object>> getMultiTenancy() throws IOException, InterruptedException {
        HttpRequest req = authBuilder("/multitenancy").GET().build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), json.getTypeFactory().constructCollectionType(List.class, Map.class));
    }

    public Map<String, Object> createMultiTenancy(Map<String, Object> payload) throws IOException, InterruptedException {
        String body = json.writeValueAsString(payload);
        HttpRequest req = authBuilder("/multitenancy")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), Map.class);
    }

    public Map<String, Object> updateMultiTenancy(String id, Map<String, Object> payload) throws IOException, InterruptedException {
        String body = json.writeValueAsString(payload);
        HttpRequest req = authBuilder("/multitenancy/" + id)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return json.readValue(res.body(), Map.class);
    }

    public boolean deleteMultiTenancy(String id) throws IOException, InterruptedException {
        HttpRequest req = authBuilder("/multitenancy/" + id)
                .DELETE()
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        return res.statusCode() == 204;
    }
}