package org.example.chatssecretos.domain.modelo;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
public class User {
    private String name;
    private String email;
    private String pwd;
    private List<String> friends;
}
