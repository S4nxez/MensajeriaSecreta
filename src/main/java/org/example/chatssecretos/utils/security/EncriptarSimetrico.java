package org.example.chatssecretos.utils.security;

import com.google.common.primitives.Bytes;
import io.vavr.control.Either;
import lombok.extern.log4j.Log4j2;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.errors.ErrorAppDataBase;
import org.example.chatssecretos.utils.Constantes;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;



/**
 *
 * @author oscar
 */
@Log4j2
@Component
public class EncriptarSimetrico {

    public Either<ErrorApp, String> encrypt(String strToEncrypt, String secret) {
        try {
            byte [] iv = new byte[12];
            byte []salt = new byte[16];
            SecureRandom sr = new SecureRandom();
            sr.nextBytes(iv);
            sr.nextBytes(salt);

            Cipher cipher = createCipher(secret, iv, salt, Cipher.ENCRYPT_MODE);
            return Either.right(Base64.getUrlEncoder().encodeToString(Bytes.concat(iv,salt,
                    cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)))));
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return Either.left(ErrorAppDataBase.ERROR_DATABASE);
        }
    }

    public Either<ErrorApp, String> decrypt(String strToDecrypt, String secret) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(strToDecrypt);
            byte[] iv = Arrays.copyOf(decoded, 12);
            byte[] salt = Arrays.copyOfRange(decoded, 12, 28);

            Cipher cipher = createCipher(secret, iv, salt, Cipher.DECRYPT_MODE);
            return Either.right(new String(cipher
                    .doFinal(Arrays.copyOfRange(decoded, 28, decoded.length)), StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Either.left(ErrorAppDataBase.ERROR_LEER_FICHEROS);
        }
    }

    private Cipher createCipher(String secret, byte[] iv, byte[] salt, int mode) throws GeneralSecurityException {
        GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);

        SecretKeyFactory factory = SecretKeyFactory.getInstance(Constantes.ALGORITMO);
        KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), Constantes.AES);

        Cipher cipher = Cipher.getInstance(Constantes.AES_GCM_NO_PADDING);
        cipher.init(mode, secretKey, parameterSpec);
        return cipher;
    }
}