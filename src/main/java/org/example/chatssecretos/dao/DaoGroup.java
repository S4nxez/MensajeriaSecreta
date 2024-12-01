package org.example.chatssecretos.dao;

import io.vavr.control.Either;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.modelo.Group;
import org.example.chatssecretos.domain.modelo.User;

import java.util.List;

public interface DaoGroup {
    Either<ErrorApp, Void> addGroup(Group group);

    Either<ErrorApp, List<Group>> getGroups();

    Either<ErrorApp, Void> updateGroup(Group group);

    Either<ErrorApp, List<Group>> getGroupsByUser(User user);
}
