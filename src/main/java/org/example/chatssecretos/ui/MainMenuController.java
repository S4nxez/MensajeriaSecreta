package org.example.chatssecretos.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.example.chatssecretos.domain.modelo.Group;
import org.example.chatssecretos.domain.modelo.Message;
import org.example.chatssecretos.domain.modelo.User;
import org.example.chatssecretos.domain.service.GroupService;
import org.example.chatssecretos.domain.service.MessageService;
import org.example.chatssecretos.domain.service.UserService;
import org.example.chatssecretos.utils.Constantes;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

@Log4j2
@Component
public class MainMenuController implements Initializable {

    @FXML
    public AnchorPane modalPane;
    @FXML
    public AnchorPane crearFields;
    @FXML
    public SplitMenuButton listaUsers;
    @FXML
    public AnchorPane anyadirAmigoFields;
    @FXML
    public TextField newFriend;
    @FXML
    private AnchorPane anyadirFields;
    @FXML
    public TextField createName;
    @FXML
    public PasswordField createPwd;
    @FXML
    public PasswordField pwdRepeat;
    @FXML
    public Label usrnmDisplay;
    @FXML
    public Label errorCrear;
    @FXML
    public TableView<Group> groupsTable;
    @FXML
    public TableColumn<Group, String> nameColumn;
    @FXML
    public TableColumn<Group, String> messageColumn;
    @FXML
    public PasswordField logInPwd;
    @FXML
    public TextField logInLink;
    @FXML
    public Label nombreGrupo;
    @FXML
    public ListView<Message> messagesList;
    @FXML
    public TextField msgField;
    @FXML
    public Button send;

    @Setter
    private Stage stage;

    private String usrnmValue;

    private final GroupService groupService;
    private final UserService usrService;
    private final MessageService msgService;

    public MainMenuController(GroupService groupService, UserService usrService, MessageService msgService, FXMLLoader fxmlLoader) {
        this.groupService = groupService;
        this.usrService = usrService;
        this.msgService = msgService;
    }

    public void setUsername(String username) {
        usrnmDisplay.setText(username);
        this.usrnmValue = username;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        groupsTable.setOnMouseClicked((MouseEvent event) -> groupClicked());
    }

    private void groupClicked() {
        ObservableList<Message> list;
        Group selectedGroup;

        if (groupsTable.getSelectionModel().getSelectedItem() != null) {
            msgField.setVisible(true);
            send.setVisible(true);
            selectedGroup = groupsTable.getSelectionModel().getSelectedItem();
            nombreGrupo.setText(selectedGroup.getNombre());
            list =  FXCollections.observableArrayList(msgService.getMessagesByGroup(selectedGroup));
            messagesList.setItems(list);
        }
    }

    public void initializeTable() {
        List<Group> groups = groupService.getGroupsByUser(usrnmValue);
        if (!groups.isEmpty()) {
            groupsTable.setItems(FXCollections.observableArrayList(groups));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            messageColumn.setCellValueFactory(cellData -> {
                Group group = cellData.getValue();
                String lastMessage = msgService.getLastMessage(group);
                return new SimpleStringProperty(lastMessage);
            });

        }
    }

    public void initializeDropList() {
        listaUsers.getItems().clear();
        Optional<User> optionalUser = usrService.getUserByName(usrnmDisplay.getText());

        optionalUser.ifPresent(usr -> {
            usrService.getFriends(usr).forEach(user -> {
                MenuItem menuItem = new MenuItem(user);
                menuItem.setOnAction(event -> friendClicked(user)); // Manejar la selección
                listaUsers.getItems().add(menuItem);
            });
        });
    }
    private void friendClicked(String username) {
        User friend;
        friend = usrService.getUserByName(username).get();
    }

    @FXML
    public void crearClicked() {
        errorCrear.setText("");
        createName.setText("");
        createPwd.setText("");
        pwdRepeat.setText("");
        anyadirFields.setVisible(false);
        modalPane.setVisible(true);
        anyadirAmigoFields.setVisible(false);
        crearFields.setVisible(true);
    }

    @FXML
    public void anyadirClicked() {
        modalPane.setVisible(true);
        crearFields.setVisible(false);
        anyadirAmigoFields.setVisible(false);
        anyadirFields.setVisible(true);
        errorCrear.setText("");
    }

    @FXML
    public void closeModal() {
        modalPane.setVisible(false);
    }

    @FXML
    public void createGroup() {
        Optional<User> optionalUser = usrService.getUserByName(usrnmDisplay.getText());
        if (optionalUser.isPresent()) {
            User expectedUsr = optionalUser.get();
            if (createName.getText().isEmpty() || createPwd.getText().isEmpty() || pwdRepeat.getText().isEmpty() ||
                    !(pwdRepeat.getText().equals(createPwd.getText())) || !groupService.addGroup(new
                    Group(createName.getText(), new ArrayList<>(List.of(expectedUsr)), createPwd.getText(),
                    usrnmDisplay.getText(), false, LocalDateTime.now()))) {
                errorCrear.setText(Constantes.E_CAMPOS_GENERICO);
            } else {
                errorCrear.setText("");
                closeModal();
                initializeTable();
            }
        }
    }

    @FXML
    public void entrarClicked() {
        Optional<User> usr = usrService.getUserByName(usrnmDisplay.getText());

        if (usr.isPresent() && groupService.logIn(logInLink.getText() , logInPwd.getText(), usr.get()
                )) {
            initializeTable();
            closeModal();
        } else {
            errorCrear.setText(Constantes.E_CAMPOS_GENERICO);
        }
    }

    @FXML
    public void sendMsg() {
        if (msgService.addNewMessage(new Message(msgField.getText(), LocalDateTime.now(), usrnmValue,
                nombreGrupo.getText()))) {
            msgField.setText("");
            groupClicked();
        }else {
            log.error(Constantes.E_MANDAR_MENSAJE);
        }
    }

    @FXML
    public void addFriend() {
        User current = usrService.getUserByName(usrnmValue).orElse(null);

        if (usrService.addFriend(current, newFriend.getText()) && current != null) {
            groupService.addPrivateGroup(current, Objects.requireNonNull(usrService.getUserByName(newFriend.getText())
                    .orElse(null)));
            initializeTable();
            initializeDropList();
            closeModal();
        } else {
            errorCrear.setText(Constantes.E_CAMPOS_GENERICO);
        }
    }

    @FXML
    public void showAddFriend() {
        errorCrear.setText("");
        createName.setText("");
        createPwd.setText("");
        pwdRepeat.setText("");
        anyadirFields.setVisible(false);
        modalPane.setVisible(true);
        crearFields.setVisible(false);
        anyadirAmigoFields.setVisible(true);
        errorCrear.setVisible(true);
    }
}
