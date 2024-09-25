package org.example.chatssecretos.domain.dao.impl;

import org.example.chatssecretos.domain.dao.DaoGroup;
import org.example.chatssecretos.domain.modelo.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DaoGroupImpl implements DaoGroup {

    private final Database db;

    public DaoGroupImpl() {
        this.db = new Database();
    }

    @Override
    public boolean addGroup(Group group) {
        List<Group> groups = db.loadGroups();

        if (groups == null)
            groups = new ArrayList<>();
        groups.add(group);
        return db.saveGroups(groups);
    }

    @Override
    public List<Group> getGroups() {
        return db.loadGroups();
    }

    @Override
    public boolean saveGroups(List<Group> groups) {
        return db.saveGroups(groups);
    }

    @Override
    public boolean updateGroup(Optional<Group> updatedGroup) {
        Group oldGroup = db.loadGroups().stream().filter(g -> g.getNombre().equals(updatedGroup.get().getNombre()))
                .findFirst().get();
        return db.deleteGroup(oldGroup) && addGroup(updatedGroup.get());
    }
}
