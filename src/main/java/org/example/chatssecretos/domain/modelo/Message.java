package org.example.chatssecretos.domain.modelo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
public class Message {
    private final String text;
    private final LocalDateTime timestamp;
    private final User sender;
    private final ArrayList<User> receiver;
    private final String grupo;
}
