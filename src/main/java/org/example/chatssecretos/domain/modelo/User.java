package org.example.chatssecretos.domain.modelo;

import lombok.*;

import java.util.List;

@Data
public class User {
    private final String name;
    private final String email;
    private final String pwd;
    private final List<String> friends;
}
