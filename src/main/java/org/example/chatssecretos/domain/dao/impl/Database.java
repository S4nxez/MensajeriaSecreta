package org.example.chatssecretos.domain.dao.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.log4j.Log4j2;
import org.example.chatssecretos.domain.modelo.Group;
import org.example.chatssecretos.domain.modelo.Message;
import org.example.chatssecretos.domain.modelo.User;
import org.example.chatssecretos.utils.config.Configuration;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

@Log4j2
public class Database {
    public List<User> users;
    public List<Group> groups;
    public List<Message> messages;

    private final Gson gson;

    private final Configuration config;

    public Database() {
        this.gson = new Gson();
        this.config = new Configuration();
    }

    public List<User> loadUsers() {
        Type userListType = new TypeToken<ArrayList<User>>() {
        }.getType();

        List<User> users = null;
        try {
            users = gson.fromJson(
                    new FileReader(config.getPathUsers()),
                    userListType);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        return users;
    }

    public boolean saveUsers(List<User> users) {
        try (FileWriter w = new FileWriter(config.getPathUsers())) {
            gson.toJson(users, w);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

}
