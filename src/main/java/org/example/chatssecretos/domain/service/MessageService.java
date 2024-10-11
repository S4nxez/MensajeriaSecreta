package org.example.chatssecretos.domain.service;

import org.example.chatssecretos.dao.impl.DaoMessageImpl;
import org.example.chatssecretos.domain.modelo.Group;
import org.example.chatssecretos.domain.modelo.Message;
import org.example.chatssecretos.utils.Constantes;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    DaoMessageImpl msgDao;

    public MessageService(DaoMessageImpl msgDao) {
        this.msgDao = msgDao;
    }

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
