package org.example.chatssecretos.ui;

import javafx.application.Platform;
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
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.errors.ErrorAppDataBase;
import org.example.chatssecretos.domain.errors.ErrorAppSecurity;
import org.example.chatssecretos.domain.errors.ErrorAppUser;
import org.example.chatssecretos.domain.modelo.User;
import org.example.chatssecretos.domain.service.UserService;
import org.example.chatssecretos.utils.Constantes;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Log4j2
@Component
public class LogInController {

    @FXML
    private TextField email;
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
    @FXML
    private AnchorPane modalError;
    @FXML
    private Label errorLabelModal;

    @Setter
    private Stage stage;

    private final UserService usrService;
    private final ApplicationContext context;

    public LogInController(UserService usrService, ApplicationContext context) {
        this.usrService = usrService;
        this.context = context;
    }

    @FXML
    private void crearClicked() {
        labelError.setText(null);
        logInPane.setVisible(false);
        signUpLabelError.setVisible(true);
        labelErrorRepetir.setVisible(true);
        labelError.setVisible(false);
        crearCuentaPane.setVisible(true);
    }

    @FXML
    private void signUpLogIn() {
        username.clear();
        pwd.clear();
        signUpUsername.clear();
        signUpPwd.clear();
        pwdFieldRepeat.clear();
        email.clear();
        signUpLabelError.setText(null);
        signUpLabelError.setVisible(false);
        labelErrorRepetir.setVisible(false);
        labelError.setVisible(true);
        crearCuentaPane.setVisible(false);
        logInPane.setVisible(true);
    }

    @FXML
    private void logInClicked() {
        usrService.logIn(new User(username.getText(), null, pwd.getText(),
                Collections.emptyList())).thenAcceptAsync(result ->
                result.peekLeft(e ->
                            Platform.runLater(() ->
                                    mostrarError(e)
                            )
                        )
                        .peek(v ->
                            Platform.runLater(() -> {
                                Scene scene = null;
                                try {
                                    FXMLLoader loader = new FXMLLoader();
                                    loader.setControllerFactory(context::getBean);
                                    loader.setLocation(getClass().getResource(Constantes.MAIN_MENU_FXML));
                                    scene = new Scene(loader.load());
                                    MainMenuController mainMenuController = loader.getController();
                                    mainMenuController.setUsername(username.getText());
                                    mainMenuController.setUserPassword(pwd.getText());
                                    mainMenuController.initializeTable();
                                    mainMenuController.initializeDropList();
                                } catch (IOException e) {
                                    log.error(e.getMessage(), e);
                                }
                                stage.setScene(scene);
                                stage.show();
                            })
                        ));
    }

    private void mostrarError(ErrorApp errorText) {
        switch (errorText) {
            case ErrorAppDataBase e -> {
                switch (e) {
                    case NO_CONNECTION -> showModalError(Constantes.E_BBDD_CONEXION);
                    case ERROR_LEER_FICHEROS -> showModalError(Constantes.E_FICHEROS);
                    case ERROR_DATABASE -> showModalError(Constantes.E_BBDD);
                    case ERROR_HILOS -> showModalError(Constantes.E_HILO);
                }
            }
            case ErrorAppUser e -> {
                switch (e) {
                    case NO_MATCHING_CREDENTIALS -> labelError.setText(Constantes.E_CREDENTIALS);
                    case USERNAME_NOT_AVAILABLE -> signUpLabelError.setText(Constantes.E_NOMBRE_USADO);
                    case PASSWORDS_NOT_MATCH -> labelErrorRepetir.setText(Constantes.E_REPETIR_CONTRASENYA);
                    case USUARIO_NO_EXISTE -> labelError.setText(Constantes.E_USUARIO_NO_EXISTE);
                    case CAMPOS_INCOMPLETOS -> signUpLabelError.setText(Constantes.E_CAMPOS_INCOMPLETOS);
                    case YA_ES_AMIGO -> labelError.setText(Constantes.E_YA_ES_AMIGO);
                }
            }
            case ErrorAppSecurity e -> {
                switch (e){
                    case ERROR_DESENCRIPTAR_MENSAJE -> showModalError(Constantes.E_DESENCRIPTAR_MENSAJE);
                    case E_GENERANDO_CLAVES -> showModalError(Constantes.E_GENERANDO_CLAVES);
                    case E_PEDIR_CLAVE_PRIVADA -> showModalError(Constantes.E_PEDIR_CLAVE_PRIVADA);
                    case E_DESENCRIPTAR_MENSAJE -> showModalError(Constantes.E_DESENCRIPTAR_EL_MENSAJE);
                    case E_ENCRIPTAR_MENSAJE -> showModalError(Constantes.E_ENCRIPTAR_MENSAJE);
                }
            }
            default -> showModalError(Constantes.E_CAMPOS_GENERICO);
        }
    }

    @FXML
    private void checkCrear() {
        usrService.notEmpty(pwdFieldRepeat.getText(), signUpUsername.getText(),
                signUpPwd.getText(), email.getText()).thenAcceptAsync(result ->
                result.peek(v ->
                        usrService.checkCrear(pwdFieldRepeat.getText(), new User(signUpUsername.getText(),
                                        email.getText(), signUpPwd.getText(), Collections.emptyList()))
                                .thenAccept(result2 ->
                                        result2.peekLeft(e ->
                                                Platform.runLater(() -> mostrarError(e))
                                        ).peek(v2 -> Platform.runLater(this::signUpLogIn)))
                ).peekLeft(e ->
                        Platform.runLater(() -> mostrarError(e))
                ));
    }

    @FXML
    private void showModalError(String errorMensaje) {
        modalError.setVisible(true);
        errorLabelModal.setText(errorMensaje);
    }

    @FXML
    private void closeModal() {
        modalError.setVisible(false);
    }
}