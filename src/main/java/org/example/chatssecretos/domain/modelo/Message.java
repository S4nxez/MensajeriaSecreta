package org.example.chatssecretos.domain.modelo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class Message {
    private final String text;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private final LocalDateTime timestamp;
    private final String sender;
    private final String grupo;

    @Override
    public String toString(){
        return sender + " -> " + text + "(hace " + Duration.between(timestamp, LocalDateTime.now()).toHours() + " horas)";
    }
}
