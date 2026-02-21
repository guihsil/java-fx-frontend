package com.freelamarket.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

            if (root.has("id")) user.setProfileId(root.get("id").asText());
            if (root.has("companyName")) user.setCompanyName(root.get("companyName").asText());
            if (root.has("cnpjNif")) user.setCnpjNif(root.get("cnpjNif").asText());
            if (root.has("address")) user.setAddress(root.get("address").asText());
            if (root.has("bio")) user.setBio(root.get("bio").asText());
            if (root.has("portfolioUrl")) user.setPortfolioUrl(root.get("portfolioUrl").asText());
            if (root.has("hourlyRate")) user.setHourlyRate(root.get("hourlyRate").asDouble());
            if (root.has("skills")) user.setSkills(root.get("skills").asText());
        } else {
            user = mapper.treeToValue(root, User.class);
        }

        user.setUserType(userType);
        return user;
    }

    public boolean updateProfile (User user) throws Exception {
        String token = UserSession.getInstance().getToken();
        String endpoint = "CLIENT".equalsIgnoreCase(user.getUserType()) ? "/client" : "/provider";

        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("id", user.getProfileId());

        if ("CLIENT".equalsIgnoreCase(user.getUserType())) {
            rootNode.put("companyName", user.getCompanyName());
            rootNode.put("cnpjNif", user.getCnpjNif());
            rootNode.put("address", user.getAddress());
            rootNode.put("bio", user.getBio());
        } else {
            rootNode.put("bio", user.getBio());
            rootNode.put("portfolioUrl", user.getPortfolioUrl());
            if (user.getHourlyRate() != null) rootNode.put("hourlyRate", user.getHourlyRate());
            rootNode.put("skills", user.getSkills());
        }

        ObjectNode userNode = mapper.createObjectNode();
        userNode.put("id", user.getId());
        userNode.put("name", user.getName());
        userNode.put("email", user.getEmail());
        userNode.put("phone", user.getPhoneNumber());
        rootNode.set("user", userNode);

        String jsonBody = mapper.writeValueAsString(rootNode);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBase + endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return true;
        } else {
            throw new Exception("Erro ao atualizar: " + response.body());
        }
    }
}
