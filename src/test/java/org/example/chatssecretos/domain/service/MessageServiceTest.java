package org.example.chatssecretos.domain.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import io.vavr.control.Either;
import org.example.chatssecretos.dao.impl.DaoMessageImpl;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.errors.ErrorAppGroup;
import org.example.chatssecretos.domain.modelo.*;
import org.example.chatssecretos.utils.security.Asimetrico;
import org.example.chatssecretos.utils.security.EncriptarSimetrico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org. junit. jupiter.api.Nested;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private DaoMessageImpl msgDao;

    @Mock
    private GroupService groupService;

    @Mock
    private EncriptarSimetrico encriptarSimetrico;

    @Mock
    private Asimetrico asimetrico;

    @Mock
    private PrivateGroupService privateGroupService;

    @InjectMocks
    private MessageService messageService;

    private User testUser;
    private Group testGroup;
    private Message testMessage;
    private LocalDateTime testTime;

    @BeforeEach
    void setUp() {
        testTime = LocalDateTime.now();
        testUser = new User("testUser", "test@example.com", "password", new ArrayList<>());
        testGroup = new Group("testGroup", new ArrayList<>(), "password", "admin", false, testTime);
        testMessage = new Message("testMessage", testTime, testUser.getName(), testGroup.getNombre());
    }

    @Nested
    class GetMessageTests {
        @Test
        void getMessagesByGroup_WhenMessagesExist_ReturnsFilteredMessages() {
            // Given
            Message message1 = new Message("text1", testTime, "user1", testGroup.getNombre());
            Message message2 = new Message("text2", testTime, "user2", "otherGroup");
            when(msgDao.getMessage()).thenReturn(Either.right(Arrays.asList(message1, message2)));

            // When
            Either<ErrorApp, List<Message>> result = messageService.getMessagesByGroup(testGroup);

            // Then
            assertTrue(result.isRight());
            assertEquals(1, result.get().size());
            assertEquals(testGroup.getNombre(), result.get().get(0).getGrupo());
        }

        @Test
        void getMessagesByGroupName_WhenMessagesExist_ReturnsFilteredMessages() {
            // Given
            PrivateMessage message = new PrivateMessage("sender", testTime, "sign",
                    "encrypted", new HashMap<>(), testGroup.getNombre());
            when(msgDao.getPrivateMessage()).thenReturn(Either.right(List.of(message)));

            // When
            Either<ErrorApp, List<PrivateMessage>> result = messageService.getMessagesByGroup(testGroup.getNombre());

            // Then
            assertTrue(result.isRight());
            assertEquals(1, result.get().size());
            assertEquals(testGroup.getNombre(), result.get().get(0).getGroupName());
        }
    }

    @Nested
    class AddMessageTests {
        @Test
        void addMessage_WhenMessageIsValid_AddsSuccessfully() {
            // Given
            String password = "password";
            when(encriptarSimetrico.encrypt(testMessage.getText(), password)).thenReturn(Either.right("encryptedText"));
            when(msgDao.addMessage(any(Message.class))).thenReturn(Either.right(null));

            // When
            CompletableFuture<Either<ErrorApp, Void>> result = messageService.addMessage(testMessage, password);

            // Then
            result.thenAccept(res -> {
                assertTrue(res.isRight());
                verify(msgDao).addMessage(any(Message.class));
            });
        }

        @Test
        void addPrivateMessage_WhenMessageIsValid_AddsSuccessfully() {
            // Given
            PrivateMessage privateMessage = new PrivateMessage(testUser.getName(), testTime,
                    "sign", "message", new HashMap<>(), testGroup.getNombre());
            PrivateGroup group = new PrivateGroup(testGroup.getNombre(),
                    List.of(testUser), "admin", testTime);
            PublicKey mockPublicKey = mock(PublicKey.class);

            when(privateGroupService.getGroupByName(testGroup.getNombre())).thenReturn(Either.right(group));
            when(asimetrico.generarClaveSimetrica()).thenReturn(Either.right("randomKey"));
            when(encriptarSimetrico.encrypt("message", "randomKey")).thenReturn(Either.right("encryptedMessage"));
            when(asimetrico.dameClavePublica(testUser.getName())).thenReturn(Either.right(mockPublicKey));
            when(asimetrico.encriptarAsimetricamente("randomKey", mockPublicKey)).thenReturn(Either.right(new byte[]{1,2,3}));
            when(msgDao.addPrivateMessage(any(PrivateMessage.class))).thenReturn(Either.right(null));

            // When
            CompletableFuture<Either<ErrorApp, Void>> result =
                    messageService.addMessage(privateMessage, testGroup.getNombre());

            // Then
            Either<ErrorApp, Void> finalResult = result.join(); // Esperamos a que complete
            assertTrue(finalResult.isRight());

            verify(privateGroupService).getGroupByName(testGroup.getNombre());
            verify(asimetrico).generarClaveSimetrica();
            verify(encriptarSimetrico).encrypt("message", "randomKey");
            verify(asimetrico).dameClavePublica(testUser.getName());
            verify(asimetrico).encriptarAsimetricamente("randomKey", mockPublicKey);
            verify(msgDao).addPrivateMessage(any(PrivateMessage.class));
        }

        @Test
        void addPrivateMessage_WhenMessageIsEmpty_ReturnsRight() {
            // Given
            PrivateMessage privateMessage = new PrivateMessage(testUser.getName(), testTime,
                    "sign", "", new HashMap<>(), testGroup.getNombre());

            // When
            CompletableFuture<Either<ErrorApp, Void>> result =
                    messageService.addMessage(privateMessage, testGroup.getNombre());

            // Then
            result.thenAccept(res -> {
                assertTrue(res.isRight());
                verifyNoInteractions(privateGroupService, asimetrico, encriptarSimetrico, msgDao);
            });
        }
    }

    @Nested
    class LastMessageTests {
        @Test
        void getLastMessage_WhenGroupHasMessages_ReturnsLastMessage() {
            // Given
            when(msgDao.getMessage()).thenReturn(Either.right(List.of(testMessage)));

            // When
            CompletableFuture<Either<ErrorApp, String>> result = messageService.getLastMessage(testGroup);

            // Then
            result.thenAccept(res -> {
                assertTrue(res.isRight());
                assertEquals(testMessage.toString(), res.get());
            });
        }

        @Test
        void getLastMessage_WhenPrivateGroupHasMessages_ReturnsLastMessage() {
            // Given
            PrivateGroup privateGroup = new PrivateGroup(testGroup.getNombre(),
                    List.of(testUser), "admin", testTime);
            PrivateMessage privateMessage = new PrivateMessage(testUser.getName(), testTime,
                    "sign", "message", new HashMap<>(), privateGroup.getNombre());
            when(msgDao.getPrivateMessage()).thenReturn(Either.right(List.of(privateMessage)));

            // When
            CompletableFuture<Either<ErrorApp, String>> result = messageService.getLastMessage(privateGroup);

            // Then
            result.thenAccept(res -> {
                assertTrue(res.isRight());
                assertEquals(privateMessage.getSender(), res.get());
            });
        }
    }

    @Nested
    class DecryptMessageTests {
        @Test
        void getDecryptedMessagesByGroupName_WhenValidCredentials_ReturnsDecryptedMessages() {
            // Given
            String password = "password";
            when(groupService.checkPwd(testGroup.getNombre(), password)).thenReturn(Either.right(null));
            when(groupService.getGroupByName(testGroup.getNombre())).thenReturn(Either.right(testGroup));
            when(msgDao.getMessage()).thenReturn(Either.right(List.of(testMessage)));
            when(encriptarSimetrico.decrypt(testMessage.getText(), password))
                    .thenReturn(Either.right("decryptedMessage"));

            // When
            CompletableFuture<Either<ErrorApp, List<Message>>> result =
                    messageService.getDecryptedMessagesByGroupName(testGroup.getNombre(), password);

            // Then
            result.thenAccept(res -> {
                assertTrue(res.isRight());
                assertEquals("decryptedMessage", res.get().get(0).getText());
            });
        }

        @Test
        void getDecryptedMessagesByPrivateGroupName_WhenValidCredentials_ReturnsDecryptedMessages() {
            // Given
            byte[] encryptedKey = new byte[]{1, 2, 3};
            String symmetricKey = "symmetricKey";
            PrivateMessage privateMessage = new PrivateMessage(testUser.getName(), testTime,
                    "sign", "encryptedMessage", Map.of(testUser.getName(), encryptedKey), testGroup.getNombre());

            PrivateKey privateKey = mock(PrivateKey.class);
            when(msgDao.getPrivateMessage()).thenReturn(Either.right(List.of(privateMessage)));
            when(asimetrico.dameClavePrivada(testUser.getName(), testUser.getPwd()))
                    .thenReturn(Either.right(privateKey));
            when(asimetrico.desencriptarAsimetricamente(encryptedKey, privateKey))
                    .thenReturn(Either.right(symmetricKey));
            when(encriptarSimetrico.decrypt("encryptedMessage", symmetricKey))
                    .thenReturn(Either.right("decryptedMessage"));

            // When
            CompletableFuture<Either<ErrorApp, List<PrivateMessage>>> result =
                    messageService.getDecryptedMessagesByPrivateGroupName(testGroup.getNombre(),
                            testUser.getName(), testUser.getPwd());

            // Then
            result.thenAccept(res -> {
                assertTrue(res.isRight());
                assertEquals(1, res.get().size());
                assertEquals("decryptedMessage", res.get().get(0).getEncryptedMessage());
                assertNull(res.get().get(0).getSymmetricKeysEncrypted());
            });
        }
    }
}