package org.example.chatssecretos.ui;

import io.vavr.control.Either;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.example.chatssecretos.domain.errors.*;
import org.example.chatssecretos.domain.modelo.*;
import org.example.chatssecretos.domain.service.GroupService;
import org.example.chatssecretos.domain.service.MessageService;
import org.example.chatssecretos.domain.service.PrivateGroupService;
import org.example.chatssecretos.domain.service.UserService;
import org.example.chatssecretos.utils.Constantes;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Component
public class MainMenuController implements Initializable {

    private final MessageService messageService;
    @FXML
    private AnchorPane pane;
    @FXML
    private AnchorPane modalPane;
    @FXML
    private AnchorPane crearFields;
    @FXML
    private SplitMenuButton listaUsers;
    @FXML
    private AnchorPane anyadirAmigoFields;
    @FXML
    private TextField newFriend;
    @FXML
    private TextField groupName;
    @FXML
    private AnchorPane anyadirGrupoPrivadoFields;
    @FXML
    private Button showPrivateModal;
    @FXML
    private HBox hboxItem;
    @FXML
    public TextField privateGroupName;
    @FXML
    private AnchorPane modalError;
    @FXML
    private Label errorLabelModal;
    @FXML
    private AnchorPane blockPwd;
    @FXML
    private Label groupNameModal;
    @FXML
    private PasswordField privateGroupPwdDecrypt;
    @FXML
    private Label errorPwdGroup;
    @FXML
    private Button sendCrypted;
    @FXML
    private AnchorPane anyadirFields;
    @FXML
    private TextField createName;
    @FXML
    private PasswordField createPwd;
    @FXML
    private PasswordField pwdRepeat;
    @FXML
    private Label usrnmDisplay;
    @FXML
    private Label errorCrear;
    @FXML
    private TableView<Object> groupsTable;
    @FXML
    private TableColumn<Object, String> nameColumn;
    @FXML
    private TableColumn<Object, String> messageColumn;
    @FXML
    private PasswordField logInPwd;
    @FXML
    private TextField logInLink;
    @FXML
    private Label nombreGrupo;
    @FXML
    private ListView<String> messagesList;
    @FXML
    private TextField msgField;
    @FXML
    private Button sendNormal;

    private String usernameValue;
    @Setter
    private String userPassword;


    private final GroupService groupService;
    private final UserService userService;
    private final MessageService msgService;
    private final PrivateGroupService privateGroupService;
    private String privateGroupPwdDecryptValue;
    private String actualGroup = "";

    public MainMenuController(GroupService groupService, UserService userService,
                              MessageService msgService, PrivateGroupService privateGroupService, MessageService messageService) {
        this.groupService = groupService;
        this.userService = userService;
        this.msgService = msgService;
        this.privateGroupService = privateGroupService;
        this.messageService = messageService;
    }

    public void setUsername(String username) {
        usrnmDisplay.setText(username);
        this.usernameValue = username;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        groupsTable.setOnMouseClicked((MouseEvent event) -> groupClicked());
    }

