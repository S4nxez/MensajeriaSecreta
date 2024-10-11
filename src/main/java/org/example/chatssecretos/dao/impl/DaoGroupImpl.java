package org.example.chatssecretos.dao.impl;

import org.example.chatssecretos.dao.DaoGroup;
import org.example.chatssecretos.domain.modelo.Group;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DaoGroupImpl implements DaoGroup {

    private final Database db;

    public DaoGroupImpl(Database database) {
        this.db = database;
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
    public boolean updateGroup(Optional<Group> updatedGroup) {
        return updatedGroup.map(group -> db.loadGroups().stream().filter(g -> g.getNombre().equals(group.getNombre())).findFirst()
                .map(oldGroup -> db.deleteGroup(oldGroup) && addGroup(group)).orElse(false)).orElse(false);
    }
}
