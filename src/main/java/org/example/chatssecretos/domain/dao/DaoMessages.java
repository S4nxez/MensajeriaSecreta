package org.example.chatssecretos.domain.dao;


import org.example.chatssecretos.domain.modelo.Message;
import org.example.chatssecretos.domain.modelo.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DaoMessages {
    private List<Message> database = new ArrayList<>();

    public DaoMessages() {
        //database.add(new Message("Hola", LocalDateTime.now(), new User("Dani", "1234"), ,"GrupoZt"));
    }
}
