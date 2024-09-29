package org.example.chatssecretos.domain.service;

import org.example.chatssecretos.dao.impl.DaoMessageImpl;
import org.example.chatssecretos.domain.modelo.Group;
import org.example.chatssecretos.domain.modelo.Message;
import org.example.chatssecretos.utils.Constantes;

import java.util.List;

public class MessageService {
    DaoMessageImpl msgDao = new DaoMessageImpl();

    public List<Message> getMessagesByGroup(Group group) {
        return msgDao.getMessage().stream().filter(message -> message.getGrupo().equals(group.getNombre())).toList();
    }

    public boolean addNewMessage(Message message) {
        return msgDao.addMessage(message);
    }

    public String getLastMessage(Group group) {
        List<Message> messages = getMessagesByGroup(group);

        if (messages.isEmpty())
            return Constantes.SIN_MENSAJES;
        return messages.getLast().getText();
    }
}
