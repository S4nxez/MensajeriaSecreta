package org.example.chatssecretos.dao;

import io.vavr.control.Either;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.modelo.Message;
import org.example.chatssecretos.domain.modelo.PrivateMessage;

import java.util.List;

public interface DaoMessage {
    Either<ErrorApp, Void> addMessage(Message msg);

    Either<ErrorApp, List<Message>> getMessage();

    Either<ErrorApp, Void> addPrivateMessage(PrivateMessage msg);

    Either<ErrorApp, List<PrivateMessage>> getPrivateMessage();
}
