package org.example.chatssecretos.domain.dao;

import com.google.gson.Gson;
import javafx.scene.control.TextField;
import lombok.Data;
import org.example.chatssecretos.domain.modelo.User;

import java.util.ArrayList;
import java.util.List;

@Data
public class DaoUser {

    private List<User> database = new ArrayList<>();

    private Gson gson;

    public DaoUser() {
        database.add(new User("Dani","1234"));
        database.add(new User("Javi","1223"));
    }

    public boolean addUser(User user) {
        return database.add(user);
    }
}
