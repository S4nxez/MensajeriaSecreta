package org.example.chatssecretos.dao.impl;

import org.example.chatssecretos.dao.DaoUser;
import org.example.chatssecretos.domain.modelo.User;

import java.util.ArrayList;
import java.util.List;


public class DaoUserImpl implements DaoUser {
    private final Database db;

    public DaoUserImpl() {
        this.db = new Database();
    }

    @Override
    public boolean addUser(User user) {
        List<User> users = db.loadUsers();
        boolean ret;

        if (users == null)
            users = new ArrayList<>();
        users.add(user);
        ret = db.saveUsers(users);
        return ret;
    }

    @Override
    public List<User> getUsers() {
        return db.loadUsers();
    }

    @Override
    public boolean updateUser(User initial) {
        return db.loadUsers().stream().filter(u -> u.getName().equals(initial.getName())).findFirst().map(oldUser ->
                db.deleteUser(oldUser) && addUser(initial)).orElse(false);
    }
}
