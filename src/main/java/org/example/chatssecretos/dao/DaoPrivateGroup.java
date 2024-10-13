package org.example.chatssecretos.dao;

import io.vavr.control.Either;
import org.example.chatssecretos.domain.modelo.PrivateGroup;

import java.util.List;

public interface DaoPrivateGroup {
    Either<String, PrivateGroup> addNew(PrivateGroup group);

    List<PrivateGroup> getPrivateGroups();
}
