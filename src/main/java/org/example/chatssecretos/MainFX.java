package org.example.chatssecretos;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.example.chatssecretos.ui.LogInController;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
public class MainFX implements ApplicationListener<ChatsSecretos.StageReadyEvent> {

    private final FXMLLoader fxmlLoader;
    private final LogInController logInController;

    public MainFX(FXMLLoader fxmlLoader, LogInController logInController){
        this.fxmlLoader = fxmlLoader;
        this.logInController = logInController;
    }

    @Override
    public void onApplicationEvent(ChatsSecretos.StageReadyEvent event) {
        try {
            Stage stage = event.getStage();
            fxmlLoader.setController(logInController);
            Parent fxmlParent = fxmlLoader.load(getClass().getResourceAsStream("/org/example/chatssecretos/fxml/logIn.fxml"));
            logInController.setStage(stage);

            stage.setScene(new Scene(fxmlParent));
            stage.show();
        } catch(IOException e){
            log.error("Failed to load FXML file", e);
        }
    }
}
