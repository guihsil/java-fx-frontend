package com.freelamarket.controller;

import com.freelamarket.model.Proposal;
import com.freelamarket.model.User;
import com.freelamarket.service.ProposalService;
import com.freelamarket.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DetailProjectController {

    // --- Componentes da Tela (IDs devem bater com o FXML) ---
    @FXML private Label lblTitle;
    @FXML private Label lblClient;
    @FXML private Label lblDesc;      // Agora é Label (leitura melhor)
    @FXML private Label lblBudget;
    @FXML private Label lblDeadline;  // Novo: Prazo do projeto

    // --- Área de Envio de Proposta ---
    @FXML private VBox boxProposta;
    @FXML private TextField txtValor;
    @FXML private TextArea txtMensagem;
    @FXML private Label lblStatus;

    // --- Serviços ---
    private Proposal projeto;
    private final ProposalService proposalService = new ProposalService();
    private final UserService userService = new UserService();

    /**
     * Método chamado pela tela anterior para carregar os dados.
     */
    public void setProject(Proposal p) {
        this.projeto = p;

        // 1. Preenche os Textos Básicos
        lblTitle.setText(p.getTitle());

        String autor = (p.getClientName() != null && !p.getClientName().isEmpty())
                ? p.getClientName() : "Anônimo";
        lblClient.setText("Publicado por: " + autor);

        lblDesc.setText(p.getDescription());

        // 2. Formata o Dinheiro (Ex: R$ 1.500,00)
        if (p.getValue() != null) {
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            lblBudget.setText(nf.format(p.getValue()));
        } else {
            lblBudget.setText("A combinar");
        }

        // 3. Formata a Data (Ex: 25/12/2026)
        if (p.getDeadline() != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
            lblDeadline.setText("Prazo: " + p.getDeadline().format(dtf));
        } else {
            lblDeadline.setText("Sem prazo definido");
        }

        // 4. Decide se mostra a caixa de proposta
        verifyPermissions();
    }

    /**
     * Verifica se o usuário logado pode enviar proposta.
     */
    private void verifyPermissions() {
        try {
            User currentUser = userService.getUserData();

            System.out.println("--- PERMISSÕES ---");
            System.out.println("Usuário: " + currentUser.getUserType());
            System.out.println("Projeto Status: " + projeto.getStatus());

            // Regra 1: Tem que ser Prestador (Provider)
            boolean isProvider = "PROVIDER".equalsIgnoreCase(currentUser.getUserType())
                    || "PRESTADOR".equalsIgnoreCase(currentUser.getUserType());

            // Regra 2: O Projeto tem que estar ABERTO (null ou OPEN)
            boolean isProjectOpen = projeto.getStatus() == null
                    || "OPEN".equalsIgnoreCase(projeto.getStatus())
                    || "ABERTO".equalsIgnoreCase(projeto.getStatus());

            if (isProvider && isProjectOpen) {
                // MOSTRA A CAIXA
                boxProposta.setVisible(true);
                boxProposta.setManaged(true);
            } else {
                // ESCONDE A CAIXA
                boxProposta.setVisible(false);
                boxProposta.setManaged(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            boxProposta.setVisible(false); // Na dúvida, esconde
        }
    }

    /**
     * Ação do botão "Enviar Proposta"
     */
    @FXML
    public void sendPropose() {
        try {
            lblStatus.setText("Enviando...");
            lblStatus.setStyle("-fx-text-fill: #6B7280; -fx-font-weight: bold;"); // Cinza

            // Validação simples
            if (txtValor.getText().isEmpty() || txtMensagem.getText().isEmpty()) {
                lblStatus.setText("Preencha o valor e a mensagem!");
                lblStatus.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;"); // Vermelho
                return;
            }

            // Conversão de valor (aceita vírgula ou ponto)
            double valor = Double.parseDouble(txtValor.getText().replace(",", "."));
            String mensagem = txtMensagem.getText();

            // Chama o serviço
            boolean sucesso = proposalService.sendPropose(projeto.getId(), valor, mensagem);

            if (sucesso) {
                lblStatus.setText("Proposta enviada com sucesso! 🎉");
                lblStatus.setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;"); // Verde

                // Bloqueia campos para evitar envio duplicado
                txtValor.setDisable(true);
                txtMensagem.setDisable(true);
            } else {
                lblStatus.setText("Falha ao enviar. Tente novamente.");
                lblStatus.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
            }

        } catch (NumberFormatException e) {
            lblStatus.setText("Valor inválido! Digite apenas números.");
            lblStatus.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
        } catch (Exception e) {
            lblStatus.setText("Erro: " + e.getMessage());
            lblStatus.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
        }
    }
}