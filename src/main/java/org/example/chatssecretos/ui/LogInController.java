package org.example.chatssecretos.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Setter;
import org.example.chatssecretos.HelloApplication;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LogInController implements Initializable {
    @FXML
    private AnchorPane logInPane;
    @FXML
    private AnchorPane crearCuentaPane;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField pwdField;
    @FXML
    private Label labelError;
    @FXML
    private Label signUpLabelError;
    @FXML
    private Label labelErrorRepetir;
    @FXML
    private TextField signUpUsernameField;
    @FXML
    private PasswordField signUpPwdField;
    @FXML
    private PasswordField pwdFieldRepeat;

    @Setter
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    @FXML
    public void crearClicked(MouseEvent mouseEvent) {
        logInPane.setVisible(false);
        crearCuentaPane.setVisible(true);
    }

    public void signUpLogIn() {
        usernameField.clear(); //Limpio todos los campos y los labels de error
        pwdField.clear();
        signUpUsernameField.clear();
        signUpPwdField.clear();
        pwdFieldRepeat.clear();
        signUpLabelError.setVisible(false);
        labelErrorRepetir.setVisible(false);
        labelError.setVisible(false);

        crearCuentaPane.setVisible(false);
        logInPane.setVisible(true);
    }

    public void logInClicked(MouseEvent mouseEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/org/example/chatssecretos/fxml/MainMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setScene(scene);
        stage.show();
    }
}