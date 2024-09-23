package org.example.chatssecretos.domain.service;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.chatssecretos.domain.dao.DaoUser;
import org.example.chatssecretos.domain.modelo.User;


public class UserService {

    private DaoUser daoUser = new DaoUser();

    public Boolean checkNewPassword(PasswordField signUpPwdField, PasswordField pwdFieldRepeat) {
        return signUpPwdField.getText().equals(pwdFieldRepeat.getText());
    }

    public boolean checkNewUsrnm(TextField signUpUsername) {
        return daoUser.getDatabase().stream().filter(u -> u.getName().equalsIgnoreCase(signUpUsername.getText()))
                .findFirst().isEmpty();
    }


    public boolean createUsr(PasswordField pwd, PasswordField pwdFieldRepeat, TextField signUpUsername) {
        if (!checkNewPassword(pwd, pwdFieldRepeat) && !signUpUsername.getText().isEmpty() && checkNewUsrnm(signUpUsername))
            return daoUser.addUser(new User(signUpUsername.getText(), pwd.getText()));
        else
            return false;
    }

    public boolean logIn(User user) {
        return daoUser.getDatabase().stream()
                .anyMatch(u -> u.getName().equals(user.getName()) && u.getPwd().equals(user.getPwd()));
    }
}
