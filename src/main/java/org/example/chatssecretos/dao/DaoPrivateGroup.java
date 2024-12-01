package org.example.chatssecretos.dao;

import io.vavr.control.Either;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.modelo.PrivateGroup;

import java.util.List;

public interface DaoPrivateGroup {
    Either<ErrorApp, Void> addNew(PrivateGroup group);

    Either<ErrorApp, List<PrivateGroup>> getPrivateGroups();
}
