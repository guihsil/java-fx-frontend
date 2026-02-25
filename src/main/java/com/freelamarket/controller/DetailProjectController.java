package com.freelamarket.controller;

import com.freelamarket.model.Proposal;
import com.freelamarket.service.ProposalService;
import com.freelamarket.service.UserService;
import com.freelamarket.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;

public class DetailProjectController {
    @FXML private Label lblTitle;
    @FXML private Label lblClient;
    @FXML private TextArea txtDesc;
    @FXML private Label lblBudget;
    @FXML private Label lblDeadLine;

    @FXML private VBox boxProposta;
    @FXML private TextField txtValor;
    @FXML private TextArea txtMensagem;
    @FXML private Label lblStatus;

    private Proposal projeto;
    private final ProposalService proposalService = new ProposalService();
    private final UserService userService = new UserService();

    public void setProject(Proposal p) {
        this.projeto = p;
        lblTitle.setText(p.getTitle());
        lblClient.setText("Publicado por: @" + (p.getClientName() != null ? p.getClientName() : "Anônimo"));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblDeadLine.setText("Prazo limite: " + (p.getDeadline() != null ? p.getDeadline().format(dtf) : "Sem data definida"));
        txtDesc.setText(p.getDescription());
        lblBudget.setText("Orçamento previsto: R$ " + p.getValue());

        verifyUserType();
    }

    private void verifyUserType() {
        try {
            User currentUser = userService.getUserData();
            if ("PROVIDER".equalsIgnoreCase(currentUser.getUserType())) {
                boxProposta.setVisible(true);
                boxProposta.setManaged(true);
            } else {
                boxProposta.setVisible(false);
                boxProposta.setManaged(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            boxProposta.setVisible(false);
        }
    }

    @FXML
    public void sendPropose() {
        try {
            lblStatus.setText("A enviar...");
            lblStatus.setStyle("-fx-text-fill: #aaaaaa;");

            double valor = Double.parseDouble(txtValor.getText().replace(",", "."));
            String mensagem = txtMensagem.getText();

            boolean sucesso = proposalService.sendPropose(projeto.getId(), valor, mensagem);

            if (sucesso) {
                lblStatus.setText("Proposta enviada com sucesso! 🎉");
                txtValor.setDisable(true);
                txtMensagem.setDisable(true);
            }
        } catch (NumberFormatException e) {
            lblStatus.setText("Erro: O valor deve ser um número válido!");
        } catch (Exception e) {
            lblStatus.setText("Erro ao enviar: " + e.getMessage());
        }
    }
}
