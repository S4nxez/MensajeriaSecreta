package org.example.chatssecretos.dao.impl;

import io.vavr.control.Either;
import org.example.chatssecretos.dao.DaoPrivateGroup;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.errors.ErrorAppGroup;
import org.example.chatssecretos.domain.modelo.PrivateGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DaoPrivateGroupImpl implements DaoPrivateGroup {

    private final Database db;

    public DaoPrivateGroupImpl(Database db) {
        this.db = db;
    }

    @Override
    public Either<ErrorApp, Void> addNew(PrivateGroup group) {
        return db.loadPrivateGroups().flatMap(groups -> {
            if (groups.stream().anyMatch(g -> g.getNombre().equals(group.getNombre())))
                    return Either.left(ErrorAppGroup.GROUP_NAME_NOT_AVAILABLE);
            groups.add(group);
            return db.savePrivateGroups(groups);
        });
    }

    @Override
    public Either<ErrorApp, List<PrivateGroup>> getPrivateGroups() {
        return db.loadPrivateGroups();
    }
}
