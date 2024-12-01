package org.example.chatssecretos.dao.impl;

import io.vavr.control.Either;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.modelo.Group;
import org.example.chatssecretos.domain.modelo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DaoGroupImplTest {

    @Mock
    private Database database;

    @InjectMocks
    private DaoGroupImpl daoGroup;

    @Nested
    class AddGroupTests {
        private Group group;
        private List<Group> groups;

        @BeforeEach
        void setUp() {
            group = new Group("testGroup", new ArrayList<>(), "password", "admin", false, LocalDateTime.now());
            groups = new ArrayList<>();
            groups.add(group);
        }

        @Test
        void addNewGroup() {
            //Given
            when(database.loadGroups()).thenReturn(Either.right(new ArrayList<>()));
            when(database.saveGroups(any())).thenReturn(Either.right(null));

            //When
            Either<ErrorApp, Void> result = daoGroup.addGroup(group);

            //Then
            assertTrue(result.isRight());
        }

        @Test
        void addExistingGroup() {
            //Given
            when(database.loadGroups()).thenReturn(Either.right(groups));

            //When
            Either<ErrorApp, Void> result = daoGroup.addGroup(group);

            //Then
            assertTrue(result.isLeft());
        }
    }

    @Nested
    class GetGroupsTests {
        private List<Group> groups;

        @BeforeEach
        void setUp() {
            groups = List.of(
                    new Group("group1", new ArrayList<>(), "pass1", "admin1", false, LocalDateTime.now()),
                    new Group("group2", new ArrayList<>(), "pass2", "admin2", false, LocalDateTime.now())
            );
        }

        @Test
        void getExistingGroups() {
            //Given
            when(database.loadGroups()).thenReturn(Either.right(groups));

            //When
            Either<ErrorApp, List<Group>> result = daoGroup.getGroups();

            //Then
            assertTrue(result.isRight());
            assertEquals(groups, result.get());
        }

        @Test
        void getEmptyGroups() {
            //Given
            when(database.loadGroups()).thenReturn(Either.right(new ArrayList<>()));

            //When
            Either<ErrorApp, List<Group>> result = daoGroup.getGroups();

            //Then
            assertTrue(result.isRight());
            assertTrue(result.get().isEmpty());
        }
    }

    @Nested
    class UpdateGroupTests {
        private Group originalGroup;
        private Group updatedGroup;
        private List<Group> groups;

        @BeforeEach
        void setUp() {
            originalGroup = new Group("testGroup", new ArrayList<>(), "password", "admin", false, LocalDateTime.now());
            updatedGroup = new Group("testGroup", List.of(new User("user1", "gmail", "pwd", null)), "password", "admin", false, LocalDateTime.now());
            groups = new ArrayList<>();
            groups.add(originalGroup);
        }

        @Test
        void updateExistingGroup() {
            // Given
            when(database.loadGroups()).thenAnswer(invocation -> Either.right(groups));
            when(database.deleteGroup(any(Group.class))).thenAnswer(groups -> {
                Group groupToDelete = groups.getArgument(0);
                this.groups.remove(groupToDelete);
                return Either.right(null);
            });
            when(database.saveGroups(any())).thenReturn(Either.right(null));

            // When
            Either<ErrorApp, Void> result = daoGroup.updateGroup(updatedGroup);

            // Then
            assertTrue(result.isRight());
        }

        @Test
        void updateNonExistentGroup() {
            //Given
            Group nonExistentGroup = new Group("nonExistent", new ArrayList<>(), "password", "admin", false, LocalDateTime.now());
            when(database.loadGroups()).thenReturn(Either.right(groups));

            //When
            Either<ErrorApp, Void> result = daoGroup.updateGroup(nonExistentGroup);

            //Then
            assertTrue(result.isLeft());
        }
    }

    @Nested
    class GetGroupsByUserTests {
        private User user;
        private List<Group> groups;

        @BeforeEach
        void setUp() {
            user = new User("testUser", "test@test.com", "password", List.of("group1"));
            groups = List.of(
                    new Group("group1", List.of(user), "pass1", "admin1", false, LocalDateTime.now()),
                    new Group("group2", new ArrayList<>(), "pass2", "admin2", false, LocalDateTime.now())
            );
        }

        @Test
        void getUserGroups() {
            //Given
            when(database.loadGroups()).thenReturn(Either.right(groups));

            //When
            Either<ErrorApp, List<Group>> result = daoGroup.getGroupsByUser(user);

            //Then
            assertTrue(result.isRight());
            assertEquals(1, result.get().size());
            assertEquals("group1", result.get().get(0).getNombre());
        }

        @Test
        void getUserWithNoGroups() {
            //Given
            User userNoGroups = new User("noGroups", "test@test.com", "password", new ArrayList<>());
            when(database.loadGroups()).thenReturn(Either.right(groups));

            //When
            Either<ErrorApp, List<Group>> result = daoGroup.getGroupsByUser(userNoGroups);

            //Then
            assertTrue(result.isRight());
            assertTrue(result.get().isEmpty());
        }
    }
}