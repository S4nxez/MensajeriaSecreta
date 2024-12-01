package org.example.chatssecretos.dao.impl;

import io.vavr.control.Either;
import org.example.chatssecretos.dao.DaoGroup;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.errors.ErrorAppGroup;
import org.example.chatssecretos.domain.modelo.Group;
import org.example.chatssecretos.domain.modelo.User;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class DaoGroupImpl implements DaoGroup {

    private final Database db;

    public DaoGroupImpl(Database database) {
        this.db = database;
    }

    @Override
    public Either<ErrorApp, Void> addGroup(Group group) {
        return db.loadGroups().flatMap(groups ->
            groups.stream()
                    .filter(g -> g.getNombre().equals(group.getNombre())).findFirst()
                    .<Either<ErrorApp, Void>>map(g -> Either.left(ErrorAppGroup.GROUP_NAME_NOT_AVAILABLE))
                    .orElseGet(() -> {
                        groups.add(group);
                        return db.saveGroups(groups);
                    }));
    }

    @Override
    public Either<ErrorApp, List<Group>> getGroups() {
        return db.loadGroups();
    }

    @Override
    public Either<ErrorApp, Void> updateGroup(Group updatedGroup) {
        return db.loadGroups().flatMap(groups ->
            groups.stream()
                    .filter(g -> g.getNombre().equals(updatedGroup.getNombre())).findFirst()
                    .map(Either::<ErrorApp, Group>right).orElseGet(() -> Either.left(ErrorAppGroup.GROUP_NOT_FOUND))
                    .flatMap(group ->
                db.deleteGroup(group).flatMap(result -> addGroup(updatedGroup)))
        );
    }

    @Override
    public Either<ErrorApp, List<Group>> getGroupsByUser(User user) {
        return db.loadGroups().map(grupos-> grupos.stream()
                        .filter( grupo-> grupo.getMiembros().contains(user)).toList())
                .map(groups -> groups.isEmpty() ? Collections.emptyList() : groups);
    }
}
