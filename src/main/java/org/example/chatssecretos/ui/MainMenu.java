package org.example.chatssecretos.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenu implements Initializable {

    @FXML
    public AnchorPane modalPane;
    @FXML
    public AnchorPane crearFields;
    @FXML
    public AnchorPane anyadirFields;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void crearClicked(ActionEvent actionEvent) {
        anyadirFields.setVisible(false);
        modalPane.setVisible(true);
        crearFields.setVisible(true);
    }

    public void anyadirClicked(ActionEvent actionEvent) {
        modalPane.setVisible(true);
        crearFields.setVisible(false);
        anyadirFields.setVisible(true);
    }

    public void closeModal(ActionEvent actionEvent) {
        modalPane.setVisible(false);
    }
}
