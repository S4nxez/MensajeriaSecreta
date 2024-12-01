package org.example.chatssecretos;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpringJavaFXApplication {
    public static void main(String[] args) {
        Application.launch(ChatsSecretos.class, args);
    }
}
