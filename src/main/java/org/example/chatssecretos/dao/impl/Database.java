package org.example.chatssecretos.dao.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.vavr.control.Either;
import lombok.extern.log4j.Log4j2;
import org.example.chatssecretos.domain.errors.*;
import org.example.chatssecretos.domain.modelo.*;
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

    public Either<ErrorApp, List<User>> loadUsers() {
        Type userListType = new TypeToken<ArrayList<User>>() {
        }.getType();

        List<User> users;
        try {
            users = gson.fromJson(
                    new FileReader(config.getPathUsers()),
                    userListType);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            return Either.left(ErrorAppDataBase.ERROR_LEER_FICHEROS);
        }
        if (users == null)
            users = new ArrayList<>();
        return Either.right(users);
    }

    public Either<ErrorApp, Void> saveUsers(List<User> users) {
        try (FileWriter w = new FileWriter(config.getPathUsers())) {
            gson.toJson(users, w);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Either.left(ErrorAppDataBase.ERROR_DATABASE);
        }
        return Either.right(null);
    }

    public Either<ErrorApp,List<Group>> loadGroups() {
        Type groupListType = new TypeToken<ArrayList<Group>>() {
        }.getType();

        List<Group> groups;
        try {
            groups = gson.fromJson(new FileReader(config.getPathGroups()), groupListType);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            return Either.left(ErrorAppDataBase.ERROR_LEER_FICHEROS);
        }
        return Either.right(groups);
    }

    public Either<ErrorApp, Void> saveGroups(List<Group> groups) {
        try (FileWriter w = new FileWriter(config.getPathGroups())) {
            gson.toJson(groups, w);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Either.left(ErrorAppDataBase.ERROR_DATABASE);
        }
        return Either.right(null);
    }

    public Either<ErrorApp, Void> deleteGroup(Group oldGroup) {
        return  loadGroups().flatMap(groups -> {
            if (groups.isEmpty()){
                log.error(Constantes.E_LISTA_GRUPOS_NULA);
                return Either.left(ErrorAppGroup.GROUP_NOT_FOUND);
            }
            groups.remove(oldGroup);
            return saveGroups(groups);
        });
    }

    public Either<ErrorApp, List<Message>> loadMessage() {
        List<Message> messages;
        Type messageListType = new TypeToken<ArrayList<Message>>() {
        }.getType();

        try {
            messages = gson.fromJson(new FileReader(config.getPathMessages()), messageListType);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            return Either.left(ErrorAppMessages.MESSAGES_NOT_FOUND);
        }
        return  Either.right(messages);
    }

    public Either<ErrorApp, List<PrivateMessage>> loadPrivateMessage() {
        List<PrivateMessage> messages;
        Type messageListType = new TypeToken<ArrayList<PrivateMessage>>() {
        }.getType();

        try {
            messages = gson.fromJson(new FileReader(config.getPathPrivateMessages()), messageListType);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            return Either.left(ErrorAppMessages.MESSAGES_NOT_FOUND);
        }
        return  Either.right(messages);
    }

    public Either<ErrorApp, Void> saveMessages(List<Message> messages) {
        try (FileWriter w = new FileWriter(config.getPathMessages())) {
            gson.toJson(messages, w);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Either.left(ErrorAppMessages.MESSAGES_NOT_FOUND);
        }
        return Either.right(null);
    }

    public Either<ErrorApp, Void> deleteUser(User oldUser) {
        return loadUsers().flatMap(users -> {
          users.remove(oldUser);
          return saveUsers(users);
        });
    }

    public Either<ErrorApp, Void> savePrivateGroups(List<PrivateGroup> groups) {
        try (FileWriter w = new FileWriter(config.getPathPrivateGroups())) {
            gson.toJson(groups, w);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Either.left(ErrorAppDataBase.ERROR_DATABASE);
        }
        return Either.right(null);
    }

    public Either<ErrorApp, List<PrivateGroup>> loadPrivateGroups() {
        List<PrivateGroup> privateGroups;
        Type privateGroupListType = new TypeToken<ArrayList<PrivateGroup>>() {
        }.getType();

        try {
            privateGroups = gson.fromJson(new FileReader(config.getPathPrivateGroups()), privateGroupListType);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            return Either.left(ErrorAppDataBase.ERROR_LEER_FICHEROS);
        }
        if (privateGroups == null)
            privateGroups = new ArrayList<>();
        return Either.right(privateGroups);
    }

    public Either<ErrorApp, Void> savePrivateMessages(List<PrivateMessage> messages) {
        try (FileWriter w = new FileWriter(config.getPathPrivateMessages())) {
            gson.toJson(messages, w);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Either.left(ErrorAppMessages.MESSAGES_NOT_FOUND);
        }
        return Either.right(null);
    }
}
