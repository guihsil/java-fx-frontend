package com.freelamarket.controller;

import com.freelamarket.App;
import com.freelamarket.Launcher;
import com.freelamarket.model.Proposal;
import com.freelamarket.service.ProposalService;
import com.freelamarket.util.UserSession;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MyProjectsController {

    @FXML private FlowPane cardContainer;
    private final ProposalService service = new ProposalService();

    @FXML
    public void initialize() {
        loadMyProjects();
    }

    private void loadMyProjects() {
        String myEmail = UserSession.getInstance().getEmail();

        Task<List<Proposal>> task = new Task<>() {
            @Override
            protected List<Proposal> call() throws Exception {
                List<Proposal> allProjects = service.loadProposals();
                return allProjects.stream()
                        .filter(p -> p.getClientEmail() != null && p.getClientEmail().equals(myEmail))
                        .collect(Collectors.toList());
            }
        };

        task.setOnSucceeded(e -> {
            cardContainer.getChildren().clear();
            List<Proposal> mine = task.getValue();

            if(mine.isEmpty()) {
                Label lbl = new Label("Você não criou nenhum projeto.");
                lbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                cardContainer.getChildren().add(lbl);
            } else {
                for(Proposal p: mine) {
                    cardContainer.getChildren().add(createCardAdmin(p));
                }
            }
        });

        task.setOnFailed(e -> {
            e.getSource().getException().printStackTrace();
            Label lbl = new Label("Erro ao carregar projetos.");
            lbl.setStyle("-fx-text-fill: #ef5350;");
            cardContainer.getChildren().add(lbl);
        });

        new Thread(task).start();
    }

    private VBox createCardAdmin(Proposal p) {
        VBox card = new VBox(10);
        card.setPadding(new Insets((15)));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: #2b2b2b; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");

        Label title = new Label(p.getTitle());
        title.setStyle("-fx-text-fill: #81c784; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label status = new Label("Status: " + p.getStatus());
        status.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12px;");

        HBox actions = new HBox(10);
        Button btnEdit = new Button("✏️ Editar");
        btnEdit.getStyleClass().addAll("btn", "btn-sm", "btn-warning");
        btnEdit.setOnAction(e -> handleEdit(p));

        Button btnDelete = new Button("🗑️ Excluir");
        btnDelete.getStyleClass().addAll("btn", "btn-sm", "btn-danger");
        btnDelete.setOnAction(e -> handleDelete(p));

        actions.getChildren().addAll(btnEdit, btnDelete);
        card.getChildren().addAll(title, status, actions);
        return card;
    }

    private void handleDelete(Proposal p) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Excluir Projeto");
        alert.setHeaderText("Tem certeza que deseja apagar '" + p.getTitle() + "'?");
        alert.setContentText("Essa ação não pode ser desfeita.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (service.deleteProject(p.getId())) {
                    loadMyProjects();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setContentText("Erro ao excluir: " + e.getMessage());
                erro.show();
            }
        }
    }

    private void handleEdit(Proposal p) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/project_create.fxml"));
            Parent root = loader.load();

            ProjectCreateController controller = loader.getController();
            controller.setProjectData(p);

            cardContainer.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
