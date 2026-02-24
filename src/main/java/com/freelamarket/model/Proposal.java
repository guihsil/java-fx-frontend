package com.freelamarket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignora campos que sobrarem (segurança)
public class Proposal {

    private String id; // O Jackson converte o UUID do backend para String automaticamente

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("budget") // Mapeia o campo 'budget' do DTO para a variável 'value'
    private Double value;

    @JsonProperty("status") // Recebe o Enum como String
    private String status;

    @JsonProperty("deadline")
    private LocalDateTime deadline;

    @JsonProperty("clientName") // AQUI ESTAVA O ERRO: Agora bate com o DTO
    private String clientName;

    // Construtor vazio (obrigatório para o Jackson)
    public Proposal() {}

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}