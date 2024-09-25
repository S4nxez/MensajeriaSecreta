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
import org.example.chatssecretos.domain.modelo.User;
import org.example.chatssecretos.domain.service.UserService;
import org.example.chatssecretos.utils.Constantes;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LogInController implements Initializable {

    @FXML
    public TextField email;
    @FXML
    private AnchorPane logInPane;
    @FXML
    private AnchorPane crearCuentaPane;
    @FXML
    private TextField username;
    @FXML
    private PasswordField pwd;
    @FXML
    private Label labelError;
    @FXML
    private Label signUpLabelError;
    @FXML
    private Label labelErrorRepetir;
    @FXML
    private TextField signUpUsername;
    @FXML
    private PasswordField signUpPwd;
    @FXML
    private PasswordField pwdFieldRepeat;

    @Setter
    private Stage stage;

    private final UserService usrService = new UserService();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    @FXML
    public void crearClicked(MouseEvent mouseEvent) {
        logInPane.setVisible(false);
        signUpLabelError.setVisible(true);
        labelErrorRepetir.setVisible(true);
        labelError.setVisible(false);
        crearCuentaPane.setVisible(true);
    }

    public void signUpLogIn() {
        username.clear();
        pwd.clear();
        signUpUsername.clear();
        signUpPwd.clear();
        pwdFieldRepeat.clear();
        signUpLabelError.setVisible(false);
        labelErrorRepetir.setVisible(false);
        labelError.setVisible(true);

        crearCuentaPane.setVisible(false);
        logInPane.setVisible(true);
    }

    public void logInClicked() {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/org/example/chatssecretos/fxml/MainMenu.fxml"));
        Scene scene;

        if (!usrService.logIn(new User(username.getText() , "",pwd.getText())))
            return;
        try {
            scene = new Scene(fxmlLoader.load());
            MainMenuController mainMenuController = fxmlLoader.getController();
            mainMenuController.setUsername(username.getText());
            mainMenuController.initializeTable();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setScene(scene);
        stage.show();
    }

    public void checkCrear() {
        labelErrorRepetir.setText(usrService.checkNewPassword(pwdFieldRepeat, signUpPwd) ? "" : Constantes.E_REPETIR_CONTRASENYA);
        signUpLabelError.setText(usrService.checkNewUsrnm(signUpUsername) ? "" : Constantes.E_NOMBRE_USADO);

        if(notEmpty() && usrService.createUsr(signUpPwd, email, signUpUsername) &&
                usrService.checkNewPassword(pwdFieldRepeat, signUpPwd))
            signUpLogIn();
    }

    private boolean notEmpty() {
        if(pwdFieldRepeat.getText().isEmpty() || signUpUsername.getText().isEmpty()|| signUpPwd.getText().isEmpty() ||
                email.getText().isEmpty()) {
            signUpLabelError.setText(Constantes.E_CAMPOS_INCOMPLETOS);
            return false;
        }
        return true;
    }
}