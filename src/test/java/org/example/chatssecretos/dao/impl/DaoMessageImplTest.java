package org.example.chatssecretos.dao.impl;

import io.vavr.control.Either;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.errors.ErrorAppMessages;
import org.example.chatssecretos.domain.modelo.Message;
import org.example.chatssecretos.domain.modelo.PrivateMessage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DaoMessageImplTest {

    @Mock
    private Database db;

    @InjectMocks
    private DaoMessageImpl daoMessage;

    @Nested
    class AddMessageTests {

        @Test
        public void test_add_message_to_empty_list_success() {
            // given
            Message msg = new Message("test message", LocalDateTime.now(), "sender", "group1");
            List<Message> emptyList = new ArrayList<>();
            when(db.loadMessage()).thenReturn(Either.right(emptyList));
            when(db.saveMessages(any())).thenReturn(Either.right(null));

            // when
            Either<ErrorApp, Void> result = daoMessage.addMessage(msg);

            // then
            assertTrue(result.isRight());
            verify(db).loadMessage();
            verify(db).saveMessages(argThat(list -> list.size() == 1 && list.get(0).equals(msg)));
        }
    }

    @Nested
    class GetMessageTests {

        @Test
        public void test_get_message_success() {
            // given
            List<Message> expectedMessages = List.of(
                    new Message("test message", LocalDateTime.now(), "user1", "group1")
            );
            when(db.loadMessage()).thenReturn(Either.right(expectedMessages));

            // when
            Either<ErrorApp, List<Message>> result = daoMessage.getMessage();

            // then
            assertTrue(result.isRight());
            assertEquals(expectedMessages, result.get());
            verify(db).loadMessage();
        }

        @Test
        public void test_get_message_database_error() {
            // given
            when(db.loadMessage()).thenReturn(Either.left(ErrorAppMessages.MESSAGES_NOT_FOUND));

            // when
            Either<ErrorApp, List<Message>> result = daoMessage.getMessage();

            // then
            assertTrue(result.isLeft());
            assertEquals(ErrorAppMessages.MESSAGES_NOT_FOUND, result.getLeft());
            verify(db).loadMessage();
        }
    }

    @Nested
    class AddPrivateMessageTests {

        @Test
        public void test_add_private_message_to_empty_list_success() {
            // given
            PrivateMessage msg = new PrivateMessage("sender", LocalDateTime.now(), "sign", "encryptedMessage", new HashMap<>(), "group1");
            List<PrivateMessage> emptyList = new ArrayList<>();
            when(db.loadPrivateMessage()).thenReturn(Either.right(emptyList));
            when(db.savePrivateMessages(any())).thenReturn(Either.right(null));

            // when
            Either<ErrorApp, Void> result = daoMessage.addPrivateMessage(msg);

            // then
            assertTrue(result.isRight());
            verify(db).loadPrivateMessage();
            verify(db).savePrivateMessages(argThat(list -> list.size() == 1 && list.get(0).equals(msg)));
        }

        @Test
        public void test_add_private_message_file_not_found() {
            // given
            PrivateMessage msg = new PrivateMessage("sender", LocalDateTime.now(), "sign", "encryptedMessage", new HashMap<>(), "group1");
            when(db.loadPrivateMessage()).thenReturn(Either.left(ErrorAppMessages.MESSAGES_NOT_FOUND));

            // when
            Either<ErrorApp, Void> result = daoMessage.addPrivateMessage(msg);

            // then
            assertTrue(result.isLeft());
            assertEquals(ErrorAppMessages.MESSAGES_NOT_FOUND, result.getLeft());
            verify(db).loadPrivateMessage();
            verify(db, never()).savePrivateMessages(any());
        }
    }

    @Nested
    class GetPrivateMessageTests {

        @Test
        public void test_get_private_messages_success() {
            // given
            List<PrivateMessage> expectedMessages = List.of(
                    new PrivateMessage("user1", LocalDateTime.now(), "sign1", "msg1", new HashMap<>(), "group1")
            );
            when(db.loadPrivateMessage()).thenReturn(Either.right(expectedMessages));

            // when
            Either<ErrorApp, List<PrivateMessage>> result = daoMessage.getPrivateMessage();

            // then
            assertTrue(result.isRight());
            assertEquals(expectedMessages, result.get());
            verify(db).loadPrivateMessage();
        }

        @Test
        public void test_get_private_messages_file_not_found() {
            // given
            when(db.loadPrivateMessage()).thenReturn(Either.left(ErrorAppMessages.MESSAGES_NOT_FOUND));

            // when
            Either<ErrorApp, List<PrivateMessage>> result = daoMessage.getPrivateMessage();

            // then
            assertTrue(result.isLeft());
            assertEquals(ErrorAppMessages.MESSAGES_NOT_FOUND, result.getLeft());
            verify(db).loadPrivateMessage();
        }
    }
}