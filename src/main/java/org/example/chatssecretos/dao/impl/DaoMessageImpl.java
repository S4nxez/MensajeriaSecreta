package org.example.chatssecretos.dao.impl;


import io.vavr.control.Either;
import org.example.chatssecretos.dao.DaoMessage;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.errors.ErrorAppMessages;
import org.example.chatssecretos.domain.modelo.Message;
import org.example.chatssecretos.domain.modelo.PrivateMessage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DaoMessageImpl implements DaoMessage {
    private final Database db;

    public DaoMessageImpl(Database database) {
        this.db = database;
    }

    @Override
    public Either<ErrorApp, Void> addMessage(Message msg) {
        return db.loadMessage().flatMap(messages -> {
            messages.add(msg);
            return db.saveMessages(messages);
        });
    }

    @Override
    public Either<ErrorApp, List<Message>> getMessage() {
        return db.loadMessage();
    }

    @Override
    public Either<ErrorApp, Void> addPrivateMessage(PrivateMessage msg) {
        return db.loadPrivateMessage().flatMap(messages -> {
            messages.add(msg);
            return db.savePrivateMessages(messages);
        });
    }

    @Override
    public Either<ErrorApp, List<PrivateMessage>> getPrivateMessage() {
        return db.loadPrivateMessage();
    }
}
