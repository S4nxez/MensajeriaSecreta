package org.example.chatssecretos.domain.modelo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrivateMessage {
    private String sender;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private  LocalDateTime timestamp;
    private String sign;
    private String encryptedMessage;
    private Map<String, byte[]> symmetricKeysEncrypted;
    private String groupName;

    @Override
    public String toString(){
        return sender + " -> " + encryptedMessage + "(hace " + Duration.between(timestamp, LocalDateTime.now()).toHours() + " horas)";
    }
}
