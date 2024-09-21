package org.example.chatssecretos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.chatssecretos.ui.LogInController;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/org/example/chatssecretos/fxml/logIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LogInController controller = fxmlLoader.getController();
        controller.setStage(stage);

        stage.setScene(scene);
        stage.setTitle("Chat privado"); //TODO quitar el string de aquí
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}