    private void mostrarError(ErrorApp errorText) {
        pane.setCursor(Cursor.DEFAULT);
        switch (errorText) {
            case ErrorAppDataBase e -> {
                switch (e) {
                    case ERROR_DATABASE -> showModalError(Constantes.E_BBDD);
                    case NO_CONNECTION -> showModalError(Constantes.E_BBDD_CONEXION);
                    case ERROR_LEER_FICHEROS -> showModalError(Constantes.E_FICHEROS);
                    case ERROR_HILOS -> showModalError(Constantes.E_HILO);
                }
            }
            case ErrorAppUser e -> {
                switch (e) {
                    case NO_MATCHING_CREDENTIALS -> errorCrear.setText(Constantes.E_CREDENTIALS);
                    case USERNAME_NOT_AVAILABLE -> errorCrear.setText(Constantes.E_NOMBRE_USADO);
                    case PASSWORDS_NOT_MATCH -> errorCrear.setText(Constantes.E_REPETIR_CONTRASENYA);
                    case USUARIO_NO_EXISTE -> errorCrear.setText(Constantes.E_USUARIO_NO_EXISTE);
                    case CAMPOS_INCOMPLETOS -> showModalError(Constantes.E_CAMPOS_INCOMPLETOS);
                    case YA_ES_AMIGO -> errorCrear.setText(Constantes.E_YA_ES_AMIGO);
                }
            }
            case ErrorAppGroup e -> {
                switch (e) {
                    case GROUP_NAME_NOT_AVAILABLE -> errorCrear.setText(Constantes.E_NOMBRE_GRUPO);
                    case GROUP_NOT_FOUND -> errorCrear.setText(Constantes.E_GRUPO_NO_ENCONTRADO);
                    case NO_MATCHING_GROUP -> errorCrear.setText(Constantes.E_CREDENTIALS);
                    case CREATE_FIELDS_INCORRECT -> errorCrear.setText(Constantes.E_CAMPOS_GENERICO);
                    case INCORRECT_PASSWORD -> errorPwdGroup.setText(Constantes.E_CREDENTIALS);
                }
            }
            case ErrorAppMessages e -> {
                switch (e) {
                    case MESSAGES_NOT_FOUND -> showModalError(Constantes.E_MANDAR_MENSAJE);
                }
            }
            case ErrorAppSecurity e -> {
                switch (e) {
                    case E_PEDIR_CLAVE_PRIVADA -> showModalError(Constantes.E_PEDIR_CLAVE_PRIVADA);
                    case E_DESENCRIPTAR_MENSAJE -> showModalError(Constantes.E_DESENCRIPTAR_EL_MENSAJE);
                    case E_ENCRIPTAR_MENSAJE -> showModalError(Constantes.E_ENCRIPTAR_MENSAJE);
                    case E_GENERANDO_CLAVES -> showModalError(Constantes.E_GENERANDO_CLAVES);
                    case ERROR_DESENCRIPTAR_MENSAJE -> showModalError(Constantes.E_DESENCRIPTAR_MENSAJE);
                }
            }
        }
    }

    private void groupClicked() {
        ObservableList<String> list;
        Object selectedGroup;

        if (groupsTable.getSelectionModel().getSelectedItem() != null) {
            msgField.setVisible(true);
            selectedGroup = groupsTable.getSelectionModel().getSelectedItem();
            if (selectedGroup instanceof Group group) {
                if (group.isPrivateChat()) {
                    actualGroup = "";
                    blockPwd.setVisible(false);
                    showPrivateModal.setVisible(false);
                    nombreGrupo.setText(Constantes.CHAT_CON + group.getNombre());
                    sendNormal.setVisible(true);
                    sendCrypted.setVisible(true);
                } else {
                    nombreGrupo.setText(((Group) selectedGroup).getNombre());
                    if (!actualGroup.equals(nombreGrupo.getText()))
                        blockPwd.setVisible(true);
                    else
                        decryptMessages();
                    groupNameModal.setText(group.getNombre());
                    showPrivateModal.setVisible(true);
                    sendNormal.setVisible(false);
                    sendCrypted.setVisible(true);
                }
                list = FXCollections.observableArrayList(msgService.getMessagesByGroup(group)
                        .get().stream().map(Message::toString).toList());
            } else {
                actualGroup = "";
                showPrivateModal.setVisible(false);
                blockPwd.setVisible(false);
                sendNormal.setVisible(true);
                sendCrypted.setVisible(false);
                nombreGrupo.setText(((PrivateGroup) selectedGroup).getNombre());
                getAsymetricMessages();
                list = null;
            }
            messagesList.setItems(list);
        }
    }

    private void getAsymetricMessages() {
        Platform.runLater(() -> pane.setCursor(Cursor.WAIT));

        messageService.getDecryptedMessagesByPrivateGroupName(nombreGrupo.getText(), usernameValue, userPassword)
                .thenAcceptAsync(ret -> {
                    if (ret.isRight()) {
                        Platform.runLater(() -> {
                            ObservableList<String> messages = FXCollections.observableArrayList(
                                    ret.get().stream().map(PrivateMessage::toString).toList()
                            );
                            messagesList.setItems(messages);
                        });
                    } else
                        Platform.runLater(() -> mostrarError(ret.getLeft()));
                })
                .whenComplete((result, ex) -> Platform.runLater(() -> pane.setCursor(Cursor.DEFAULT)));
    }

