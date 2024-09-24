package org.example.chatssecretos.domain.dao.impl;

import com.google.gson.Gson;
import org.example.chatssecretos.domain.modelo.Group;

import java.util.ArrayList;
import java.util.List;

public class DaoGroupImpl {

    List<Group> database = new ArrayList<>();

    private Gson gson;

    public DaoGroupImpl() {
        //database.add(new Group());
    }

    public boolean addGroup(Group group) {
        return database.add(group);
    }
}
