package org.example.chatssecretos.dao.impl;

import io.vavr.control.Either;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.errors.ErrorAppDataBase;
import org.example.chatssecretos.domain.errors.ErrorAppUser;
import org.example.chatssecretos.domain.modelo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DaoUserImplTest {

    private static final String TEST_USER = "test";
    private static final String TEST_EMAIL = "test@test.com";
    private static final String TEST_PASSWORD = "pwd";
    private User testUser;

    @Mock
    private Database db;

    @InjectMocks
    private DaoUserImpl daoUser;

    @BeforeEach
    void setUp() {
        testUser = new User(TEST_USER, TEST_EMAIL, TEST_PASSWORD, new ArrayList<>());
    }
    @Nested
    class AddUserTests {
        @Test
        public void test_add_user_to_empty_list_success() {
            // Given
            List<User> emptyList = new ArrayList<>();
            when(db.loadUsers()).thenReturn(Either.right(emptyList));
            when(db.saveUsers(any())).thenReturn(Either.right(null));

            // When
            Either<ErrorApp, Void> result = daoUser.addUser(testUser);

            // Then
            assertTrue(result.isRight());
            verify(db).loadUsers();
            verify(db).saveUsers(argThat(list -> list.size() == 1 && list.contains(testUser)));
        }
    }

    @Nested
    class GetUsersTests {

        @Test
        public void test_get_users_returns_list_when_successful() {
            // given
            List<User> expectedUsers = List.of(testUser);
            when(db.loadUsers()).thenReturn(Either.right(expectedUsers));

            // when
            Either<ErrorApp, List<User>> result = daoUser.getUsers();

            // then
            assertTrue(result.isRight());
            assertEquals(expectedUsers, result.get());
        }

        @Test
        public void test_get_users_returns_error_when_file_not_found() {
            // given
            when(db.loadUsers()).thenReturn(Either.left(ErrorAppDataBase.ERROR_LEER_FICHEROS));

            // when
            Either<ErrorApp, List<User>> result = daoUser.getUsers();

            // then
            assertTrue(result.isLeft());
            assertEquals(ErrorAppDataBase.ERROR_LEER_FICHEROS, result.getLeft());
        }
    }

    @Nested
    class GetBynameTests {

        @Test
        public void test_get_existing_user_by_name_returns_right() {
            // Given
            List<User> users = List.of(testUser);
            when(db.loadUsers()).thenReturn(Either.right(users));

            // When
            Either<ErrorApp, User> result = daoUser.getUserByName(TEST_USER);

            // Then
            assertTrue(result.isRight());
            assertEquals(testUser, result.get());
        }

        @Test
        public void test_get_nonexistent_user_returns_error() {
            // Given
            List<User> users = new ArrayList<>();
            when(db.loadUsers()).thenReturn(Either.right(users));

            // when
            Either<ErrorApp, User> result = daoUser.getUserByName("nonexistentUser");

            // then
            assertTrue(result.isLeft());
            assertEquals(ErrorAppUser.USUARIO_NO_EXISTE, result.getLeft());
        }
    }

    @Nested
    class UpdateTests {

        @Test
        public void test_update_existing_user_success() {
            // given
            List<User> users = new ArrayList<>();
            User existingUser = new User("john", "john@test.com", "pwd123", new ArrayList<>());
            users.add(existingUser);
            User updatedUser = new User("john", "john.new@test.com", "pwd", List.of(testUser.getName()));

            when(db.loadUsers()).thenReturn(Either.right(users));
            when(db.deleteUser(existingUser)).thenReturn(Either.right(null));
            when(db.saveUsers(any())).thenReturn(Either.right(null));

            // when
            Either<ErrorApp, Void> result = daoUser.updateUser(updatedUser);

            // then
            assertTrue(result.isRight());
            verify(db).deleteUser(existingUser);
            verify(db).saveUsers(any());
        }

        @Test
        public void test_update_nonexistent_user_fails() {
            // given
            List<User> users = new ArrayList<>();
            when(db.loadUsers()).thenReturn(Either.right(users));

            // when
            Either<ErrorApp, Void> result = daoUser.updateUser(testUser);

            // then
            assertTrue(result.isLeft());
            assertEquals(ErrorAppDataBase.ERROR_DATABASE, result.getLeft());
            verify(db, never()).deleteUser(any());
            verify(db, never()).saveUsers(any());
        }
    }
}