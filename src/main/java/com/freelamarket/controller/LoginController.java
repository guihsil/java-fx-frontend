package com.freelamarket.controller;

import com.freelamarket.App;
import com.freelamarket.service.AuthService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if(email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Por favor, preencha todos os campos!");
            return;
        }

        Task<Boolean> loginTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return authService.login(email, password);
            }
        };

        loginTask.setOnSucceeded(e -> {
            abrirHome();
        });

        loginTask.setOnFailed(e -> {
            Throwable erro = loginTask.getException();
            errorLabel.setText("Erro: " + erro.getMessage());
            erro.printStackTrace();
        });

        new Thread(loginTask).start();
    }

    @FXML
    public void handleRegister() {
        try{
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/register.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setMaximized(true);
            stage.setTitle("FreelaMarket - Registrar");
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Erro ao carregar Registrar: "+e.getMessage());
        }
    }

    private void abrirHome() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setMaximized(true);
            stage.setTitle("FreelaMarket - Home");
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Erro ao carregar Home: "+e.getMessage());
        }
    }

}
