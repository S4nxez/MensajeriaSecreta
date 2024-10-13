package org.example.chatssecretos.dao.impl;

import io.vavr.control.Either;
import org.example.chatssecretos.dao.DaoPrivateGroup;
import org.example.chatssecretos.domain.modelo.PrivateGroup;
import org.example.chatssecretos.utils.Constantes;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DaoPrivateGroupImpl implements DaoPrivateGroup {

    private final Database db;

    public DaoPrivateGroupImpl(Database db) {
        this.db = db;
    }

    @Override
    public Either<String, PrivateGroup> addNew(PrivateGroup group) {
        List<PrivateGroup> groups = db.loadPrivateGroups();

        if (groups == null)
            groups = new ArrayList<>();
        if (groups.stream().anyMatch(g -> g.getNombre().equals(group.getNombre())))
            return Either.left(Constantes.E_NOMBRE_USADO);
        groups.add(group);
        db.savePrivateGroups(groups);
        return Either.right(group);
    }

    @Override
    public List<PrivateGroup> getPrivateGroups() {
        return db.loadPrivateGroups();
    }
}
