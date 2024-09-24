package org.example.chatssecretos.domain.dao.impl;


import org.example.chatssecretos.domain.modelo.Message;

import java.util.ArrayList;
import java.util.List;

public class DaoMessageImpl {
    private List<Message> database = new ArrayList<>();

    public DaoMessageImpl() {
        //database.add(new Message("Hola", LocalDateTime.now(), new User("Dani", "1234"), ,"GrupoZt"));
    }
}
