package org.example.chatssecretos;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.example.chatssecretos.utils.Constantes;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ChatsSecretos extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() throws Exception {
        super.init();
        applicationContext = new SpringApplicationBuilder(SpringJavaFXApplication.class).run();
    }

    @Override
    public void start(Stage stage){
        /*FXMLLoader fxmlLoader = new FXMLLoader(ChatsSecretos.class.getResource("/org/example/chatssecretos/fxml/logIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LogInController controller = fxmlLoader.getController();
        controller.setStage(stage);

        stage.setScene(scene);*/ //esto hay que quitarlo
        stage.setTitle(Constantes.TITLE);
        stage.setResizable(false);
        stage.show();
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }

    static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {super(stage);}
        public Stage getStage(){return ((Stage) getSource());}
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        applicationContext.close();
        Platform.exit();
    }
    public static void main(String[] args) {
        launch();
    }
}