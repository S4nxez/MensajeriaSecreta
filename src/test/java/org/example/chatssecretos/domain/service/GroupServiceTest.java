package org.example.chatssecretos.domain.service;

import io.vavr.control.Either;
import org.example.chatssecretos.dao.DaoUser;
import org.example.chatssecretos.dao.impl.DaoGroupImpl;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.errors.ErrorAppGroup;
import org.example.chatssecretos.domain.errors.ErrorAppUser;
import org.example.chatssecretos.domain.modelo.Group;
import org.example.chatssecretos.domain.modelo.PrivateGroup;
import org.example.chatssecretos.domain.modelo.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private DaoGroupImpl daoGroup;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DaoUser daoUser;

    @Mock
    private PrivateGroupService privateGroupService;

    @InjectMocks
    private GroupService groupService;

    private Group group;

    @BeforeEach
    void setup(){
        group = new Group("group1", List.of(), "password", null, false, LocalDateTime.now());
    }
    @Nested
    class AddGroupTests {
        @Test
        void addGroup_WhenSuccessful() {
            // Given
            when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
            when(daoGroup.addGroup(group)).thenReturn(Either.right(null));

            // When
            Either<ErrorApp, Void> result = groupService.addGroup(group);

            // Then
            assertTrue(result.isRight());
            assertEquals("encodedPassword", group.getPassword());
        }

        @Test
        void addGroup_WhenGroupNameExists_ReturnsError() {
            // Given
            when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
            when(daoGroup.addGroup(group)).thenReturn(Either.left(ErrorAppGroup.GROUP_NAME_NOT_AVAILABLE));

            // When
            Either<ErrorApp, Void> result = groupService.addGroup(group);

            // Then
            assertTrue(result.isLeft());
            assertEquals(ErrorAppGroup.GROUP_NAME_NOT_AVAILABLE, result.getLeft());
        }
    }

    @Nested
    class getCombinedGroups {
        @Test
        void getCombinedGroups_WhenUserExistsWithBothTypes_ReturnsCombinedGroups() {
            // Given
            String username = "testUser";
            User user = new User(username, "test@example.com", "password", List.of());
            Group group = new Group("group1", List.of(user), "encodedPassword", null, false, LocalDateTime.now());
            PrivateGroup privateGroup = new PrivateGroup("privateGroup1", List.of(user), "admin", null);

            when(daoUser.getUserByName(username)).thenReturn(Either.right(user));
            when(daoGroup.getGroupsByUser(user)).thenReturn(Either.right(List.of(group)));
            when(privateGroupService.getPrivateGroupsByUsername(username))
                    .thenReturn(Either.right(List.of(privateGroup)));

            // When
            CompletableFuture<Either<ErrorApp, List<Object>>> resultFuture =
                    groupService.getCombinedGroups(username);

            // Then
            resultFuture.thenAccept(result -> {
                assertTrue(result.isRight());
                List<Object> combinedGroups = result.get();
                assertEquals(2, combinedGroups.size());
                assertTrue(combinedGroups.contains(group));
                assertTrue(combinedGroups.contains(privateGroup));
            });
        }

        @Test
        void getCombinedGroups_WhenUserDoesNotExist_ReturnsError() {
            // Given
            String username = "nonexistentUser";
            when(daoUser.getUserByName(username)).thenReturn(Either.left(ErrorAppUser.USUARIO_NO_EXISTE));

            // When
            CompletableFuture<Either<ErrorApp, List<Object>>> resultFuture =
                    groupService.getCombinedGroups(username);

            // Then
            resultFuture.thenAccept(result -> {
                assertTrue(result.isLeft());
                assertEquals(ErrorAppUser.USUARIO_NO_EXISTE, result.getLeft());
            });
        }

        @Test
        void getCombinedGroups_WhenNoGroups_ReturnsEmptyList() {
            // Given
            String username = "testUser";
            User user = new User(username, "test@example.com", "password", List.of());

            when(daoUser.getUserByName(username)).thenReturn(Either.right(user));
            when(daoGroup.getGroupsByUser(user)).thenReturn(Either.right(List.of()));
            when(privateGroupService.getPrivateGroupsByUsername(username))
                    .thenReturn(Either.right(List.of()));

            // When
            CompletableFuture<Either<ErrorApp, List<Object>>> resultFuture =
                    groupService.getCombinedGroups(username);

            // Then
            resultFuture.thenAccept(result -> {
                assertTrue(result.isRight());
                List<Object> combinedGroups = result.get();
                assertTrue(combinedGroups.isEmpty());
            });
        }

        @Test
        void getCombinedGroups_success() {
            // Given
            String username = "user1";
            User user = new User("user1", "user1@example.com", "password", List.of());
            Group group = new Group("group1", List.of(user), "encodedPassword", null, false, LocalDateTime.now());
            PrivateGroup privateGroup = new PrivateGroup("privateGroup1", List.of(user), "admin", null);

            when(daoUser.getUserByName(username)).thenReturn(Either.right(user));
            when(daoGroup.getGroupsByUser(user)).thenReturn(Either.right(List.of(group)));
            when(privateGroupService.getPrivateGroupsByUsername(username)).thenReturn(Either.right(List.of(privateGroup)));

            // When
            CompletableFuture<Either<ErrorApp, List<Object>>> resultFuture = groupService.getCombinedGroups(username);

            // Then
            resultFuture.thenAccept(result -> {
                assertTrue(result.isRight());
                List<Object> combinedGroups = result.get();
                assertEquals(2, combinedGroups.size());
                assertTrue(combinedGroups.contains(group));
                assertTrue(combinedGroups.contains(privateGroup));
            });
        }
    }


    @Nested
    class LoginTests {
        @Test
        void login_WhenSuccessful() {
            // Given
            String groupName = "group1";
            String rawPassword = "password";
            String encodedPassword = "encodedPassword";
            User user = new User("user1", "user1@example.com", rawPassword, new ArrayList<>());
            group = new Group(groupName, new ArrayList<>(), encodedPassword, "admin", false, LocalDateTime.now());

            when(daoGroup.getGroups()).thenReturn(Either.right(List.of(group)));
            when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
            when(daoGroup.updateGroup(any())).thenReturn(Either.right(null));

            // When
            CompletableFuture<Either<ErrorApp, Void>> resultFuture = groupService.logIn(groupName, "password", user);

            // Then
            resultFuture.thenAccept(result -> {
                assertTrue(result.isRight());
                assertEquals(1, group.getMiembros().size());
                assertTrue(group.getMiembros().contains(user));
            });
        }

        @Test
        void login_WhenGroupNotFound_ReturnsError() {
            // Given
            String groupName = "nonexistentGroup";
            String password = "password";
            User user = new User("user1", "user1@example.com", "password", List.of());
            when(daoGroup.getGroups()).thenReturn(Either.right(List.of()));

            // When
            CompletableFuture<Either<ErrorApp, Void>> resultFuture = groupService.logIn(groupName, password, user);

            // Then
            resultFuture.thenAccept(result -> {
                assertTrue(result.isLeft());
                assertEquals(ErrorAppGroup.NO_MATCHING_GROUP, result.getLeft());
            });
        }
    }

    @Nested
    class CheckPasswordTests {
        @Test
        void checkPassword_WhenCorrect() {
            // Given
            String groupName = "group1";
            String password = "password";
            Group group = new Group(groupName, List.of(), "encodedPassword", null, false, LocalDateTime.now());

            when(daoGroup.getGroups()).thenReturn(Either.right(List.of(group)));
            when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);

            // When
            Either<ErrorApp, Void> result = groupService.checkPwd(groupName, password);

            // Then
            assertTrue(result.isRight());
        }

        @Test
        void checkPassword_WhenIncorrect_ReturnsError() {
            // Given
            String groupName = "group1";
            String password = "wrongPassword";
            Group group = new Group(groupName, List.of(), "encodedPassword", null, false, LocalDateTime.now());

            when(daoGroup.getGroups()).thenReturn(Either.right(List.of(group)));
            when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(false);

            // When
            Either<ErrorApp, Void> result = groupService.checkPwd(groupName, password);

            // Then
            assertTrue(result.isLeft());
            assertEquals(ErrorAppGroup.INCORRECT_PASSWORD, result.getLeft());
        }
    }

    @Nested
    class GetMembersTests {
        @Test
        void getMembers_WhenGroupExists() {
            // Given
            String groupName = "group1";
            User user = new User("user1", "user1@example.com", "password", List.of());
            Group group = new Group(groupName, List.of(user), "encodedPassword", null, false, LocalDateTime.now());

            when(daoGroup.getGroups()).thenReturn(Either.right(List.of(group)));

            // When
            CompletableFuture<Either<ErrorApp, List<User>>> resultFuture = groupService.getMiembrosByGroupName(groupName);

            // Then
            resultFuture.thenAccept(result -> {
                assertTrue(result.isRight());
                List<User> members = result.get();
                assertEquals(1, members.size());
                assertEquals(user, members.get(0));
            });
        }

        @Test
        void getMembers_WhenGroupDoesNotExist_ReturnsError() {
            // Given
            String groupName = "nonexistentGroup";
            when(daoGroup.getGroups()).thenReturn(Either.right(List.of()));

            // When
            CompletableFuture<Either<ErrorApp, List<User>>> resultFuture = groupService.getMiembrosByGroupName(groupName);

            // Then
            resultFuture.thenAccept(result -> {
                assertTrue(result.isLeft());
                assertEquals(ErrorAppGroup.NO_MATCHING_GROUP, result.getLeft());
            });
        }
    }
}