    public void initializeTable() {
        pane.setCursor(Cursor.WAIT);
        groupService.getCombinedGroups(usernameValue).thenAcceptAsync(result ->
                result.peek(groups -> {
                    if (!groups.isEmpty()) {
                        Platform.runLater(() -> {
                        groupsTable.setItems(FXCollections.observableArrayList(groups));
                        nameColumn.setCellValueFactory(new PropertyValueFactory<>(Constantes.NOMBRE));
                        });
                        messageColumn.setCellValueFactory(cellData -> {
                            Object obj = cellData.getValue();

                            SimpleStringProperty property = new SimpleStringProperty(Constantes.CARGANDO);
                            CompletableFuture<Either<ErrorApp, String>> lastMessage = (obj instanceof Group group)
                                    ? msgService.getLastMessage(group)
                                    : msgService.getLastMessage((PrivateGroup) obj);
                            lastMessage.thenAccept(either ->
                                    Platform.runLater(() -> {
                                        either.peek(property::set)
                                                .peekLeft(this::mostrarError);
                                        groupsTable.refresh();
                                    })
                            );
                            return property;
                        });
                    } else
                        log.warn(Constantes.E_LISTA_GRUPOS_NULA);
                    pane.setCursor(Cursor.DEFAULT);
                }).peekLeft(this::mostrarError)
        );
    }

    public void initializeDropList() {
        pane.setCursor(Cursor.WAIT);
        listaUsers.getItems().clear();
        userService.getUserByName(usernameValue).thenAcceptAsync(result ->
                        result.peek(user -> user.getFriends().forEach(friend -> {
                                    MenuItem menuItem = new MenuItem(friend);
                                    menuItem.setOnAction(event -> cargarAmigo(friend));
                                    listaUsers.getItems().add(menuItem);
                                    pane.setCursor(Cursor.DEFAULT);
                                }))
                                .peekLeft(this::mostrarError))
                .exceptionally(ex -> {
                    mostrarError(ErrorAppDataBase.ERROR_HILOS);
                    log.error(Constantes.E_HILO, ex);
                    return null;
                });
    }

    private void cargarAmigo(String friendUsername) {
        pane.setCursor(Cursor.WAIT);
        userService.getUserByName(friendUsername).thenAcceptAsync(result -> {
                    result.peek(this::friendClicked)
                            .peekLeft(this::mostrarError);
                    pane.setCursor(Cursor.DEFAULT);
                })
                .exceptionally(ex -> {
                    mostrarError(ErrorAppDataBase.ERROR_HILOS);
                    log.error(Constantes.E_HILO, ex);
                    return null;
                });
    }

    private void friendClicked(User friend) {
        pane.setCursor(Cursor.WAIT);
        userService.getUserByName(usernameValue)
                .thenComposeAsync(result -> result.fold(
                        error -> {
                            mostrarError(error);
                            return CompletableFuture.completedFuture(Either.left(error));
                        },
                        user -> groupService.getPrivateChats(friend, user)
                ))
                .thenAcceptAsync(groupResult -> groupResult.peekLeft(this::mostrarError)
                        .peek(group ->
                                Platform.runLater(() -> {
                                    actualizarUIParaChatPrivado(group);
                                    cargarMensajesDelGrupo(group);
                                    pane.setCursor(Cursor.DEFAULT);
                                })
                        ))
                .exceptionally(ex -> {
                    log.error(Constantes.E_HILO, ex);
                    mostrarError(ErrorAppDataBase.ERROR_HILOS);
                    return null;
                });
    }


    @FXML
    private void createGroup() {
        pane.setCursor(Cursor.WAIT);
        userService.getUserByName(usernameValue).thenAcceptAsync(result ->
                result.flatMap(user -> {
                            if (createName.getText().isEmpty() || createPwd.getText().isEmpty() ||
                                    pwdRepeat.getText().isEmpty() || !(pwdRepeat.getText().equals(createPwd.getText())))
                                return Either.left(ErrorAppGroup.CREATE_FIELDS_INCORRECT);
                            return groupService.addGroup(new Group(createName.getText(), List.of(user),
                                    createPwd.getText(), usernameValue, false, LocalDateTime.now()));
                        }).peekLeft(this::mostrarError)
                        .peek(group -> {
                            errorCrear.setText(null);
                            closeModal();
                            initializeTable();
                            pane.setCursor(Cursor.DEFAULT);
                        })).exceptionally(ex -> {
            log.error(Constantes.E_HILO, ex);
            mostrarError(ErrorAppDataBase.ERROR_HILOS);
            return null;
        });
    }

