package org.example.chatssecretos.dao.impl;


import org.example.chatssecretos.dao.DaoMessage;
import org.example.chatssecretos.domain.modelo.Message;

import java.util.ArrayList;
import java.util.List;

public class DaoMessageImpl implements DaoMessage {
    private final Database db;

    public DaoMessageImpl() {
        this.db = new Database();
    }

    @Override
    public boolean addMessage(Message msg) {
        List<Message> messages = db.loadMessage();

        if (messages == null)
            messages = new ArrayList<>();
        messages.add(msg);
        return db.saveMessage(messages);
    }

    @Override
    public List<Message> getMessage() {
        return db.loadMessage();
    }

}
