package org.example.chatssecretos.domain.dao.impl;

import org.example.chatssecretos.domain.dao.DaoUser;
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
        boolean ret;
        List<User> users = db.loadUsers();

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
    public boolean updateUsr(User initial) {
        return db.loadUsers().stream().filter(u -> u.getName().equals(initial.getName())).findFirst().map(oldUser ->
                db.deleteUser(oldUser) && addUser(initial)).orElse(false);
    }
}
