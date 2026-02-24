package com.freelamarket.controller;

import com.freelamarket.model.Proposal;
import com.freelamarket.service.ProposalService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.util.List;

public class FeedController {

    @FXML
    private FlowPane cardContainer;

    private final ProposalService service = new ProposalService();

    @FXML
    public void initialize() {
        loaddata();
    }

    public void loaddata(){
        Task<List<Proposal>> task = new Task<>() {
            @Override
            protected List<Proposal> call() throws Exception {
                return service.loadProposals();
            }
        };

        task.setOnSucceeded(event -> {
            List<Proposal> proposals = task.getValue();
            cardContainer.getChildren().clear();

            if (proposals == null || proposals.isEmpty()) {
                Label emptyLabel = new Label("Nenhuma proposta encontrada no momento.");
                emptyLabel.getStyleClass().add("subtitulo");
                cardContainer.getChildren().add(emptyLabel);
            } else {
                for (Proposal p : proposals){
                    cardContainer.getChildren().add(createProposalCard(p));
                }
            }
        });

        task.setOnFailed(event -> {
            Throwable error = task.getException();
            Label lblError = new Label("Erro ao carregar: " + error.getMessage());
            lblError.getStyleClass().add("text-danger");
            cardContainer.getChildren().add(lblError);
            error.printStackTrace();
        });

        new Thread(task).start();
    }

    private VBox createProposalCard(Proposal p){
        VBox card = new VBox(15);
        card.getStyleClass().add("project-card");

        Label lblTitle = new Label(p.getTitle());
        lblTitle.getStyleClass().add("project-card-title");
        lblTitle.setWrapText(true);

        String publisherName = (p.getClientName() != null) ? p.getClientName() : "Anônimo";
        Label lblClient = new Label("@" + publisherName);
        lblClient.getStyleClass().add("subtitulo");
        lblClient.setStyle("-fx-font-size: 12px;");

        String descCompleta = (p.getDescription() != null) ? p.getDescription() : "Sem descrição.";
        if (descCompleta.length() > 100) {
            descCompleta = descCompleta.substring(0, 100) + "...";
        }
        Text txtDesc = new Text(descCompleta);
        txtDesc.getStyleClass().add("project-card-desc");
        txtDesc.setWrappingWidth(280);

        // AJUSTE AQUI: Trocado p.getBudget() por p.getValue() para bater com o seu Model
        Double budgetValue = (p.getValue() != null) ? p.getValue() : 0.0;
        Label lblBudget = new Label("R$ " + String.format("%.2f", budgetValue));
        lblBudget.getStyleClass().add("project-card-budget");

        Button btnView = new Button("Ver Detalhes");
        btnView.getStyleClass().addAll("reg-btn", "reg-btn-success");
        btnView.setMaxWidth(Double.MAX_VALUE);
        btnView.setOnAction(e -> openDetalWindow(p));

        card.getChildren().addAll(lblTitle, lblClient, txtDesc, lblBudget, btnView);

        return card;
    }

    private void openDetalWindow(Proposal p) {
        try {
            FXMLLoader loader = new FXMLLoader(com.freelamarket.App.class.getResource("view/detail_project.fxml"));
            Parent root = loader.load();

            DetailProjectController controller = loader.getController();
            controller.setProject(p);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("FreelaMarket - Detalhes do Projeto");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}