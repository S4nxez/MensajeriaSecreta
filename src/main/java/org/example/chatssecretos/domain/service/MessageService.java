package org.example.chatssecretos.domain.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.chatssecretos.domain.dao.impl.DaoMessageImpl;
import org.example.chatssecretos.domain.modelo.Group;
import org.example.chatssecretos.domain.modelo.Message;
import org.example.chatssecretos.utils.Constantes;

import java.util.List;

public class MessageService {
    DaoMessageImpl msgDao = new DaoMessageImpl();

    public ObservableList<Message> getMessagesByGroup(Group group) {
        return FXCollections.observableArrayList(msgDao.getMessage().stream().filter(message -> message.getGrupo()
                .equals(group.getNombre())).toList());
    }

    public boolean addNewMessage(Message message) {
        return msgDao.addMessage(message);
    }

    public String getLastMessage(Group group) {
        List<Message> messages = getMessagesByGroup(group);
        if (messages.isEmpty()) {
            return Constantes.SIN_MENSAJES;
        }
        return messages.getLast().getText();
    }
}