    @FXML
    private void entrarClicked() {
        Platform.runLater(() -> pane.setCursor(Cursor.WAIT));
        userService.entrarClickedUser(usernameValue, logInLink.getText(), logInPwd.getText())
                .thenAcceptAsync(result ->
                        result.peek(v ->
                                        Platform.runLater(() -> {
                                            initializeTable();
                                            closeModal();
                                        })
                                )
                                .peekLeft(e -> Platform.runLater(() -> mostrarError(e)))
                ).whenComplete((v, e)->Platform.runLater(() -> pane.setCursor(Cursor.DEFAULT)));
    }

    @FXML
    private void sendMsg() {
        pane.setCursor(Cursor.WAIT);
        msgService.addMessage(new PrivateMessage(usernameValue, LocalDateTime.now(),
                                msgField.getText(), msgField.getText(), null,
                                nombreGrupo.getText()),
                        nombreGrupo.getText())
                .thenAcceptAsync(result -> result
                        .peek(v ->
                            Platform.runLater(() -> {
                                msgField.setText(null);
                                groupClicked();
                                pane.setCursor(Cursor.DEFAULT);
                            })
                        ).peekLeft(e -> Platform.runLater(() -> mostrarError(e))));
    }

    @FXML
    private void addFriend() {
        pane.setCursor(Cursor.WAIT);
        userService.getUserByName(usernameValue).thenAcceptAsync(result ->
                        result.flatMap(user -> userService.addFriend(user, newFriend.getText())
                                        .map(friend -> groupService.addPrivateChat(user, friend)))
                                .peek(v ->
                                        Platform.runLater(() -> {
                                            closeModal();
                                            initializeTable();
                                            initializeDropList();
                                        })
                                )
                                .peekLeft(e -> Platform.runLater(() -> mostrarError(e))))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        mostrarError(ErrorAppDataBase.ERROR_HILOS);
                        log.error(Constantes.E_HILO, ex);
                    });
                    return null;
                });
    }

    @FXML
    private void showNewPrivateGroup() {
        pane.setCursor(Cursor.WAIT);
        modalPane.setVisible(true);
        anyadirGrupoPrivadoFields.setVisible(true);
        hboxItem.getChildren().clear();

        groupService.getMiembrosByGroupName(nombreGrupo.getText()).thenAcceptAsync(result ->
                        result.peek(users -> {
                                            users.forEach(u ->
                                                    Platform.runLater(() ->
                                                            hboxItem.getChildren().add(new CheckBox(u.getName())
                                                            )
                                                    )
                                            );
                                            pane.setCursor(Cursor.DEFAULT);
                                        }
                                )
                                .peekLeft(e -> Platform.runLater(() -> mostrarError(e))))
                .exceptionally(ex -> {
                    log.error(Constantes.E_HILO, ex);
                    Platform.runLater(() -> mostrarError(ErrorAppDataBase.ERROR_HILOS));
                    return null;
                });
    }

    @FXML
    private void addPrivateGroup() {
        List<User> selectedUsers = new ArrayList<>();
        pane.setCursor(Cursor.WAIT);

        for (Node node : hboxItem.getChildren()) {
            if (node instanceof CheckBox checkBox && checkBox.isSelected()) {
                userService.getUserByName(checkBox.getText()).thenAcceptAsync(result ->
                                result.peek(selectedUsers::add)
                                        .peekLeft(this::mostrarError)
                        )
                        .exceptionally(ex -> {
                            log.error(Constantes.E_HILO, ex);
                            mostrarError(ErrorAppDataBase.ERROR_HILOS);
                            return null;
                        });
            }
        }
        privateGroupService.addNew(new PrivateGroup(privateGroupName.getText(), new ArrayList<>(selectedUsers),
                usernameValue, LocalDateTime.now())).thenAcceptAsync(result -> result.peek(v ->
                            Platform.runLater(() -> {
                                privateGroupName.setText(null);
                                closeModal();
                                initializeTable();
                                pane.setCursor(Cursor.DEFAULT);
                            })
                        )
                        .peekLeft(this::mostrarError)
        );
    }

    @FXML
    private void decryptMessages() {
        privateGroupPwdDecryptValue = privateGroupPwdDecrypt.getText();
        pane.setCursor(Cursor.WAIT);
        msgService.getDecryptedMessagesByGroupName(nombreGrupo.getText(), privateGroupPwdDecryptValue)
                .thenApply(result -> result.map(FXCollections::observableArrayList))
                .thenAcceptAsync(result ->
                        result.peek(messages ->
                                Platform.runLater(() -> {
                                    blockPwd.setVisible(false);
                                    errorPwdGroup.setText(null);
                                    actualGroup = nombreGrupo.getText();
                                    messagesList.setItems(FXCollections.observableArrayList(messages
                                            .stream().map(Message::toString).toList()));
                                    pane.setCursor(Cursor.DEFAULT);
                                })
                        ).peekLeft(ex -> Platform.runLater(() -> mostrarError(ex)))
                )
                .exceptionally(ex -> {
                    mostrarError(ErrorAppDataBase.ERROR_HILOS);
                    return null;
                });
    }

    @FXML
    private void sendMsgCrypted() {
        pane.setCursor(Cursor.WAIT);
        msgService.addMessage(new Message(msgField.getText(), LocalDateTime.now(),
                        usernameValue, nombreGrupo.getText()), privateGroupPwdDecryptValue)
                .thenAcceptAsync(result ->
                        result.peek(success ->
                                Platform.runLater(() -> {
                                    msgField.setText(null);
                                    groupClicked();
                                    pane.setCursor(Cursor.DEFAULT);
                                })
                        ).peekLeft(error -> {
                            log.error(Constantes.E_MANDAR_MENSAJE);
                            Platform.runLater(() -> mostrarError(error));
                        })
                );
    }

    private void actualizarUIParaChatPrivado(Group group) {
        msgField.setVisible(true);
        sendNormal.setVisible(true);
        showPrivateModal.setVisible(false);
        nombreGrupo.setText(Constantes.CHAT_CON + group.getNombre());
    }

    private void cargarMensajesDelGrupo(Group group) {
        msgService.getMessagesByGroup(group)
                .peekLeft(this::mostrarError)
                .peek(messages -> messagesList.setItems(
                        FXCollections.observableArrayList(messages.stream().map(Message::toString)
                                .toList())));
    }

    @FXML
    private void closeModalError() {
        modalError.setVisible(false);
    }

    private void showModalError(String errorMensaje) {
        modalError.setVisible(true);
        errorLabelModal.setText(errorMensaje);
    }

    @FXML
    private void showAddFriend() {
        errorCrear.setText(null);
        createName.setText(null);
        createPwd.setText(null);
        pwdRepeat.setText(null);
        anyadirFields.setVisible(false);
        crearFields.setVisible(false);
        anyadirGrupoPrivadoFields.setVisible(false);
        modalPane.setVisible(true);
        anyadirAmigoFields.setVisible(true);
        errorCrear.setVisible(true);
    }

    @FXML
    private void crearClicked() {
        errorCrear.setText(null);
        createName.setText(null);
        createPwd.setText(null);
        pwdRepeat.setText(null);
        anyadirFields.setVisible(false);
        anyadirGrupoPrivadoFields.setVisible(false);
        modalPane.setVisible(true);
        anyadirAmigoFields.setVisible(false);
        crearFields.setVisible(true);
    }

    @FXML
    private void anyadirClicked() {
        modalPane.setVisible(true);
        crearFields.setVisible(false);
        anyadirAmigoFields.setVisible(false);
        anyadirGrupoPrivadoFields.setVisible(false);
        anyadirFields.setVisible(true);
        errorCrear.setText(null);
    }

    @FXML
    private void closeModal() {
        anyadirGrupoPrivadoFields.setVisible(false);
        anyadirAmigoFields.setVisible(false);
        anyadirFields.setVisible(false);
        crearFields.setVisible(false);
        modalPane.setVisible(false);
    }
}
