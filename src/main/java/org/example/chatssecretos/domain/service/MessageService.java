package org.example.chatssecretos.domain.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.chatssecretos.domain.dao.impl.DaoMessageImpl;
import org.example.chatssecretos.domain.modelo.Group;
import org.example.chatssecretos.domain.modelo.Message;

import java.util.List;

public class MessageService {
    DaoMessageImpl msgDao = new DaoMessageImpl();

    public ObservableList getMessagesByGroup(Group group) {
        return FXCollections.observableArrayList(msgDao.getMessage().stream().filter(message -> message.getGrupo()
                .equals(group.getNombre())).toList());
    }

    public void addNewMessage(Message message) {
        msgDao.addMessage(message);
    }

    public String getLastMessage(Group group) {
        List<Message> messages = getMessagesByGroup(group);
        if (messages.isEmpty()) {
            return "No messages";
        }
        return messages.getLast().getText();
    }
}
