package org.example.chatssecretos.domain.service;

import io.vavr.control.Either;
import org.example.chatssecretos.dao.impl.DaoGroupImpl;
import org.example.chatssecretos.dao.impl.DaoUserImpl;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.errors.ErrorAppUser;
import org.example.chatssecretos.domain.modelo.Group;
import org.example.chatssecretos.domain.modelo.User;
import org.example.chatssecretos.utils.security.Asimetrico;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {

    private final DaoUserImpl daoUser;
    private final DaoGroupImpl daoGroupImpl;
    private final GroupService groupService;
    private final Asimetrico asimetrico;

    public UserService(DaoUserImpl daoUser, DaoGroupImpl daoGroupImpl,
                       GroupService groupService, Asimetrico asimetrico) {
        this.daoUser = daoUser;
        this.daoGroupImpl = daoGroupImpl;
        this.groupService = groupService;
        this.asimetrico = asimetrico;
    }

    private Either<ErrorApp, Void> checkNewPassword(String signUpPwd, String pwdRepeat) {
        return signUpPwd.equals(pwdRepeat)
                ? Either.right(null)
                : Either.left(ErrorAppUser.PASSWORDS_NOT_MATCH);
    }

    private Either<ErrorApp, Void> checkNewUsername(String signUpUsername) {
        return daoUser.getUsers().flatMap(u ->
                u.stream().anyMatch(user -> user.getName().equalsIgnoreCase(signUpUsername))
                        ? Either.left(ErrorAppUser.USERNAME_NOT_AVAILABLE)
                        : Either.right(null));
    }

    private Either<ErrorApp, Void> createUser(User user) {
        return checkNewUsername(user.getName())
                .flatMap(usernameCheck ->
                        asimetrico.generarYGuardarClavesUsuario(user.getName(), user.getPwd()))
                .flatMap(claveGenerada -> {
                    user.setPwd(null);
                    return daoUser.addUser(user);
                });
    }

    @Async
    public CompletableFuture<Either<ErrorApp, Void>> logIn(User user) {
        return CompletableFuture.completedFuture(
                daoUser.getUserByName(user.getName())
                        .flatMap(u ->
                                asimetrico.dameClavePrivada(user.getName(), user.getPwd())
                                        .map(privateKey -> Either.<ErrorApp, Void>right(null))
                                        .getOrElse(Either.left(ErrorAppUser.NO_MATCHING_CREDENTIALS))
                        )

        );
    }

    public CompletableFuture<Either<ErrorApp, User>> getUserByName(String username) {
        return CompletableFuture.completedFuture(daoUser.getUsers().flatMap(users ->
                users.stream().filter(user ->
                                user.getName().equalsIgnoreCase(username))
                        .findFirst()
                        .map(Either::<ErrorApp, User>right)
                        .orElseGet(() -> Either.left(ErrorAppUser.USUARIO_NO_EXISTE))
        ));
    }

    public Either<ErrorApp, User> addFriend(User initial, String userName) {
        return
                daoUser.getUserByName(userName).flatMap(f -> {
                    if (initial.getFriends().stream().anyMatch(lista -> lista.equals(userName)))
                        return Either.left(ErrorAppUser.YA_ES_AMIGO);
                    initial.getFriends().add(f.getName());
                    f.getFriends().add(initial.getName());
                    return daoUser.updateUser(initial).flatMap(updatedInitial ->
                            daoUser.updateUser(f).flatMap(updatedF -> {
                                daoGroupImpl.addGroup(new Group(initial.getName() + ":" + f.getName(),
                                        List.of(initial, f), null, null, true,
                                        LocalDateTime.now()));
                                return daoUser.getUserByName(userName);
                            }));
                });
    }

    @Async
    public CompletableFuture<Either<ErrorApp, Void>> checkCrear(String pwdRepeat, User user) {
        return CompletableFuture.completedFuture(
                checkNewPassword(pwdRepeat, user.getPwd())
                        .flatMap(v -> createUser(user)));
    }

    @Async
    public CompletableFuture<Either<ErrorApp, Void>> notEmpty(String pwdFieldRepeat,
                                                              String signUpUsername, String signUpPwd, String email) {
        return CompletableFuture.completedFuture(
                (pwdFieldRepeat.isEmpty() || signUpUsername.isEmpty() ||
                        signUpPwd.isEmpty() || email.isEmpty())
                        ? Either.left(ErrorAppUser.CAMPOS_INCOMPLETOS)
                        : Either.right(null));
    }

    @Async
    public CompletableFuture<Either<ErrorApp, Void>> entrarClickedUser(String username,
                                                                       String groupLink, String logInPwd) {
        return CompletableFuture.completedFuture(daoUser.getUserByName(username))
                .thenCompose(userEither ->
                        userEither.fold(
                                error -> CompletableFuture.completedFuture(Either.left(error)),
                                user -> groupService.logIn(groupLink, logInPwd, user)));
    }
}
