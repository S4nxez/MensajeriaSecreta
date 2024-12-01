package org.example.chatssecretos.domain.service;

import io.vavr.control.Either;
import org.example.chatssecretos.dao.DaoPrivateGroup;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.errors.ErrorAppGroup;
import org.example.chatssecretos.domain.modelo.PrivateGroup;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PrivateGroupService {
    private final DaoPrivateGroup daoPrivateGroup;

    public PrivateGroupService(DaoPrivateGroup daoPrivateGroup){
        this.daoPrivateGroup = daoPrivateGroup;
    }

    @Async
    public CompletableFuture<Either<ErrorApp, Void>> addNew(PrivateGroup group) {
        return CompletableFuture.completedFuture(daoPrivateGroup.addNew(group));
    }

    public Either<ErrorApp,List<PrivateGroup>> getPrivateGroupsByUsername(String username) {
        return daoPrivateGroup.getPrivateGroups().map(groups ->
            groups.isEmpty() ? Collections.emptyList() : groups.stream()
                    .filter(g -> g.getMiembros().stream().anyMatch(u -> u.getName().equalsIgnoreCase(username)))
                    .toList());
    }

    public Either<ErrorApp, PrivateGroup> getGroupByName(String groupName) {
        return daoPrivateGroup.getPrivateGroups().flatMap(groups ->
                groups.stream()
                        .filter(g -> g.getNombre().equals(groupName))
                        .findFirst()
                        .<Either<ErrorApp, PrivateGroup>>map(Either::right)
                        .orElseGet(() -> Either.left(ErrorAppGroup.NO_MATCHING_GROUP))
        );
    }
}
