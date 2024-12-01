package org.example.chatssecretos.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.vavr.control.Either;
import org.example.chatssecretos.dao.impl.DaoGroupImpl;
import org.example.chatssecretos.dao.impl.DaoUserImpl;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.errors.ErrorAppUser;
import org.example.chatssecretos.domain.modelo.Group;
import org.example.chatssecretos.domain.modelo.User;
import org.example.chatssecretos.utils.security.Asimetrico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.PrivateKey;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String TEST_USER = "testUser";
    private static final String MAIL = "test@example.com";
    private static final String PASSWORD = "password";

    @InjectMocks
    private UserService userService;

    @Mock
    private DaoUserImpl daoUser;

    @Mock
    private DaoGroupImpl daoGroupImpl;

    @Mock
    private GroupService groupService;

    @Mock
    private Asimetrico asimetrico;

    private User testUser;
    private PrivateKey mockPrivateKey;

    @BeforeEach
    void setUp() {
        testUser = new User(TEST_USER, MAIL, PASSWORD, new ArrayList<>());
        mockPrivateKey = mock(PrivateKey.class);
    }

    @Nested
    class LoginTests {
        @Test
        void login_WhenCredentialsAreValid_ReturnsSuccess() {
            // Given
            when(daoUser.getUserByName(testUser.getName())).thenReturn(Either.right(testUser));
            when(asimetrico.dameClavePrivada(testUser.getName(), testUser.getPwd()))
                    .thenReturn(Either.right(mockPrivateKey));

            // When
            CompletableFuture<Either<ErrorApp, Void>> result = userService.logIn(testUser);

            // Then
            assertThat(result.join().isRight()).isTrue();
        }

        @Test
        void login_WhenUserDoesNotExist_ReturnsError() {
            // Given
            when(daoUser.getUserByName(testUser.getName()))
                    .thenReturn(Either.left(ErrorAppUser.USUARIO_NO_EXISTE));

            // When
            CompletableFuture<Either<ErrorApp, Void>> result = userService.logIn(testUser);

            // Then
            assertThat(result.join().isLeft()).isTrue();
            assertThat(result.join().getLeft()).isEqualTo(ErrorAppUser.USUARIO_NO_EXISTE);
        }

        @Test
        void login_WhenCredentialsAreInvalid_ReturnsError() {
            // Given
            when(daoUser.getUserByName(testUser.getName())).thenReturn(Either.right(testUser));
            when(asimetrico.dameClavePrivada(testUser.getName(), testUser.getPwd()))
                    .thenReturn(Either.left(ErrorAppUser.NO_MATCHING_CREDENTIALS));

            // When
            CompletableFuture<Either<ErrorApp, Void>> result = userService.logIn(testUser);

            // Then
            assertThat(result.join().isLeft()).isTrue();
            assertThat(result.join().getLeft()).isEqualTo(ErrorAppUser.NO_MATCHING_CREDENTIALS);
        }
    }

    @Nested
    class UserCreationTests {
        @Test
        void createUser_WhenAllFieldsAreValid_ReturnsSuccess() {
            // Given
            String pwdRepeat = PASSWORD;
            when(daoUser.getUsers()).thenReturn(Either.right(new ArrayList<>()));
            when(asimetrico.generarYGuardarClavesUsuario(testUser.getName(), testUser.getPwd()))
                    .thenReturn(Either.right(null));
            when(daoUser.addUser(testUser)).thenReturn(Either.right(null));

            // When
            CompletableFuture<Either<ErrorApp, Void>> result = userService.checkCrear(pwdRepeat, testUser);

            // Then
            assertThat(result.join().isRight()).isTrue();
        }

        @Test
        void createUser_WhenPasswordsDontMatch_ReturnsError() {
            // Given
            String pwdRepeat = "differentPassword";

            // When
            CompletableFuture<Either<ErrorApp, Void>> result = userService.checkCrear(pwdRepeat, testUser);

            // Then
            assertThat(result.join().isLeft()).isTrue();
            assertThat(result.join().getLeft()).isEqualTo(ErrorAppUser.PASSWORDS_NOT_MATCH);
        }

        @Test
        void createUser_WhenUsernameExists_ReturnsError() {
            // Given
            String pwdRepeat = PASSWORD;
            when(daoUser.getUsers()).thenReturn(Either.right(List.of(testUser)));

            // When
            CompletableFuture<Either<ErrorApp, Void>> result = userService.checkCrear(pwdRepeat, testUser);

            // Then
            assertThat(result.join().isLeft()).isTrue();
            assertThat(result.join().getLeft()).isEqualTo(ErrorAppUser.USERNAME_NOT_AVAILABLE);
        }
    }

    @Nested
    class AddFriendTests {
        User friend;
        @BeforeEach
        void setup(){
           friend = new User("friend", "friend@example.com", PASSWORD, new ArrayList<>());
        }
        @Test
        void addFriend_WhenUserExists_ReturnsSuccess() {
            // Given

            when(daoUser.getUserByName(friend.getName())).thenReturn(Either.right(friend));
            when(daoUser.updateUser(any(User.class))).thenReturn(Either.right(null));
            when(daoGroupImpl.addGroup(any(Group.class))).thenReturn(Either.right(null));

            // When
            Either<ErrorApp, User> result = userService.addFriend(testUser, friend.getName());

            // Then
            assertThat(result.isRight()).isTrue();
            assertThat(testUser.getFriends()).contains(friend.getName());
            assertThat(friend.getFriends()).contains(testUser.getName());
        }

        @Test
        void addFriend_WhenAlreadyFriends_ReturnsError() {
            // Given
            testUser.getFriends().add("friend");
            when(daoUser.getUserByName(friend.getName())).thenReturn(Either.right(friend));

            // When
            Either<ErrorApp, User> result = userService.addFriend(testUser, friend.getName());

            // Then
            assertThat(result.isLeft()).isTrue();
            assertThat(result.getLeft()).isEqualTo(ErrorAppUser.YA_ES_AMIGO);
        }
    }

    @Nested
    class ValidationTests {
        @Test
        void notEmpty_WhenAllFieldsAreFilled_ReturnsSuccess() {
            // Given

            // When
            CompletableFuture<Either<ErrorApp, Void>> result =
                    userService.notEmpty(PASSWORD, TEST_USER, PASSWORD, MAIL);

            // Then
            assertThat(result.join().isRight()).isTrue();
        }

        @Test
        void notEmpty_WhenAnyFieldIsEmpty_ReturnsError() {
            // Given
            String pwdRepeat = "";

            // When
            CompletableFuture<Either<ErrorApp, Void>> result =
                    userService.notEmpty(pwdRepeat, TEST_USER, PASSWORD, MAIL);

            // Then
            assertThat(result.join().isLeft()).isTrue();
            assertThat(result.join().getLeft()).isEqualTo(ErrorAppUser.CAMPOS_INCOMPLETOS);
        }
    }

    @Nested
    class GroupEntryTests {
        @Test
        void entrarClickedUser_WhenUserAndGroupExist_ReturnsSuccess() {
            // Given
            String groupLink = "groupLink";
            String password = PASSWORD;
            when(daoUser.getUserByName(testUser.getName())).thenReturn(Either.right(testUser));
            when(groupService.logIn(groupLink, password, testUser))
                    .thenReturn(CompletableFuture.completedFuture(Either.right(null)));

            // When
            CompletableFuture<Either<ErrorApp, Void>> result =
                    userService.entrarClickedUser(testUser.getName(), groupLink, password);

            // Then
            assertThat(result.join().isRight()).isTrue();
        }

        @Test
        void entrarClickedUser_WhenUserDoesNotExist_ReturnsError() {
            // Given
            String groupLink = "groupLink";
            String password = PASSWORD;
            when(daoUser.getUserByName(testUser.getName()))
                    .thenReturn(Either.left(ErrorAppUser.USUARIO_NO_EXISTE));

            // When
            CompletableFuture<Either<ErrorApp, Void>> result =
                    userService.entrarClickedUser(testUser.getName(), groupLink, password);

            // Then
            assertThat(result.join().isLeft()).isTrue();
            assertThat(result.join().getLeft()).isEqualTo(ErrorAppUser.USUARIO_NO_EXISTE);
        }
    }
}
