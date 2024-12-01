package org.example.chatssecretos.domain.service;

import io.vavr.control.Either;
import lombok.extern.slf4j.Slf4j;
import org.example.chatssecretos.dao.impl.DaoMessageImpl;
import org.example.chatssecretos.domain.errors.ErrorAppSecurity;
import org.example.chatssecretos.domain.modelo.*;
import org.example.chatssecretos.utils.security.*;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.utils.Constantes;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class MessageService {
    private final GroupService groupService;
    private final DaoMessageImpl msgDao;
    private final EncriptarSimetrico encriptarSimetrico;
    private final Asimetrico asimetrico;
    private final PrivateGroupService privateGroupService;
    private final DaoMessageImpl daoMessageImpl;

    public MessageService(DaoMessageImpl msgDao, GroupService groupService,
                          EncriptarSimetrico encriptarSimetrico, Asimetrico asimetrico,
                          PrivateGroupService privateGroupService, DaoMessageImpl daoMessageImpl) {
        this.msgDao = msgDao;
        this.groupService = groupService;
        this.encriptarSimetrico = encriptarSimetrico;
        this.asimetrico = asimetrico;
        this.privateGroupService = privateGroupService;
        this.daoMessageImpl = daoMessageImpl;
    }

    public Either<ErrorApp, List<Message>> getMessagesByGroup(Group group) {
        return msgDao.getMessage().map(messages -> messages.stream()
                .filter(message -> message.getGrupo().equals(group.getNombre()))
                .toList());
    }

    public Either<ErrorApp, List<PrivateMessage>> getMessagesByGroup(String groupName) {
        return msgDao.getPrivateMessage().map(messages -> messages.stream()
                .filter(message -> message.getGroupName().equals(groupName))
                .toList());
    }

    @Async
    public CompletableFuture<Either<ErrorApp, Void>> addMessage(PrivateMessage message,
                                                                String groupName) {
        if (message.getEncryptedMessage().isEmpty())
            return CompletableFuture.completedFuture(Either.right(null));

        return CompletableFuture.supplyAsync(() -> {
            Map<String, byte[]> symmetricKeysEncrypted = new HashMap<>();
            Either<ErrorApp, PrivateGroup> privateGroup = privateGroupService.getGroupByName(groupName);

            if (privateGroup.isLeft()) return Either.left(privateGroup.getLeft());
            try {
                Either<ErrorApp, String> randomPwd = asimetrico.generarClaveSimetrica();
                if (randomPwd.isLeft()) return Either.left(randomPwd.getLeft());

                Either<ErrorApp, String> encryptedMessageResult = encriptarSimetrico
                        .encrypt(message.getEncryptedMessage(), randomPwd.get());
                if (encryptedMessageResult.isLeft())
                    return Either.left(encryptedMessageResult.getLeft());

                PrivateGroup group = privateGroup.get();
                for (User user : group.getMiembros()) {
                    Either<ErrorApp, PublicKey> publicKey = asimetrico.dameClavePublica(user.getName());
                    if (publicKey.isLeft())
                        return Either.left(publicKey.getLeft());
                    Either<ErrorApp, byte[]> encryptedKey = asimetrico.encriptarAsimetricamente(randomPwd.get(), publicKey.get());
                    if (encryptedKey.isLeft())
                        return Either.left(encryptedKey.getLeft());
                    symmetricKeysEncrypted.put(user.getName(), encryptedKey.get());
                }

                message.setSign(null);
                message.setEncryptedMessage(encryptedMessageResult.get());
                message.setSymmetricKeysEncrypted(symmetricKeysEncrypted);

                return daoMessageImpl.addPrivateMessage(message);
            } catch (Exception e) {
                log.error(Constantes.E_GENERANDO_CLAVES, e);
                return Either.left(ErrorAppSecurity.E_GENERANDO_CLAVES);
            }
        });
    }

    @Async
    public CompletableFuture<Either<ErrorApp, Void>> addMessage(Message message, String password) {
        if (message.getText().isEmpty())
            return CompletableFuture.completedFuture(Either.right(null));
        return CompletableFuture.completedFuture(encriptarSimetrico.encrypt(message.getText(), password)
                .flatMap(encryptedText -> {
                    message.setText(encryptedText);
                    return msgDao.addMessage(message);
                }));
    }

    @Async
    public CompletableFuture<Either<ErrorApp, String>> getLastMessage(Group group) {
        return CompletableFuture.completedFuture(
                getMessagesByGroup(group).flatMap(messages ->
                        messages.isEmpty()
                                ? Either.right(Constantes.SIN_MENSAJES)
                                : Either.right(messages.getLast().toString())
                ));
    }

    @Async
    public CompletableFuture<Either<ErrorApp, String>> getLastMessage(PrivateGroup group) {
        return CompletableFuture.completedFuture(
                getMessagesByGroup(group.getNombre()).flatMap(messages ->
                        messages.isEmpty()
                                ? Either.right(Constantes.SIN_MENSAJES)
                                : Either.right(messages.getLast().getSender())
                ));
    }

    @Async
    public CompletableFuture<Either<ErrorApp, List<Message>>> getDecryptedMessagesByGroupName(String groupName, String pwd) {
        return CompletableFuture.completedFuture(
                groupService.checkPwd(groupName, pwd)
                        .flatMap(valid -> groupService.getGroupByName(groupName))
                        .flatMap(this::getMessagesByGroup)
                        .flatMap(messages -> desencriptarMensajes(messages, pwd))
        );
    }

    @Async
    public CompletableFuture<Either<ErrorApp, List<PrivateMessage>>>
    getDecryptedMessagesByPrivateGroupName(String groupName, String username, String contrasenya) {
        List<PrivateMessage> mensajesDesencriptados = new ArrayList<>();

        Either<ErrorApp, List<PrivateMessage>> mensajes = getMessagesByGroup(groupName);
        if (mensajes.isLeft())
            return CompletableFuture.completedFuture(mensajes);

        for (PrivateMessage mensaje : mensajes.get()) {
            try {
                byte[] encryptedKey = mensaje.getSymmetricKeysEncrypted().get(username);
                Either<ErrorApp, PrivateKey> clavePrivada = asimetrico.dameClavePrivada(username, contrasenya);
                if (clavePrivada.isLeft())
                    return CompletableFuture.completedFuture(Either.left(clavePrivada.getLeft()));

                Either<ErrorApp, String> symmetricKey = asimetrico
                        .desencriptarAsimetricamente(encryptedKey, clavePrivada.get());
                if (symmetricKey.isLeft())
                    return CompletableFuture.completedFuture(Either.left(symmetricKey.getLeft()));
                encriptarSimetrico.decrypt(mensaje.getEncryptedMessage(), symmetricKey.get()).flatMap(r ->
                {
                    PrivateMessage mensajeDesencriptado = new PrivateMessage();
                    mensajeDesencriptado.setSender(mensaje.getSender());
                    mensajeDesencriptado.setTimestamp(mensaje.getTimestamp());
                    mensajeDesencriptado.setEncryptedMessage(r);
                    mensajeDesencriptado.setSymmetricKeysEncrypted(null);

                    mensajesDesencriptados.add(mensajeDesencriptado);
                    return Either.right(mensajeDesencriptado);
                });
            } catch (Exception e) {
                log.error(Constantes.E_DESENCRIPTAR_MENSAJE, e);
                return CompletableFuture.completedFuture(
                        Either.left(ErrorAppSecurity.ERROR_DESENCRIPTAR_MENSAJE));
            }
        }
        return CompletableFuture.completedFuture(Either.right(mensajesDesencriptados));
    }

    private Either<ErrorApp, List<Message>> desencriptarMensajes(List<Message> mensajes, String pwd) {
        List<Either<ErrorApp, Message>> result = mensajes.stream()
                .map(message -> encriptarSimetrico.decrypt(message.getText(), pwd)
                        .map(encryptedText -> {
                            message.setText(encryptedText);
                            return message;
                        }))
                .toList();

        List<ErrorApp> errors = result.stream().filter(Either::isLeft).map(Either::getLeft).toList();
        if (!errors.isEmpty())
            return Either.left(errors.getFirst());

        List<Message> validMessages = result.stream()
                .filter(Either::isRight)
                .map(Either::get)
                .toList();
        return Either.right(validMessages);
    }
}
