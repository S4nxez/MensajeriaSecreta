package org.example.chatssecretos.domain.modelo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
public class Group {
    private final ArrayList<User> miembros;
    private final String nombre;
    private final String password;
    private final User administrador;
    private final LocalDateTime creationDate;

}
