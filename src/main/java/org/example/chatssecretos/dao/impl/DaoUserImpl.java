package org.example.chatssecretos.dao.impl;

import io.vavr.control.Either;
import org.example.chatssecretos.dao.DaoUser;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.errors.ErrorAppDataBase;
import org.example.chatssecretos.domain.errors.ErrorAppUser;
import org.example.chatssecretos.domain.modelo.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DaoUserImpl implements DaoUser {
    private final Database db;

    public DaoUserImpl(Database database) {
        this.db = database;
    }

    @Override
    public Either<ErrorApp, Void> addUser(User user) {
        return db.loadUsers().flatMap(users -> {
            users.add(user);
            return db.saveUsers(users).map(saved -> null);
        });
    }

    @Override
    public Either<ErrorApp, User> getUserByName(String username) {
        return getUsers().flatMap(users ->
                users.stream().filter(user ->
                                user.getName().equalsIgnoreCase(username))
                        .findFirst()
                        .map(Either::<ErrorApp, User>right)
                        .orElseGet(() -> Either.left(ErrorAppUser.USUARIO_NO_EXISTE)));
    }

    @Override
    public Either<ErrorApp, List<User>> getUsers() {
        return db.loadUsers();
    }

    @Override
    public Either<ErrorApp, Void> updateUser(User initial) {
        return db.loadUsers().flatMap(users -> {
            Optional<User> oldUserOpt = users.stream().filter(u -> u.getName().equals(initial.getName())).findFirst();
            if (oldUserOpt.isPresent()) {
                return db.deleteUser(oldUserOpt.get()).flatMap(deleted -> addUser(initial));
            }
            return Either.left(ErrorAppDataBase.ERROR_DATABASE);
        });
    }
}
