package org.example.chatssecretos.domain.service;

import io.vavr.control.Either;
import org.example.chatssecretos.dao.DaoPrivateGroup;
import org.example.chatssecretos.domain.modelo.PrivateGroup;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PrivateGroupService {
    private final DaoPrivateGroup daoPrivateGroup;

    public PrivateGroupService(DaoPrivateGroup daoPrivateGroup){
        this.daoPrivateGroup = daoPrivateGroup;
    }

    public Either<String, PrivateGroup> addNew(PrivateGroup group) {
        return daoPrivateGroup.addNew(group);
    }

    public List<PrivateGroup> getPrivateGroupsByUsername(String username){
        if (daoPrivateGroup.getPrivateGroups() == null)
            return Collections.emptyList();
        return daoPrivateGroup.getPrivateGroups().stream().filter(g -> g.getMiembros().stream()
                .anyMatch(u -> u.getName().equalsIgnoreCase(username))).toList();
    }
}
