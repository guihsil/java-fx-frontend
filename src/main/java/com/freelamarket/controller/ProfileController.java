package com.freelamarket.controller;

import com.freelamarket.model.User;
import com.freelamarket.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ProfileController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private Label msgLabel;
    @FXML private VBox clientBox;
    @FXML private VBox providerBox;
    // Client
    @FXML private TextField companyNameField;
    @FXML private TextField cnpjField;
    @FXML private TextField addressField;
    @FXML private TextArea bioClientField;
    // Provider
    @FXML private TextArea bioProviderField;
    @FXML private TextField portfolioField;
    @FXML private TextField hourlyRateField;
    @FXML private TextField skillsField;

    @FXML private Button btnEdit;
    @FXML private Button btnSave;

    private final UserService userService = new UserService();
    private User currentUser;

    @FXML
    public void initialize() {
        lockFields(true);
        loadData();
    }

    private void loadData() {
        try {
            currentUser = userService.getUserData();

            nameField.setText(currentUser.getName());
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getPhoneNumber());

            if ("CLIENT".equalsIgnoreCase(currentUser.getUserType())) {
                clientBox.setVisible(true);
                clientBox.setManaged(true);

                companyNameField.setText(clearNull(currentUser.getCompanyName()));
                cnpjField.setText(clearNull(currentUser.getCnpjNif()));
                addressField.setText(clearNull(currentUser.getAddress()));
                bioClientField.setText(clearNull(currentUser.getBio()));

            } else {
                providerBox.setVisible(true);
                providerBox.setManaged(true);

                bioProviderField.setText(clearNull(currentUser.getBio()));
                portfolioField.setText(clearNull(currentUser.getPortfolioUrl()));
                skillsField.setText(clearNull(currentUser.getSkills()));

                hourlyRateField.setText(currentUser.getHourlyRate() != null ? String.valueOf(currentUser.getHourlyRate()) : "");
            }
        } catch (Exception e) {
            msgLabel.setText("Erro ao carregar perfil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void enableEdit() {
        lockFields(false);
        btnEdit.setVisible(false);
        btnEdit.setManaged(false);
        btnSave.setVisible(true);
        btnSave.setManaged(true);
        msgLabel.setText("Você pode editar seus dados agora.");
        msgLabel.setStyle("-fx-text-fill: #64b5f6;");
    }

    @FXML
    public void saveEdit() {
        try {
            currentUser.setName(nameField.getText());
            currentUser.setPhoneNumber(phoneField.getText());

            if ("CLIENT".equalsIgnoreCase(currentUser.getUserType())) {
                currentUser.setCompanyName(companyNameField.getText());
                currentUser.setCnpjNif(cnpjField.getText());
                currentUser.setAddress(addressField.getText());
                currentUser.setBio(bioClientField.getText());
            } else {
                currentUser.setBio(bioProviderField.getText());
                currentUser.setPortfolioUrl(portfolioField.getText());
                currentUser.setSkills(skillsField.getText());

                if (!hourlyRateField.getText().isEmpty()) {
                    currentUser.setHourlyRate(Double.parseDouble(hourlyRateField.getText().replace(",", ".")));
                }
            }

            msgLabel.setText("Salvando...");
            boolean sucesso = userService.updateProfile(currentUser);

            if (sucesso) {
                msgLabel.setText("Perfil atualizado com sucesso! ✅");
                msgLabel.setStyle("-fx-text-fill: #81c784;");

                lockFields(true);
                btnSave.setVisible(false);
                btnSave.setManaged(false);
                btnEdit.setVisible(true);
                btnEdit.setManaged(true);
            }

        } catch (NumberFormatException e) {
            msgLabel.setText("O Valor Hora deve ser um número! (Ex: 50.00)");
            msgLabel.setStyle("-fx-text-fill: #ef5350;");
        } catch (Exception e) {
            msgLabel.setText("Erro ao salvar: " + e.getMessage());
            msgLabel.setStyle("-fx-text-fill: #ef5350;");
            e.printStackTrace();
        }
    }

    private void lockFields(boolean travar) {
        boolean editar = !travar;
        nameField.setEditable(editar);
        phoneField.setEditable(editar);

        companyNameField.setEditable(editar);
        cnpjField.setEditable(editar);
        addressField.setEditable(editar);
        bioClientField.setEditable(editar);

        bioProviderField.setEditable(editar);
        portfolioField.setEditable(editar);
        hourlyRateField.setEditable(editar);
        skillsField.setEditable(editar);
    }

    private String clearNull(String info) {
        return (info == null || info.equalsIgnoreCase("null")) ? "" : info;
    }

    private String clearNull(Double info) {
        return info == null ? "" : String.valueOf(info);
    }
}