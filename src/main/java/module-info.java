module org.example.chatssecretos {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens org.example.chatssecretos to javafx.fxml;
    exports org.example.chatssecretos;
    exports org.example.chatssecretos.ui;
    opens org.example.chatssecretos.ui to javafx.fxml;
}