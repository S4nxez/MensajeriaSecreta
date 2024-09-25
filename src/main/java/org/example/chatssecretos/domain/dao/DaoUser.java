package org.example.chatssecretos.domain.dao;

import org.example.chatssecretos.domain.modelo.User;

import java.util.List;

public interface DaoUser {
    boolean addUser(User user);

    List<User> getUsers();
}
