package org.example.chatssecretos.domain.service;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.chatssecretos.domain.dao.impl.DaoUserImpl;
import org.example.chatssecretos.domain.modelo.User;

import java.util.Optional;


public class UserService {

    private DaoUserImpl daoUser = new DaoUserImpl();

    public Boolean checkNewPassword(PasswordField signUpPwdField, PasswordField pwdFieldRepeat) {
        return signUpPwdField.getText().equals(pwdFieldRepeat.getText());
    }

    public boolean checkNewUsrnm(TextField signUpUsername) {
        return daoUser.getUsers().stream().filter(u -> u.getName().equalsIgnoreCase(signUpUsername.getText()))
                .findFirst().isEmpty();
    }

    public boolean createUsr(PasswordField pwd, TextField email, TextField signUpUsername) {
        if (checkNewUsrnm(signUpUsername))
            return daoUser.addUser(new User(signUpUsername.getText(), email.getText(),pwd.getText()));
        else
            return false;
    }

    public boolean logIn(User user) {
        return daoUser.getUsers().stream()
                .anyMatch(u -> u.getName().equals(user.getName()) && u.getPwd().equals(user.getPwd()));
    }

    public Optional<User> getUserByName(Label usrnm) {
        return daoUser.getUsers().stream().filter(u -> u.getName().equalsIgnoreCase(usrnm.getText()))
                .findFirst();
    }
}
