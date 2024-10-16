package org.example.chatssecretos.domain.service;

import org.example.chatssecretos.dao.impl.DaoGroupImpl;
import org.example.chatssecretos.domain.modelo.Group;
import org.example.chatssecretos.domain.modelo.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class GroupService {
    private final DaoGroupImpl daoGroup;

    public GroupService(DaoGroupImpl daoGroup) {
        this.daoGroup = daoGroup;
    }

    public boolean checkGroupName(String signUpGroupName) {
        return daoGroup.getGroups() == null || daoGroup.getGroups().stream()
                .anyMatch(g -> g.getNombre().equalsIgnoreCase(signUpGroupName));
    }
    public boolean addGroup(Group group) {
        if (checkGroupName(group.getNombre()))
            return daoGroup.addGroup(group);
        else
            return false;
    }

    public List<Group> getGroupsByUser(String usr) {
        if (daoGroup.getGroups() == null)
            return Collections.emptyList();
        return daoGroup.getGroups().stream()
                .filter(g -> g.getMiembros().stream().anyMatch(u -> u.getName().equalsIgnoreCase(usr))).toList();
    }

    public boolean logIn(String groupName, String logInPwdText, User usr) {
        Optional<Group> group = daoGroup.getGroups().stream().filter(g -> g.getNombre().equalsIgnoreCase(groupName)).findFirst();

        if (group.isPresent() && !group.get().isPrivateChat() && group.get().getPassword().equals(logInPwdText) &&
                group.get().getMiembros().stream().noneMatch(u -> u.getName().equals(usr.getName()))) {
            group.get().getMiembros().add(usr);
            return daoGroup.updateGroup(group);
        }
        return false;
    }

    public void addPrivateGroup(User user1, User user2) {
        addGroup(new Group(user1.getName()+":"+user2.getName(), new ArrayList<>(List.of(user1,user2)),
                "","", true, LocalDateTime.now()));
    }

    public Group getGroupByName(String groupName) {
        return daoGroup.getGroups().stream().filter(g -> g.getNombre().equals(groupName)).findFirst().orElse(null);
    }

    public Group getPrivateChats(User user1, User user2) {
        return daoGroup.getGroups().stream().filter(g -> g.getMiembros().contains(user1) &&
                g.getMiembros().contains(user2) && g.isPrivateChat()).findFirst().orElse(null);
    }
}
