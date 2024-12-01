package org.example.chatssecretos.utils.config;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


@Log4j2
@Getter
@Component
public class Configuration {
    private String pathUsers;
    private String pathGroups;
    private String pathMessages;
    private String pathPrivateGroups;
    private String pathKeyStore;
    private String keystorePassword;
    private String pathPrivateMessages;

    private Configuration() {
        Properties properties= new Properties();
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
            properties.load(input);
            this.pathUsers = properties.getProperty("pathUsers");
            this.pathGroups = properties.getProperty("pathGroups");
            this.pathMessages = properties.getProperty("pathMessages");
            this.pathKeyStore = properties.getProperty("pathKeyStore");
            this.pathPrivateGroups = properties.getProperty("pathPrivateGroups");
            this.keystorePassword = properties.getProperty("keystorePassword");
            this.pathPrivateMessages = properties.getProperty("pathPrivateMessages");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
