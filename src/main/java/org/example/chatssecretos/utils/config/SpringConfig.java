package org.example.chatssecretos.utils.config;

import com.google.gson.GsonBuilder;
import javafx.fxml.FXMLLoader;
import org.example.chatssecretos.utils.LocalDateTimeDeserializer;
import org.example.chatssecretos.utils.LocalDateTimeSerializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.gson.Gson;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class SpringConfig {
    @Bean
    public FXMLLoader createLoader(ApplicationContext context) {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(context::getBean);
        return loader;
    }

    @Bean
    public PasswordEncoder createPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Gson createGson() {
        return  new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer()).setPrettyPrinting().create();
    }
}
