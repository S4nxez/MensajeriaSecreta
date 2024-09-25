package org.example.chatssecretos.domain.service;

import org.example.chatssecretos.domain.dao.impl.DaoGroupImpl;
import org.example.chatssecretos.domain.modelo.Group;
import org.example.chatssecretos.domain.modelo.User;

import java.util.List;
import java.util.Optional;

public class GroupService {
    private final DaoGroupImpl daoGroup = new DaoGroupImpl();

    public boolean checkGroupName(String signUpGroupName) {
        return daoGroup.getGroups() == null || daoGroup.getGroups().stream().filter(g -> g.getNombre().equalsIgnoreCase(signUpGroupName))
                .findFirst().isEmpty();
    }
    public boolean addGroup(Group group) {
        if (checkGroupName(group.getNombre()))
            return daoGroup.addGroup(group);
        else
            return false;
    }

    public List<Group> getGroupsByUser(String usr) {
        if (daoGroup.getGroups() == null)
            return null;
        return daoGroup.getGroups().stream()
                .filter(g -> g.getMiembros().stream()
                        .anyMatch(u -> u.getName().equalsIgnoreCase(usr))).toList();
    }

    public boolean logIn(String text, String logInPwdText, User usr) {
        Optional<Group> group = daoGroup.getGroups().stream().filter(g -> g.getNombre().equalsIgnoreCase(text)).findFirst();
        if (group.isPresent() && group.get().getPassword().equals(logInPwdText) && !group.get().getMiembros().stream()
                .anyMatch(u -> u.getName().equals(usr.getName()))) {
            group.get().getMiembros().add(usr);
            return daoGroup.updateGroup(group);
        }
        return false;
    }
}
