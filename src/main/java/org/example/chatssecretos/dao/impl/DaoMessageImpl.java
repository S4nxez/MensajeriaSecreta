package org.example.chatssecretos.dao.impl;


import org.example.chatssecretos.dao.DaoMessage;
import org.example.chatssecretos.domain.modelo.Message;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DaoMessageImpl implements DaoMessage {
    private final Database db;

    public DaoMessageImpl(Database database) {
        this.db = database;
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
