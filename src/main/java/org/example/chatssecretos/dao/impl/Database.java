package org.example.chatssecretos.dao.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.vavr.control.Either;
import lombok.extern.log4j.Log4j2;
import org.example.chatssecretos.domain.modelo.Group;
import org.example.chatssecretos.domain.modelo.Message;
import org.example.chatssecretos.domain.modelo.PrivateGroup;
import org.example.chatssecretos.domain.modelo.User;
import org.example.chatssecretos.utils.Constantes;
import org.example.chatssecretos.utils.config.Configuration;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

@Component
@Log4j2
public class Database {
    private final Gson gson;

    private final Configuration config;

    public Database(Gson gson, Configuration config) {
        this.gson = gson;
        this.config = config;
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

    public List<Group> loadGroups() {
        Type groupListType = new TypeToken<ArrayList<Group>>() {
        }.getType();

        List<Group> groups = null;
        try {
            groups = gson.fromJson(new FileReader(config.getPathGroups()), groupListType);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        return groups;
    }

    public boolean saveGroups(List<Group> groups) {
        try (FileWriter w = new FileWriter(config.getPathGroups())) {
            gson.toJson(groups, w);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    public boolean deleteGroup(Group oldGroup) {
        List<Group> groups = null;
        Type groupListType = new TypeToken<ArrayList<Group>>() {
        }.getType();

        try {
            groups = gson.fromJson(new FileReader(config.getPathGroups()), groupListType);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        if (groups == null) {
            log.error(Constantes.E_LISTA_USUARIOS_NULA);
            return false;
        }
        return groups.remove(oldGroup) && saveGroups(groups);
    }

    public List<Message> loadMessage() {
        List<Message> messages = null;
        Type messageListType = new TypeToken<ArrayList<Message>>() {
        }.getType();

        try {
            messages = gson.fromJson(new FileReader(config.getPathMessages()), messageListType);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        return messages;
    }

    public boolean saveMessage(List<Message> messages) {
        try (FileWriter w = new FileWriter(config.getPathMessages())) {
            gson.toJson(messages, w);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    public boolean deleteUser(User oldUser) {
        List<User> users = null;
        Type groupListType = new TypeToken<ArrayList<User>>() {
        }.getType();

        try {
            users = gson.fromJson(new FileReader(config.getPathUsers()), groupListType);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        }

        if (users == null) {
            log.error(Constantes.USERS_LIST_IS_NULL);
            return false;
        }
        return users.remove(oldUser) && saveUsers(users);
    }

    public Either<String, List<PrivateGroup>> savePrivateGroups(List<PrivateGroup> groups) {
        try (FileWriter w = new FileWriter(config.getPathPrivateGroups())) {
            gson.toJson(groups, w);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Either.left(Constantes.E_GUARDAR_GRUPO);
        }
        return Either.right(groups);
    }

    public List<PrivateGroup> loadPrivateGroups() {
        List<PrivateGroup> privateGroups = null;
        Type privateGroupListType = new TypeToken<ArrayList<PrivateGroup>>() {
        }.getType();

        try {
            privateGroups = gson.fromJson(new FileReader(config.getPathPrivateGroups()), privateGroupListType);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        return privateGroups;
    }
}
