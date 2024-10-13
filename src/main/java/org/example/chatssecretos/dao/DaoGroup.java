package org.example.chatssecretos.dao;

import org.example.chatssecretos.domain.modelo.Group;

import java.util.List;
import java.util.Optional;

public interface DaoGroup {
    boolean addGroup(Group group);

    List<Group> getGroups();

    boolean updateGroup(Optional<Group> group);
}
