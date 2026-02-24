package com.freelamarket.controller;

import com.freelamarket.App;
import com.freelamarket.model.Negotiation;
import com.freelamarket.model.Proposal;
import com.freelamarket.model.User;
import com.freelamarket.service.ProposalService;
import com.freelamarket.service.UserService;
import com.freelamarket.util.UserSession;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MyProjectsController {

    @FXML private FlowPane cardContainer;
    private final ProposalService service = new ProposalService();

    @FXML
    public void initialize() {
        try {
            User currentUser = new UserService().getUserData();
            if ("provider".equalsIgnoreCase(currentUser.getUserType())){
                loadProviderProposals();
            } else {
                loadClientProjects();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadClientProjects() {
        // Passo 1: Não usamos mais o email da sessão direto, pois precisamos comparar Nomes

        Task<List<Proposal>> task = new Task<>() {
            @Override
            protected List<Proposal> call() throws Exception {
                // A. Pegamos os dados do usuário logado para saber o nome dele
                User currentUser = new UserService().getUserData();
                String myName = currentUser.getName();

                // B. Carregamos todos os projetos
                List<Proposal> allProjects = service.loadProposals();

                // C. CORREÇÃO: Filtramos pelo clientName (único dado que temos agora)
                return allProjects.stream()
                        .filter(p -> p.getClientName() != null && p.getClientName().equalsIgnoreCase(myName))
                        .collect(Collectors.toList());
            }
        };

        task.setOnSucceeded(e -> {
            cardContainer.getChildren().clear();
            List<Proposal> mine = task.getValue();

            if(mine.isEmpty()) {
                Label lbl = new Label("Você não criou nenhum projeto.");
                lbl.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 14px;"); // Cinza claro
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

    private void loadProviderProposals() {
        Task<List<Negotiation>> task = new Task<>() {
            @Override
            protected List<Negotiation> call() throws Exception {
                return service.getMyProposals();
            }
        };

        task.setOnSucceeded(e -> {
            cardContainer.getChildren().clear();
            List<Negotiation> myProposals = task.getValue();

            if (myProposals == null || myProposals.isEmpty()){
                Label lbl = new Label("Você ainda não enviou propostas para nenhum projeto.");
                lbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px");
                cardContainer.getChildren().add(lbl);
            } else {
                for(Negotiation n: myProposals) {
                    cardContainer.getChildren().add(createCardProvider(n));
                }
            }
        });

        task.setOnFailed(e -> {
            Label lbl = new Label("Erro ao carregar suas propostas.");
            lbl.setStyle("-fx-text-fill: #ef5350;");
            cardContainer.getChildren().add(lbl);
        });

        new Thread(task).start();
    }

    private VBox createCardProvider(Negotiation n){
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(220);
        card.setStyle("-fx-background-color: #2b2b2b; " +
                "-fx-background-radius: 8; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");

        Label title = new Label("Vaga: " + n.getProjectTitle());
        title.setStyle("-fx-text-fill: #64b5f6; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14px;");

        Label charged = new Label("💰 Seu lance: R$ " + n.getProposedValue());
        charged.setStyle("-fx-text-fill: white;");

        Label status = new Label("Status: " + n.getStatus());

        if("ACCEPTED".equalsIgnoreCase(n.getStatus())) {
            status.setStyle("-fx-text-fill: #81c784; " +
                    "-fx-font-weight: bold;");
        } else if ("REJECTED".equalsIgnoreCase(n.getStatus())) {
            status.setStyle("-fx-text-fill: #ef5350; " +
                    "-fx-font-weight: bold;");
        } else {
            status.setStyle("-fx-text-fill: #ffb74d; " +
                    "-fx-font-style: italic;");
        }

        card.getChildren().addAll(title, charged, status);
        return card;
    }

    private VBox createCardAdmin(Proposal p) {
        VBox card = new VBox(10);
        card.setPadding(new Insets((15)));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: #2b2b2b; " +
                "-fx-background-radius: 8; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");

        Label title = new Label(p.getTitle());
        title.setStyle("-fx-text-fill: #81c784; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14px;");

        Label status = new Label("Status: " + p.getStatus());
        status.setStyle("-fx-text-fill: #aaaaaa; " +
                "-fx-font-size: 12px;");

        HBox actions = new HBox(10);

        Button btnVerPropostas = new Button("Ver");
        btnVerPropostas.getStyleClass().addAll("btn", "btn-sm", "btn-info");
        btnVerPropostas.setOnAction(e -> openProposes(p));

        Button btnEdit = new Button("Editar");
        btnEdit.getStyleClass().addAll("btn", "btn-sm", "btn-warning");
        btnEdit.setOnAction(e -> handleEdit(p));

        Button btnDelete = new Button("Excluir");
        btnDelete.getStyleClass().addAll("btn", "btn-sm", "btn-danger");
        btnDelete.setOnAction(e -> handleDelete(p));

        actions.getChildren().addAll(btnVerPropostas, btnEdit, btnDelete);

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
                    loadClientProjects();
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

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Editar Projeto");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();

            stage.setOnHidden(e -> loadClientProjects());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openProposes(com.freelamarket.model.Proposal p) {
        try {
            FXMLLoader loader = new FXMLLoader(com.freelamarket.App.class.getResource("view/project_proposes.fxml"));
            Parent root = loader.load();

            ProjectProposeController controller = loader.getController();
            controller.setProjetct(p.getId(), p.getTitle());

            Stage stage = new Stage();
            stage.setTitle("Gerenciar Propostas");
            stage.setScene(new Scene(root, 500, 600));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
