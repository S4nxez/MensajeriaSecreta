package org.example.chatssecretos.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.example.chatssecretos.HelloApplication;
import org.example.chatssecretos.domain.modelo.User;
import org.example.chatssecretos.domain.service.UserService;
import org.example.chatssecretos.utils.Constantes;

import java.io.IOException;
import java.util.Collections;

import static java.lang.Boolean.TRUE;

@Log4j2
public class LogInController {

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


    @FXML
    public void crearClicked() {
        labelError.setText("");
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
        email.clear();
        signUpLabelError.setVisible(false);
        labelErrorRepetir.setVisible(false);
        labelError.setVisible(true);
        crearCuentaPane.setVisible(false);
        logInPane.setVisible(true);
    }

    public void logInClicked() {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/org/example/chatssecretos/fxml/MainMenu.fxml"));
        Scene scene;

        if (!usrService.logIn(new User(username.getText() , "",pwd.getText(), Collections.emptyList()))) {
            labelError.setText(Constantes.E_CAMPOS_GENERICO);
            return;
        }
        scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
            MainMenuController mainMenuController = fxmlLoader.getController();
            mainMenuController.setUsername(username.getText());
            mainMenuController.initializeTable();
            mainMenuController.initializeDropList();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        stage.setScene(scene);
        stage.show();
    }

    public void checkCrear() {
        labelErrorRepetir.setText(TRUE.equals(usrService.checkNewPassword(pwdFieldRepeat.getText(), signUpPwd.getText()))
                ? "" : Constantes.E_REPETIR_CONTRASENYA);
        signUpLabelError.setText(usrService.checkNewUsername(signUpUsername.getText()) ? "" : Constantes.E_NOMBRE_USADO);

        if(notEmpty() && usrService.createUser(new User(signUpUsername.getText(), email.getText(),
                signUpPwd.getText(), Collections.emptyList())) && usrService.checkNewPassword(pwdFieldRepeat.getText(),
                signUpPwd.getText()))
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