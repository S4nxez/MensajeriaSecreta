package org.example.chatssecretos.dao.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.log4j.Log4j2;
import org.example.chatssecretos.domain.modelo.Group;
import org.example.chatssecretos.domain.modelo.Message;
import org.example.chatssecretos.domain.modelo.User;
import org.example.chatssecretos.utils.LocalDateTimeDeserializer;
import org.example.chatssecretos.utils.LocalDateTimeSerializer;
import org.example.chatssecretos.utils.config.Configuration;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;

@Log4j2
public class Database {
    private final Gson gson;

    private final Configuration config;

    public Database() {
        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer()).setPrettyPrinting();
        this.gson = gsonBuilder
                .create();
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

    public List<Group> loadGroups() {
        Type groupListType = new TypeToken<ArrayList<Group>>() {
        }.getType();

        List<Group> groups = null;
        try {
            groups = gson.fromJson(
                    new FileReader(config.getPathGroups()),
                    groupListType);
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
            groups = gson.fromJson(
                    new FileReader(config.getPathGroups()),
                    groupListType);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        assert groups != null; //TODO quitar esto q me dijo como pero no me acuerdo
        return groups.remove(oldGroup) && saveGroups(groups);
    }

    public List<Message> loadMessage() {
        List<Message> messages = null;
        Type groupListType = new TypeToken<ArrayList<Message>>() {
        }.getType();

        try {
            messages = gson.fromJson(
                    new FileReader(config.getPathMessages()),
                    groupListType);
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
            users = gson.fromJson(
                    new FileReader(config.getPathUsers()),
                    groupListType);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        assert users != null; //TODO lomismo
        return users.remove(oldUser) && saveUsers(users);
    }
}
