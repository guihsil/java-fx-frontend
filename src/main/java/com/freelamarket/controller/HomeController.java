package com.freelamarket.controller;

import com.freelamarket.App;
import com.freelamarket.model.User;
import com.freelamarket.service.UserService;
import com.freelamarket.util.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;

public class HomeController {

    @FXML private StackPane contentArea;
    @FXML private Button btnPublicar;
    @FXML private Button btnMeusProjetos;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        configurarPermissoes();
        mostrarFeed();
    }

    private void configurarPermissoes() {
        btnPublicar.setVisible(false);
        btnPublicar.setManaged(false);
        try {
            User user = userService.getUserData();
            if ("CLIENT".equalsIgnoreCase(user.getUserType())) {
                btnPublicar.setVisible(true);
                btnPublicar.setManaged(true);
            } else if ("PROVIDER".equalsIgnoreCase(user.getUserType())){
                btnMeusProjetos.setText("📂 Minhas Propostas");
            }
        } catch (Exception e) {
            btnPublicar.setVisible(false);
            btnPublicar.setManaged(false);
            e.printStackTrace();
        }
    }

    @FXML
    public void mostrarFeed() {
        carregarTela("view/feed.fxml");
    }

    @FXML
    public void create() { carregarTela("view/project_create.fxml"); }

    @FXML
    public void mostrarMeusProjetos() { carregarTela("view/my_projects.fxml"); }

    @FXML
    public void mostrarPerfil() { carregarTela("view/profile.fxml"); }

    @FXML
    public void handleLogout() {
        UserSession.getInstance().cleanUserSession();

        try{
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnPublicar.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle("FreelaMarket - Login");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Erro ao voltar pro login: " + e.getMessage());
        }
    }

    private void carregarTela(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
            Parent novaTela = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(novaTela);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}