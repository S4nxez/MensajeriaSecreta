package org.example.chatssecretos.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.vavr.control.Either;
import org.example.chatssecretos.dao.impl.DaoPrivateGroupImpl;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.errors.ErrorAppGroup;
import org.example.chatssecretos.domain.modelo.PrivateGroup;
import org.example.chatssecretos.domain.modelo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org. junit. jupiter.api.Nested;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrivateGroupServiceTest {

    @InjectMocks
    private PrivateGroupService privateGroupService;

    @Mock
    private DaoPrivateGroupImpl daoPrivateGroup;

    private User testUser;
    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {
        testUser = new User("testUser", "test@example.com", "password", new ArrayList<>());
        testDate = LocalDateTime.now();
    }

    @Nested
    class AddNewGroupTests {
        @Test
        @DisplayName("Add new private group successfully")
        void addNew_WhenGroupIsValid_ReturnsSuccess() {
            // Given
            PrivateGroup group = new PrivateGroup("newGroup", new ArrayList<>(), "Admin", testDate);
            when(daoPrivateGroup.addNew(group)).thenReturn(Either.right(null));

            // When
            Either<ErrorApp, Void> result = privateGroupService.addNew(group).join();

            // Then
            assertThat(result.isRight()).isTrue();
        }

        @Test
        @DisplayName("Add new private group with existing name fails")
        void addNew_WhenGroupNameExists_ReturnsError() {
            // Given
            PrivateGroup group = new PrivateGroup("existingGroup", new ArrayList<>(), "Admin", testDate);
            when(daoPrivateGroup.addNew(group)).thenReturn(Either.left(ErrorAppGroup.GROUP_NAME_NOT_AVAILABLE));

            // When
            Either<ErrorApp, Void> result = privateGroupService.addNew(group).join();

            // Then
            assertThat(result.isLeft()).isTrue();
            assertThat(result.getLeft()).isEqualTo(ErrorAppGroup.GROUP_NAME_NOT_AVAILABLE);
        }
    }

    @Nested
    class GetPrivateGroupsTests {
        private PrivateGroup group1;
        private PrivateGroup group2;

        @BeforeEach
        void setUp() {
            group1 = new PrivateGroup("group1",
                    List.of(testUser),
                    "Admin",
                    testDate);
            group2 = new PrivateGroup("group2",
                    List.of(new User("user2", "user2@example.com", "password", new ArrayList<>())),
                    "Admin",
                    testDate);
        }

        @Test
        @DisplayName("Get private groups by username returns matching groups")
        void getPrivateGroupsByUsername_WhenGroupsExist_ReturnsMatchingGroups() {
            // Given
            when(daoPrivateGroup.getPrivateGroups()).thenReturn(Either.right(List.of(group1, group2)));

            // When
            Either<ErrorApp, List<PrivateGroup>> result =
                    privateGroupService.getPrivateGroupsByUsername(testUser.getName());

            // Then
            assertThat(result.isRight()).isTrue();
            assertThat(result.get()).hasSize(1);
            assertThat(result.get().get(0)).isEqualTo(group1);
        }

        @Test
        @DisplayName("Get private groups by username returns empty list when no matches")
        void getPrivateGroupsByUsername_WhenNoMatches_ReturnsEmptyList() {
            // Given
            when(daoPrivateGroup.getPrivateGroups()).thenReturn(Either.right(List.of(group2)));

            // When
            Either<ErrorApp, List<PrivateGroup>> result =
                    privateGroupService.getPrivateGroupsByUsername(testUser.getName());

            // Then
            assertThat(result.isRight()).isTrue();
            assertThat(result.get()).isEmpty();
        }

        @Test
        @DisplayName("Get private groups by username when database error")
        void getPrivateGroupsByUsername_WhenDatabaseError_ReturnsError() {
            // Given
            when(daoPrivateGroup.getPrivateGroups()).thenReturn(Either.left(ErrorAppGroup.GROUP_NOT_FOUND));

            // When
            Either<ErrorApp, List<PrivateGroup>> result =
                    privateGroupService.getPrivateGroupsByUsername(testUser.getName());

            // Then
            assertThat(result.isLeft()).isTrue();
            assertThat(result.getLeft()).isEqualTo(ErrorAppGroup.GROUP_NOT_FOUND);
        }
    }

    @Nested
    class GetGroupByNameTests {
        private PrivateGroup testGroup;

        @BeforeEach
        void setUp() {
            testGroup = new PrivateGroup("testGroup", new ArrayList<>(), "admin", testDate);
        }

        @Test
        @DisplayName("Get group by name when group exists")
        void getGroupByName_WhenGroupExists_ReturnsGroup() {
            // Given
            when(daoPrivateGroup.getPrivateGroups()).thenReturn(Either.right(List.of(testGroup)));

            // When
            Either<ErrorApp, PrivateGroup> result = privateGroupService.getGroupByName(testGroup.getNombre());

            // Then
            assertThat(result.isRight()).isTrue();
            assertThat(result.get()).isEqualTo(testGroup);
        }

        @Test
        @DisplayName("Get group by name when group doesn't exist")
        void getGroupByName_WhenGroupDoesNotExist_ReturnsError() {
            // Given
            String nonExistentGroupName = "nonExistentGroup";
            when(daoPrivateGroup.getPrivateGroups()).thenReturn(Either.right(List.of(testGroup)));

            // When
            Either<ErrorApp, PrivateGroup> result = privateGroupService.getGroupByName(nonExistentGroupName);

            // Then
            assertThat(result.isLeft()).isTrue();
            assertThat(result.getLeft()).isEqualTo(ErrorAppGroup.NO_MATCHING_GROUP);
        }

        @Test
        @DisplayName("Get group by name when database error")
        void getGroupByName_WhenDatabaseError_ReturnsError() {
            // Given
            when(daoPrivateGroup.getPrivateGroups()).thenReturn(Either.left(ErrorAppGroup.GROUP_NOT_FOUND));

            // When
            Either<ErrorApp, PrivateGroup> result = privateGroupService.getGroupByName(testGroup.getNombre());

            // Then
            assertThat(result.isLeft()).isTrue();
            assertThat(result.getLeft()).isEqualTo(ErrorAppGroup.GROUP_NOT_FOUND);
        }
    }
}
