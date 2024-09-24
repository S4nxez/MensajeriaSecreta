package org.example.chatssecretos.utils.config;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.example.chatssecretos.utils.Constantes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


@Log4j2
@Getter
public class Configuration {
    private String pathUsers;

    public Configuration() {
        Properties properties= new Properties();
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
            properties.load(input);
            this.pathUsers = properties.getProperty("pathUsers");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
