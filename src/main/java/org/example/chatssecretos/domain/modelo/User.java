package org.example.chatssecretos.domain.modelo;

import lombok.*;

@Data
@AllArgsConstructor
public class User {
    private String name;
    private String email;
    private String pwd;
}
