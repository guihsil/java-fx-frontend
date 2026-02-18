package com.freelamarket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            scene.getStylesheets().add(App.class.getResource("css/login.css").toExternalForm());
            scene.getStylesheets().add(App.class.getResource("css/register.css").toExternalForm());
            scene.getStylesheets().add(App.class.getResource("css/home.css").toExternalForm());

            stage.setTitle("FreelaMarket");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            System.out.println("Erro: ");
            e.printStackTrace();
        }

    }
}
