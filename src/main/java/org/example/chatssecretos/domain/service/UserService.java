package org.example.chatssecretos.domain.service;

import org.example.chatssecretos.dao.impl.DaoUserImpl;
import org.example.chatssecretos.domain.modelo.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class UserService {

    private final DaoUserImpl daoUser = new DaoUserImpl();

    public Boolean checkNewPassword(String signUpPwdField, String pwdFieldRepeat) {
        return signUpPwdField.equals(pwdFieldRepeat);
    }

    public boolean checkNewUsername(String signUpUsername) {
        return daoUser.getUsers().stream().noneMatch(user -> user.getName().equalsIgnoreCase(signUpUsername));
    }

    public boolean createUser(User usr) {
        if (checkNewUsername(usr.getName()))
            return daoUser.addUser(usr);
        else
            return false;
    }

    public boolean logIn(User user) {
        return daoUser.getUsers().stream().anyMatch(u -> u.getName().equals(user.getName()) &&
                u.getPwd().equals(user.getPwd()));
    }

    public Optional<User> getUserByName(String username) {
        return daoUser.getUsers().stream().filter(u -> u.getName().equalsIgnoreCase(username)).findFirst();
    }

    public List<String> getFriends(User currentUser) {
        return getUserByName(currentUser.getName()).map(User::getFriends).orElse(Collections.emptyList());
    }

    public boolean addFriend(User initial, String userName){
        Optional<User> friend = getUserByName(userName);

        if(friend.isPresent() && initial.getFriends().stream().noneMatch(f -> f.equals(userName))) {
            initial.getFriends().add(friend.get().getName());
            friend.get().getFriends().add(initial.getName());
            return daoUser.updateUser(initial) && daoUser.updateUser(friend.get());
        }
        return false;
    }
}
