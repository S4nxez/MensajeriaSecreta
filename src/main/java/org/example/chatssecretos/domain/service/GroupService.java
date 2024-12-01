package org.example.chatssecretos.domain.service;

import io.vavr.control.Either;
import org.example.chatssecretos.dao.DaoUser;
import org.example.chatssecretos.dao.impl.DaoGroupImpl;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.errors.ErrorAppGroup;
import org.example.chatssecretos.domain.modelo.Group;
import org.example.chatssecretos.domain.modelo.User;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class GroupService {
    private final DaoGroupImpl daoGroup;
    private final PasswordEncoder passwordEncoder;
    private final DaoUser daoUser;
    private final PrivateGroupService privateGroupService;

    public GroupService(DaoGroupImpl daoGroup, PasswordEncoder passwordEncoder, DaoUser daoUser, PrivateGroupService privateGroupService) {
        this.daoGroup = daoGroup;
        this.passwordEncoder = passwordEncoder;
        this.daoUser = daoUser;
        this.privateGroupService = privateGroupService;
    }

    public Either<ErrorApp,Void> addGroup(Group group) {
        group.setPassword(passwordEncoder.encode(group.getPassword()));
        return daoGroup.addGroup(group);
    }

    private Either<ErrorApp, List<Group>> getGroupsByUsername(String username) {
        return daoUser.getUserByName(username).flatMap(daoGroup::getGroupsByUser);
    }

    @Async
    public CompletableFuture<Either<ErrorApp, List<Object>>> getCombinedGroups(String username) {
        List<Object> ret = new ArrayList<>();
        return CompletableFuture.completedFuture(
                getGroupsByUsername(username)
                        .peek(ret::addAll)
                        .flatMap(v -> privateGroupService.getPrivateGroupsByUsername(username)
                                .peek(ret::addAll)
                                .map(v2 -> ret)
                        ));
    }

    @Async
    public CompletableFuture<Either<ErrorApp, Void>> logIn(String groupName, String logInPwdText, User usr) {
        return CompletableFuture.completedFuture(daoGroup.getGroups()
                .flatMap(groups ->
                        groups.stream()
                                .filter(g -> g.getNombre().equalsIgnoreCase(groupName))
                                .findFirst()
                                .filter(group -> !group.isPrivateChat() &&
                                        passwordEncoder.matches(logInPwdText, group.getPassword()) &&
                                        group.getMiembros().stream().noneMatch(u ->
                                                u.getName().equals(usr.getName())))
                                .map(group -> {
                                    group.getMiembros().add(usr);
                                    return daoGroup.updateGroup(group);
                                })
                                .orElseGet(() -> Either.left(ErrorAppGroup.NO_MATCHING_GROUP))
                ));
    }

    public Either<ErrorApp, Void> addPrivateChat(User user1, User user2) {
        return addGroup(new Group(user1.getName()+":"+user2.getName(), List.of(user1,user2),
                "adsdsasdasd",null, true, LocalDateTime.now()));
    }

    public Either<ErrorApp,Group> getGroupByName(String groupName) {
        return daoGroup.getGroups().flatMap(groups -> groups.stream()
                .filter(g -> g.getNombre().equals(groupName))
                .findFirst().map(Either::<ErrorApp,Group>right)
                .orElse(Either.left(ErrorAppGroup.NO_MATCHING_GROUP)));
    }

    @Async
    public CompletableFuture<Either<ErrorApp, Group>> getPrivateChats(User user1, User user2) {
        return CompletableFuture.completedFuture(
                daoGroup.getGroups().flatMap(groups -> groups.stream()
                .filter(g -> g.getMiembros().contains(user1) && g.getMiembros().contains(user2) && g.isPrivateChat())
                .findFirst().map(Either::<ErrorApp, Group>right).orElse(null))
        );
    }

    public Either<ErrorApp, Void> checkPwd(String groupName, String pwd) {
        return getGroupByName(groupName).flatMap(group ->
            passwordEncoder.matches(pwd, group.getPassword())
                    ? Either.right(null)
                    : Either.left(ErrorAppGroup.INCORRECT_PASSWORD)
        );
    }

    public CompletableFuture<Either<ErrorApp, List<User>>> getMiembrosByGroupName(String nombre) {
        return CompletableFuture.completedFuture(getGroupByName(nombre).map(Group::getMiembros));
    }
}
