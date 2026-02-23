package com.freelamarket.controller;

import com.freelamarket.model.Proposal;
import com.freelamarket.service.ProposalService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
                Label emptyLabel = new Label("Nenhuma Proposta Encontrada 😢");
                emptyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
                cardContainer.getChildren().add(emptyLabel);
            } else {
                for (Proposal p: proposals){
                    cardContainer.getChildren().add(createProposalCard(p));
                }
            }
        });

        task.setOnFailed(event -> {
            Throwable error = task.getException();
            Label lblError = new Label("Erro ao Carregar: "+error.getMessage());
            lblError.getStyleClass().add("text-danger");
            cardContainer.getChildren().add(lblError);
            error.printStackTrace();
        });

        new Thread(task).start();
    }

    private VBox createProposalCard(Proposal p){
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(300);
        card.setMinWidth(300);
        card.getStyleClass().addAll("panel", "panel-default");
        card.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0); -fx-background-color: #2b2b2b; -fx-background-radius: 8;");

        Label lblTitle = new Label(p.getTitle());
        lblTitle.getStyleClass().addAll("h4", "strong");
        lblTitle.setStyle("-fx-text-fill: #81c784;");
        lblTitle.setWrapText(true);

        String publisherName = (p.getClientName() != null) ? p.getClientName() : "Anônimo";
        Label lblClient = new Label("@" + publisherName);
        lblClient.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12px;");

        String descCompleta = p.getDescription();
        if (descCompleta != null && descCompleta.length() > 100) {
            descCompleta = descCompleta.substring(0, 100) + "...";
        }

        Text txtDesc = new Text(descCompleta);
        txtDesc.setWrappingWidth(250);
        txtDesc.setStyle("-fx-fill: white;");

        Button btnView = new Button("Ver Detalhes");
        btnView.getStyleClass().addAll("btn", "btn-sm", "btn-info");
        btnView.setMaxWidth(Double.MAX_VALUE);
        btnView.setOnAction(e -> openDetalWindow(p));

        card.getChildren().addAll(lblTitle, lblClient, txtDesc, btnView);

        return card;
    }

    private void openDetalWindow(Proposal p) {
        try {
            FXMLLoader loader = new FXMLLoader(com.freelamarket.App.class.getResource("view/detail_project.fxml"));
            Parent root = loader.load();

            DetailProjectController controller = loader.getController();
            controller.setProject(p);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Detalhes da Vaga");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}