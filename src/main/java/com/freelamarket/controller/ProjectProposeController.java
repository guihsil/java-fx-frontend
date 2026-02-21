package com.freelamarket.controller;

import com.freelamarket.model.Negotiation;
import com.freelamarket.service.ProposalService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;

public class ProjectProposeController {
    @FXML private Label lblTitulo;
    @FXML private Label lblStatus;
    @FXML private VBox containerPropostas;

    private String projectId;
    private final ProposalService service = new ProposalService();

    public void setProjetct(String idProjeto, String tituloProjeto) {
        this.projectId = idProjeto;
        lblTitulo.setText("Vaga: " + tituloProjeto);
        loadProposes();
    }

    private void loadProposes() {
        Task<List<Negotiation>> task = new Task<>() {
            @Override
            protected List<Negotiation> call() throws Exception {
                return service.getProposalsForProject(projectId);
            }
        };

        task.setOnSucceeded(e -> {
            List<Negotiation> lista = task.getValue();
            containerPropostas.getChildren().clear();

            if (lista == null || lista.isEmpty()) {
                lblStatus.setText("Nenhum freelancer enviou proposta ainda. 😢");
            } else {
                lblStatus.setText("Encontramos " + lista.size() + " proposta(s)!");
                for (Negotiation n : lista) {
                    containerPropostas.getChildren().add(createProposalCards(n));
                }
            }
        });

        task.setOnFailed(e -> {
            lblStatus.setText("Erro ao carregar: " + task.getException().getMessage());
            lblStatus.setStyle("-fx-text-fill: #ef5350;");
        });

        new Thread(task).start();
    }

    private VBox createProposalCards(Negotiation n) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #2b2b2b; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 0);");

        Label lblNome = new Label("👤 " + (n.getProviderName() != null ? n.getProviderName() : "Freelancer Anônimo"));
        lblNome.setStyle("-fx-text-fill: #64b5f6; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblValor = new Label("💰 Cobrou: R$ " + n.getProposedValue());
        lblValor.setStyle("-fx-text-fill: #81c784; -fx-font-weight: bold;");

        Text txtMensagem = new Text("Mensagem: " + n.getMessage());
        txtMensagem.setStyle("-fx-fill: white;");
        txtMensagem.setWrappingWidth(380);

        Label lblEstado = new Label("Status: " + n.getStatus());
        lblEstado.setStyle("-fx-text-fill: #aaaaaa; -fx-font-style: italic;");

        HBox boxBotoes = new HBox(10);
        Button btnAceitar = new Button("✅ Aceitar");
        btnAceitar.getStyleClass().addAll("btn", "btn-sm", "btn-success");

        Button btnRecusar = new Button("❌ Recusar");
        btnRecusar.getStyleClass().addAll("btn", "btn-sm", "btn-danger");

        btnAceitar.setOnAction(e -> replyPropose(n.getId(), true));
        btnRecusar.setOnAction(e -> replyPropose(n.getId(), false));

        if (!"PENDING".equalsIgnoreCase(n.getStatus())) {
            boxBotoes.setVisible(false);
            boxBotoes.setManaged(false);
        }

        boxBotoes.getChildren().addAll(btnAceitar, btnRecusar);
        card.getChildren().addAll(lblNome, lblValor, txtMensagem, lblEstado, boxBotoes);

        return card;
    }

    private void replyPropose(String negotiationId, boolean aceitar) {
        try {
            boolean sucesso = service.replyPropose(negotiationId, aceitar);
            if (sucesso) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText(aceitar ? "Proposta ACEITA com sucesso! 🎉" : "Proposta recusada.");
                alert.showAndWait();

                loadProposes();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Erro ao enviar resposta: " + e.getMessage());
            alert.show();
        }
    }

}
