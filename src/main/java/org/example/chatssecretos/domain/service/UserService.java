package org.example.chatssecretos.domain.service;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.chatssecretos.domain.dao.impl.DaoUserImpl;
import org.example.chatssecretos.domain.modelo.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class UserService {

    private final DaoUserImpl daoUser = new DaoUserImpl();

    public Boolean checkNewPassword(PasswordField signUpPwdField, PasswordField pwdFieldRepeat) {
        return signUpPwdField.getText().equals(pwdFieldRepeat.getText());
    }

    public boolean checkNewUsrnm(TextField signUpUsername) {
        return daoUser.getUsers().stream().filter(u -> u.getName().equalsIgnoreCase(signUpUsername.getText()))
                .findFirst().isEmpty();
    }

    public boolean createUsr(PasswordField pwd, TextField email, TextField signUpUsername) {
        if (checkNewUsrnm(signUpUsername))
            return daoUser.addUser(new User(signUpUsername.getText(), email.getText(),pwd.getText(), Collections.emptyList()));
        else
            return false;
    }

    public boolean logIn(User user) {
        return daoUser.getUsers().stream()
                .anyMatch(u -> u.getName().equals(user.getName()) && u.getPwd().equals(user.getPwd()));
    }

    public Optional<User> getUserByName(String usrnm) {
        return daoUser.getUsers().stream().filter(u -> u.getName().equalsIgnoreCase(usrnm))
                .findFirst();
    }

    public List<String> getFriends(User currentUser) {
        return getUserByName(currentUser.getName()).map(User::getFriends).orElse(Collections.emptyList());
    }

    public boolean addFriend(User initial, String userName){
        Optional<User> friend = getUserByName(userName);
        if(friend.isPresent() && initial.getFriends().stream().noneMatch(f -> f.equals(userName))) {
            initial.getFriends().add(friend.get().getName());
            friend.get().getFriends().add(initial.getName());
            return daoUser.updateUsr(initial) && daoUser.updateUsr(friend.get());
        }
        return false;
    }
}
