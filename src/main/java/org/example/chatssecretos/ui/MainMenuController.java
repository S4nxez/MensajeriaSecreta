package org.example.chatssecretos.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.example.chatssecretos.domain.modelo.Group;
import org.example.chatssecretos.domain.modelo.Message;
import org.example.chatssecretos.domain.modelo.User;
import org.example.chatssecretos.domain.service.GroupService;
import org.example.chatssecretos.domain.service.MessageService;
import org.example.chatssecretos.domain.service.UserService;
import org.example.chatssecretos.utils.Constantes;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


public class MainMenuController implements Initializable {

    @FXML
    public AnchorPane modalPane;
    @FXML
    public AnchorPane crearFields;
    @FXML
    public AnchorPane anyadirFields;
    public TextField createName;
    public PasswordField createPwd;
    public PasswordField pwdRepeat;
    public Label usrnmDisplay;
    public Label errorCrear;
    public TableView<Group> groupsTable;
    public TableColumn<Group, String> nameColumn;
    public TableColumn<Group, String> messageColumn;
    public PasswordField logInPwd;
    public TextField logInLink;
    public Label nombreGrupo;
    public ListView messagesList;
    public TextField msgField;
    public Button send;

    private String usrnmValue;

    private GroupService groupService = new GroupService();
    private UserService usrService = new UserService();
    private MessageService msgService = new MessageService();

    public void setUsername(String username) {
        usrnmDisplay.setText(username);
        this.usrnmValue = username;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTable();
        groupsTable.setOnMouseClicked((MouseEvent event) -> groupClicked());
    }

    private void groupClicked() {
        if (groupsTable.getSelectionModel().getSelectedItem() != null) {
            msgField.setVisible(true);
            send.setVisible(true);
            Group selectedGroup = (Group) groupsTable.getSelectionModel().getSelectedItem();
            nombreGrupo.setText(selectedGroup.getNombre());
            ObservableList list =  msgService.getMessagesByGroup(selectedGroup);
            if (list != null)
                messagesList.setItems(list);
        }

    }

    public void initializeTable() {
        List<Group> groups = groupService.getGroupsByUser(usrnmValue);
        if (groups != null) {
            groupsTable.setItems(FXCollections.observableArrayList(groups));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            messageColumn.setCellValueFactory(cellData -> {
                Group group = cellData.getValue();
                String lastMessage = msgService.getLastMessage(group);
                return new SimpleStringProperty(lastMessage);
            });
        }
    }

    public void crearClicked() {
        errorCrear.setText("");
        createName.setText("");
        createPwd.setText("");
        pwdRepeat.setText("");
        anyadirFields.setVisible(false);
        modalPane.setVisible(true);
        crearFields.setVisible(true);
    }

    public void anyadirClicked() {
        modalPane.setVisible(true);
        crearFields.setVisible(false);
        anyadirFields.setVisible(true);
        errorCrear.setText("");
    }

    public void closeModal() {
        modalPane.setVisible(false);
    }

    public void createGroup() {
        Optional<User> optionalUser = usrService.getUserByName(usrnmDisplay);
        if (optionalUser.isPresent()) {
            User expectedUsr = optionalUser.get();
            if (createName.getText().isEmpty() || createPwd.getText().isEmpty() || pwdRepeat.getText().isEmpty() ||
                    !(pwdRepeat.getText().equals(createPwd.getText())) || !groupService.addGroup(new
                    Group(createName.getText(), new ArrayList<>(List.of(expectedUsr)), createPwd.getText(),
                    usrnmDisplay.getText(), LocalDateTime.now()))) {
                errorCrear.setText(Constantes.E_CAMPOS_GENERICO);
            } else {
                errorCrear.setText("");
                closeModal();
                initializeTable();
            }
        }
    }

    public void entrarClicked() {
        if (groupService.logIn(logInLink.getText() , logInPwd.getText(), usrService.getUserByName(usrnmDisplay).get())) {
            initializeTable();
            closeModal();
        } else {
            errorCrear.setText(Constantes.E_CAMPOS_GENERICO);
        }
    }

    public void sendMsg() {
        msgService.addNewMessage(new Message(msgField.getText(), LocalDateTime.now(), usrnmValue, nombreGrupo.getText()));
        msgField.setText("");
        groupClicked();
    }
}
