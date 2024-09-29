package org.example.chatssecretos.dao;

import org.example.chatssecretos.domain.modelo.Message;

import java.util.List;

public interface DaoMessage {

    boolean addMessage(Message msg);

    List<Message> getMessage();

}
