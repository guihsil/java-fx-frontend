package com.freelamarket.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelamarket.model.User;
import com.freelamarket.util.UserSession;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UserService {

    private final HttpClient client;
    private final ObjectMapper mapper;
    private final String urlBase = "http://localhost:8080";

    public UserService() {
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    public User getUserData() throws Exception{
        String token = UserSession.getInstance().getToken();
        if (token == null) throw new Exception("Usuário não logado!");

        HttpRequest requestClient = createRequest("/client", token);
        HttpResponse<String> responseClient = client.send(requestClient, HttpResponse.BodyHandlers.ofString());

        if (responseClient.statusCode() == 200) {
            return processRes(responseClient.body(), "CLIENT");
        }

        HttpRequest requestProvider = createRequest("/provider", token);
        HttpResponse<String> responseProvider = client.send(requestProvider, HttpResponse.BodyHandlers.ofString());

        if (responseProvider.statusCode() == 200) {
            return processRes(responseProvider.body(), "PROVIDER");
        }

        throw new Exception("Não conseguimos identificar seu perfil (nem Cliente, nem Provider).");
    }

    private HttpRequest createRequest(String endpoint, String token) {
        return HttpRequest.newBuilder()
                .uri(URI.create(urlBase + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
    }

    private User processRes(String jsonBody, String userType) throws Exception {
        JsonNode root = mapper.readTree(jsonBody);
        User user;

        if (root.has("user")) {
            user = mapper.treeToValue(root.get("user"), User.class);
        } else {
            user = mapper.treeToValue(root, User.class);
        }

        user.setUserType(userType);
        return user;
    }

}
