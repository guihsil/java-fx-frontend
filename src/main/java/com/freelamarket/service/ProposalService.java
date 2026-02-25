package com.freelamarket.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.freelamarket.model.Negotiation;
import com.freelamarket.model.ProjectDTO;
import com.freelamarket.model.Proposal;
import com.freelamarket.util.UserSession;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class ProposalService {

    private final HttpClient client;
    private final ObjectMapper mapper;
    private final String BASE_URL = "http://localhost:8080/project";

    public ProposalService() {
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public List<Proposal> loadProposals() throws Exception {
        String token = UserSession.getInstance().getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), new TypeReference<List<Proposal>>(){});
        } else if (response.statusCode() == 404) {
            return Collections.emptyList();
        } else {
            throw new Exception("Erro ao carregar projetos: " + response.statusCode());
        }
    }

    public boolean createProject(ProjectDTO projeto) throws Exception {
        String token = UserSession.getInstance().getToken();

        String jsonBody = mapper.writeValueAsString(projeto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201 || response.statusCode() == 200) {
            return true;
        } else {
            throw new Exception("Erro ao criar projeto: " + response.body());
        }
    }

    public boolean deleteProject(String id) throws Exception {
        String token = UserSession.getInstance().getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200 || response.statusCode() == 204) {
            return true;
        } else {
            throw new Exception("Erro ao deletar: " + response.statusCode());
        }
    }

    public boolean updateProject(String id,  ProjectDTO project) throws Exception {
        String token = UserSession.getInstance().getToken();
        String jsobBody = mapper.writeValueAsString(project);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(jsobBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200 || response.statusCode() == 204) {
            return true;
        } else {
            throw new Exception("Erro ao deletar: " + response.statusCode());
        }
    }

    public boolean sendPropose(String projectId, double valor, String mensagem) throws Exception {
        String token = UserSession.getInstance().getToken();

        com.fasterxml.jackson.databind.node.ObjectNode jsonNodes = mapper.createObjectNode();
        jsonNodes.put("proposedValue", valor);
        jsonNodes.put("message", mensagem);

        String jsonBody = mapper.writeValueAsString(jsonNodes);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/negotiations/" + projectId))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) {
            return true;
        } else {
            throw new Exception("Erro ao enviar proposta. Código: " + response.statusCode() + " | Resposta: " + response.body());
        }
    }

    public List<Negotiation> getProposalsForProject(String projectId) throws Exception {
        String token = UserSession.getInstance().getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/project/" + projectId + "/negotiations"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), new com.fasterxml.jackson.core.type.TypeReference<>() {});
        } else {
            throw new Exception("Erro ao carregar propostas: " + response.body());
        }
    }

    public boolean replyPropose(String negotiationId, boolean aceitar) throws Exception {
        String token = UserSession.getInstance().getToken();
        String endpoint = aceitar ? "/accept" : "/denied";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/negotiations/" + negotiationId + endpoint))
                .header("Authorization", "Bearer " + token)
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 204 || response.statusCode() == 200) {
            return true;
        } else {
            throw new Exception("Erro ao responder: " + response.statusCode());
        }
    }

    public List<Negotiation> getMyProposals() throws Exception {
        String token = UserSession.getInstance().getToken();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/negotiations/me"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), new TypeReference<>() {});
        } else {
            throw new Exception("Erro ao carregar minhas propostas: " + response.body());
        }
    }
}