package org.example.chatssecretos.domain.dao.impl;

import org.example.chatssecretos.domain.dao.DaoUser;
import org.example.chatssecretos.domain.modelo.User;

import java.util.ArrayList;
import java.util.List;


public class DaoUserImpl implements DaoUser {
    private List<User> users;
    private Database db;

    public DaoUserImpl() {
        this.db = new Database();
        this.users = db.loadUsers();
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

}
