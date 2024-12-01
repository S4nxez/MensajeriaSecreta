package org.example.chatssecretos.dao.impl;

import io.vavr.control.Either;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.errors.ErrorAppGroup;
import org.example.chatssecretos.domain.modelo.PrivateGroup;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DaoPrivateGroupImplTest {

    @Mock
    private Database database;

    @InjectMocks
    private DaoPrivateGroupImpl daoPrivateGroup;

    @Nested
    class AddNewTests {
        private PrivateGroup privateGroup;
        private List<PrivateGroup> privateGroups;

        @BeforeEach
        void setUp() {
            privateGroup = new PrivateGroup("testGroup", null,"admin", LocalDateTime.now());
            privateGroups = new ArrayList<>();
            privateGroups.add(privateGroup);
        }

        @Test
        void addNewPrivateGroup() {
            //Given
            when(database.loadPrivateGroups()).thenReturn(Either.right(new ArrayList<>()));
            when(database.savePrivateGroups(any())).thenReturn(Either.right(null));

            //When
            Either<ErrorApp, Void> result = daoPrivateGroup.addNew(privateGroup);

            //Then
            assertTrue(result.isRight());
            verify(database).savePrivateGroups(any());
        }

        @Test
        void addExistingPrivateGroup() {
            //Given
            when(database.loadPrivateGroups()).thenReturn(Either.right(privateGroups));

            //When
            Either<ErrorApp, Void> result = daoPrivateGroup.addNew(privateGroup);

            //Then
            assertTrue(result.isLeft());
            assertEquals(ErrorAppGroup.GROUP_NAME_NOT_AVAILABLE, result.getLeft());
            verify(database, never()).savePrivateGroups(any());
        }
    }

    @Nested
    class GetPrivateGroupsTests {
        private List<PrivateGroup> privateGroups;

        @BeforeEach
        void setUp() {
            privateGroups = List.of(
                    new PrivateGroup("group1",  null,"admin1", LocalDateTime.now()),
                    new PrivateGroup("group2", null,"admin2", LocalDateTime.now())
            );
        }

        @Test
        void getExistingPrivateGroups() {
            //Given
            when(database.loadPrivateGroups()).thenReturn(Either.right(privateGroups));

            //When
            Either<ErrorApp, List<PrivateGroup>> result = daoPrivateGroup.getPrivateGroups();

            //Then
            assertTrue(result.isRight());
            assertEquals(privateGroups, result.get());
        }

        @Test
        void getEmptyPrivateGroups() {
            //Given
            when(database.loadPrivateGroups()).thenReturn(Either.right(new ArrayList<>()));

            //When
            Either<ErrorApp, List<PrivateGroup>> result = daoPrivateGroup.getPrivateGroups();

            //Then
            assertTrue(result.isRight());
            assertTrue(result.get().isEmpty());
        }
    }
}