package com.freelamarket.controller;

import com.freelamarket.App;
import com.freelamarket.service.AuthService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> accountTypeField;
    @FXML private Label msgLabel;

    private final AuthService auth = new AuthService();

    @FXML
    public void initialize() {
        accountTypeField.getItems().addAll("Client", "Provider");
    }

    @FXML
    public void handleSignup() {
        if(!passwordField.getText().equals(confirmPasswordField.getText())) {
            msgLabel.setText("As senhas não conferem!");
            msgLabel.getStyleClass().setAll("text-danger");
            return;
        }

        if (nameField.getText().isEmpty() || emailField.getText().isEmpty() || accountTypeField.getValue() == null) {
            msgLabel.setText("Preencha o tipo de conta!");
            msgLabel.getStyleClass().setAll("text-danger");
            return;
        }

        String typeSelected = accountTypeField.getValue();
        String typeBackend = typeSelected.contains("Client") ? "client" : "provider";

        msgLabel.setText("Dados Enviados...");
        msgLabel.getStyleClass().remove("text-danger");

        Task<Void> registerTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                auth.register(
                        nameField.getText(),
                        emailField.getText(),
                        passwordField.getText(),
                        phoneField.getText(),
                        typeBackend
                );
                return null;
            }
        };

        registerTask.setOnSucceeded(e -> {
            msgLabel.setText("Sucesso! Faça login agora.");
            msgLabel.getStyleClass().setAll("text-success");
            handleBack();
        });

        registerTask.setOnFailed(e -> {
            Throwable error = e.getSource().getException();
            msgLabel.setText("Erro: " + error.getMessage());
            msgLabel.getStyleClass().setAll("text-danger");
            error.printStackTrace();
        });

        new Thread(registerTask).start();
    }

    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setMaximized(true);
            stage.setTitle("FreelaMarket - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
