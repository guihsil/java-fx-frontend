package com.freelamarket.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelamarket.model.User;
import com.freelamarket.util.UserSession;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String url = "http://localhost:8080/auth";

    public boolean login(String email, String password) throws Exception {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("login", email);
        loginData.put("password", password);

        String jsonBody = mapper.writeValueAsString(loginData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String token = response.body();
            UserSession.getInstance().setToken(token);
            UserSession.getInstance().setEmail(email);
            return true;
        } else {
            throw new Exception("Informações incorretas");
        }
    }

    public boolean register(String name, String email, String password, String phone, String userType) throws Exception {
        Map<String, Object> registerData = new HashMap<>();
        registerData.put("name", name);
        registerData.put("email", email);
        registerData.put("password", password);
        registerData.put("phone", phone);
        registerData.put("userType", userType);

        String jsonBody = mapper.writeValueAsString(registerData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200) {
            String token = response.body();
            UserSession.getInstance().setToken(token);
            UserSession.getInstance().setEmail(email);
            return true;
        } else {
            throw new Exception("Falha no login");
        }
    }

}
