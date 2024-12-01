package org.example.chatssecretos.dao;

import io.vavr.control.Either;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.modelo.User;

import java.util.List;

public interface DaoUser {
    Either<ErrorApp, Void> addUser(User user);

    Either<ErrorApp, User> getUserByName(String username);

    Either<ErrorApp, List<User>> getUsers();

    Either<ErrorApp, Void> updateUser(User initial);
}
