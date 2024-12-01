package org.example.chatssecretos.domain.modelo;

import lombok.*;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class User {
    private final String name;
    private final String email;
    private String pwd;
    private final List<String> friends;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email);
    }
}
