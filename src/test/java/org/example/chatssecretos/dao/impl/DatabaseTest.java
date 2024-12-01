package org.example.chatssecretos.dao.impl;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.vavr.control.Either;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.modelo.Group;
import org.example.chatssecretos.domain.modelo.Message;
import org.example.chatssecretos.domain.modelo.PrivateMessage;
import org.example.chatssecretos.domain.modelo.User;
import org.example.chatssecretos.utils.config.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseTest {

    private Database database;
    private Gson gson;

    @Mock(lenient = true)
    private Configuration config;

    private String usersFilePath;
    private String groupsFilePath;
    private String messagesFilePath;
    private String privateMessagesFilePath;
    private String privateGroupsFilePath;

    @BeforeEach
    void setUp() throws IOException {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                        (localDateTime, type, jsonSerializationContext) -> new JsonPrimitive(localDateTime.toString()))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                        (jsonElement, type, jsonDeserializationContext) -> LocalDateTime.parse(jsonElement.getAsString()))
                .setPrettyPrinting()
                .create();

        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            properties.load(input);
        }

        String originalUsersPath = properties.getProperty("pathUsers");
        String originalGroupsPath = properties.getProperty("pathGroups");
        String originalMessagesPath = properties.getProperty("pathMessages");
        String originalPrivateMessagesPath = properties.getProperty("pathPrivateMessages");
        String originalPrivateGroupsPath = properties.getProperty("pathPrivateGroups");

        usersFilePath = originalUsersPath.replace(".json", "_test.json");
        groupsFilePath = originalGroupsPath.replace(".json", "_test.json");
        messagesFilePath = originalMessagesPath.replace(".json", "_test.json");
        privateMessagesFilePath = originalPrivateMessagesPath.replace(".json", "_test.json");
        privateGroupsFilePath = originalPrivateGroupsPath.replace(".json", "_test.json");


        deleteFileIfExists(usersFilePath);
        deleteFileIfExists(groupsFilePath);
        deleteFileIfExists(messagesFilePath);
        deleteFileIfExists(privateMessagesFilePath);
        deleteFileIfExists(privateGroupsFilePath);

        copyFile(originalUsersPath, usersFilePath);
        copyFile(originalGroupsPath, groupsFilePath);
        copyFile(originalMessagesPath, messagesFilePath);
        copyFile(originalPrivateMessagesPath, privateMessagesFilePath);
        copyFile(originalPrivateGroupsPath, privateGroupsFilePath);

        when(config.getPathUsers()).thenReturn(usersFilePath);
        when(config.getPathGroups()).thenReturn(groupsFilePath);
        when(config.getPathMessages()).thenReturn(messagesFilePath);
        when(config.getPathPrivateMessages()).thenReturn(privateMessagesFilePath);
        when(config.getPathPrivateGroups()).thenReturn(privateGroupsFilePath);
        database = new Database(gson, config);
    }

    @AfterEach
    void tearDown() throws IOException {
        database = null;
        System.gc();
        cleanupFiles();
    }
    private void cleanupFiles() throws IOException {
        Files.deleteIfExists(Paths.get(usersFilePath));
        Files.deleteIfExists(Paths.get(groupsFilePath));
        Files.deleteIfExists(Paths.get(messagesFilePath));
        Files.deleteIfExists(Paths.get(privateMessagesFilePath));
        Files.deleteIfExists(Paths.get(privateGroupsFilePath));
    }

    private void deleteFileIfExists(String filePath) throws IOException {
        Files.deleteIfExists(Paths.get(filePath));
    }

    private void copyFile(String sourcePath, String destPath) throws IOException {
        Files.copy(Paths.get(sourcePath), Paths.get(destPath), StandardCopyOption.REPLACE_EXISTING);
    }

    @Nested
    class UserTests {

        @Test
        void testLoadUsers() throws IOException {
            //Given
            List<User> expectedUsers = List.of(new User("testUser", "test@example.com", "password", List.of()));
            try (FileWriter writer = new FileWriter(usersFilePath)) {
                gson.toJson(expectedUsers, writer);
            }

            //When
            Either<ErrorApp, List<User>> result = database.loadUsers();

            //Then
            assertTrue(result.isRight());
            assertEquals(expectedUsers, result.get());
        }

        @Test
        void testSaveUsers() throws IOException {
            //Given
            List<User> usersToSave = List.of(new User("testUser", "test@example.com", "password", List.of()));

            //When
            Either<ErrorApp, Void> result = database.saveUsers(usersToSave);

            //Then
            assertTrue(result.isRight());
            try (FileReader reader = new FileReader(usersFilePath)) {
                Type userListType = new TypeToken<List<User>>() {}.getType();
                List<User> savedUsers = gson.fromJson(reader, userListType);
                assertEquals(usersToSave, savedUsers);
            }
        }

        @Test
        void testDeleteUser() throws IOException {
            //Given
            User user = new User("testUser", "test@example.com", "password", List.of());
            List<User> users = new ArrayList<>();
            users.add(user);
            try (FileWriter writer = new FileWriter(usersFilePath)) {
                gson.toJson(users, writer);
            }

            //When
            Either<ErrorApp, Void> result = database.deleteUser(user);

            //Then
            assertTrue(result.isRight());
            Either<ErrorApp, List<User>> loadResult = database.loadUsers();
            assertTrue(loadResult.isRight());
            assertFalse(loadResult.get().contains(user));
        }
    }

    @Nested
    class GroupTests {

        @Test
        void testLoadGroups() throws IOException {
            //Given
            List<Group> expectedGroups = List.of(new Group("testGroup", List.of(),
                    "password", "admin", false, LocalDateTime.now()));
            try (FileWriter writer = new FileWriter(groupsFilePath)) {
                gson.toJson(expectedGroups, writer);
            }

            //When
            Either<ErrorApp, List<Group>> result = database.loadGroups();

            //Then
            assertTrue(result.isRight());
            assertEquals(expectedGroups, result.get());
        }

        @Test
        void testSaveGroups() throws IOException {
            //Given
            List<Group> groupsToSave = List.of(new Group("testGroup", List.of(),
                    "password", "admin", false, LocalDateTime.now()));

            //When
            Either<ErrorApp, Void> result = database.saveGroups(groupsToSave);

            //Then
            assertTrue(result.isRight());
            try (FileReader reader = new FileReader(groupsFilePath)) {
                Type groupListType = new TypeToken<List<Group>>() {}.getType();
                List<Group> savedGroups = gson.fromJson(reader, groupListType);
                assertEquals(groupsToSave, savedGroups);
            }
        }

        @Test
        void testDeleteGroup() throws IOException {
            //Given
            Group group = new Group("testGroup", List.of(), "password",
                    "admin", false, LocalDateTime.now());
            List<Group> groups = new ArrayList<>();
            groups.add(group);
            try (FileWriter writer = new FileWriter(groupsFilePath)) {
                gson.toJson(groups, writer);
            }

            //When
            Either<ErrorApp, Void> result = database.deleteGroup(group);

            //Then
            assertTrue(result.isRight());
            Either<ErrorApp, List<Group>> loadResult = database.loadGroups();
            assertTrue(loadResult.isRight());
            assertFalse(loadResult.get().contains(group));
        }
    }

    @Nested
    class MessageTests {

        @Test
        void testLoadMessages() throws IOException {
            //Given
            List<Message> expectedMessages = List.of(new Message("testMessage",
                    LocalDateTime.now(), "testUser", "testGroup"));
            try (FileWriter writer = new FileWriter(messagesFilePath)) {
                gson.toJson(expectedMessages, writer);
            }

            //When
            Either<ErrorApp, List<Message>> result = database.loadMessage();

            //Then
            assertTrue(result.isRight());
            assertEquals(expectedMessages, result.get());
        }

        @Test
        void testSaveMessages() throws IOException {
            //Given
            List<Message> messagesToSave = List.of(new Message("testMessage",
                    LocalDateTime.now(), "testUser", "testGroup"));

            //When
            Either<ErrorApp, Void> result = database.saveMessages(messagesToSave);

            //Then
            assertTrue(result.isRight());
            try (FileReader reader = new FileReader(messagesFilePath)) {
                Type messageListType = new TypeToken<List<Message>>() {}.getType();
                List<Message> savedMessages = gson.fromJson(reader, messageListType);
                assertEquals(messagesToSave, savedMessages);
            }
        }
    }

    @Nested
    class PrivateMessageTests {

        @Test
        void testLoadPrivateMessages() throws IOException {
            //Given
            List<PrivateMessage> expectedMessages = List.of(new PrivateMessage("testUser",
                    LocalDateTime.now(), "sign", "encryptedMessage", new HashMap<>(), "groupName"));
            try (FileWriter writer = new FileWriter(privateMessagesFilePath)) {
                gson.toJson(expectedMessages, writer);
            }

            //When
            Either<ErrorApp, List<PrivateMessage>> result = database.loadPrivateMessage();

            //Then
            assertTrue(result.isRight());
            assertEquals(expectedMessages, result.get());
        }

        @Test
        void testSavePrivateMessages() throws IOException {
            //Given
            List<PrivateMessage> messagesToSave = List.of(new PrivateMessage("testUser",
                    LocalDateTime.now(), "sign", "encryptedMessage",
                    new HashMap<>(), "groupName"));

            //When
            Either<ErrorApp, Void> result = database.savePrivateMessages(messagesToSave);

            //Then
            assertTrue(result.isRight());
            try (FileReader reader = new FileReader(privateMessagesFilePath)) {
                Type messageListType = new TypeToken<List<PrivateMessage>>() {}.getType();
                List<PrivateMessage> savedMessages = gson.fromJson(reader, messageListType);
                assertEquals(messagesToSave, savedMessages);
            }
        }
    }
}