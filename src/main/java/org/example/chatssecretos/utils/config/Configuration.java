package org.example.chatssecretos.utils.config;

import lombok.Data;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.example.chatssecretos.utils.Constantes;

import java.io.IOException;
import java.util.Properties;


@Log4j2
@Getter
public class Configuration {
    private String pathUsers;
    private String baseUrl;

    public Configuration() {
        try {
            Properties p = new Properties();
            p.load(getClass().getClassLoader().getResourceAsStream(Constantes.CONFIG_PROPERTIES));
            this.pathUsers = p.getProperty("pathUsers");
            this.baseUrl = p.getProperty("baseUrl");
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
    }
}
