module org.example.chatssecretos {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires com.google.gson;
    requires org.apache.logging.log4j;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    exports org.example.chatssecretos.utils.config;

    opens org.example.chatssecretos.domain.modelo to com.google.gson, javafx.base;
    opens org.example.chatssecretos to javafx.fxml;
    exports org.example.chatssecretos;
    exports org.example.chatssecretos.ui;
    opens org.example.chatssecretos.ui to javafx.fxml;
    exports org.example.chatssecretos.domain.modelo;
}