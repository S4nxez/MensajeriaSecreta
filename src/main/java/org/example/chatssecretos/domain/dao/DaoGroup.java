package org.example.chatssecretos.domain.dao;

import org.example.chatssecretos.domain.modelo.Group;
import org.example.chatssecretos.domain.modelo.User;

import java.util.List;
import java.util.Optional;

public interface DaoGroup {

    boolean addGroup(Group group);

    List<Group> getGroups();

    boolean saveGroups(List<Group> groups);

    boolean updateGroup(Optional<Group> group);
}
