package com.freelamarket.controller;

import com.freelamarket.App;
import com.freelamarket.model.ProjectDTO;
import com.freelamarket.model.Proposal;
import com.freelamarket.service.ProposalService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import java.time.LocalDate;

public class ProjectCreateController {

    @FXML private TextField titleField;
    @FXML private TextArea descField;
    @FXML private TextField budgetField;
    @FXML private DatePicker deadlinePicker;
    @FXML private Label msgLabel;

    private final ProposalService service = new ProposalService();
    private String idEditingProject = null;

    @FXML
    public void initialize() {
        final int MAX_CHARS = 15000;

        descField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().length() <= MAX_CHARS ? change : null
        ));
    }

    @FXML
    public void handlePublish() {
        try {
            if (titleField.getText().isEmpty() || descField.getText().isEmpty() || budgetField.getText().isEmpty()) {
                msgLabel.setText("Preencha todos os campos obrigatórios!");
                msgLabel.setStyle("-fx-text-fill: #ef5350;");
                return;
            }

            String title = titleField.getText();
            String desc = descField.getText();
            Double budget = Double.parseDouble(budgetField.getText().replace(",", ".")); // Aceita vírgula
            LocalDate deadline = deadlinePicker.getValue();

            if (deadline == null) {
                msgLabel.setText("Selecione uma data válida!");
                return;
            }

            ProjectDTO novoProjeto = new ProjectDTO(title, desc, budget, deadline);

            try {
                boolean success;
                if(idEditingProject == null) {
                    success = service.createProject(novoProjeto);
                } else {
                    success = service.updateProject(idEditingProject, novoProjeto);
                }

                if(success){
                    voltarParaOFeed();
                }
            } catch (Exception e) {
                msgLabel.setText("Error: "+e.getMessage());
            }

        } catch (NumberFormatException e) {
            msgLabel.setText("O Orçamento deve ser um número (Ex: 500.00)");
        } catch (Exception e) {
            msgLabel.setText("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void voltarParaOFeed() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/feed.fxml"));
            Parent feed = loader.load();
            msgLabel.getScene().getRoot().lookup("#contentArea");
            msgLabel.setText("Projeto Publicado com Sucesso! ✅");
            msgLabel.setStyle("-fx-text-fill: #81c784;");
            titleField.clear();
            descField.clear();
            budgetField.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setProjectData(Proposal p){
        this.idEditingProject = p.getId();
        this.titleField.setText(p.getTitle());
        this.descField.setText(p.getDescription());
        this.budgetField.setText(String.valueOf(p.getValue()));
        if(p.getDeadline() != null) {
            this.deadlinePicker.setValue(p.getDeadline().toLocalDate());
        }
        this.msgLabel.setText("Editando projeto...");
    }
}