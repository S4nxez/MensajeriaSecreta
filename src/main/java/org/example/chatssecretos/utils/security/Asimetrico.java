package org.example.chatssecretos.utils.security;

import io.vavr.control.Either;
import lombok.extern.log4j.Log4j2;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.example.chatssecretos.domain.errors.ErrorApp;
import org.example.chatssecretos.domain.errors.ErrorAppSecurity;
import org.example.chatssecretos.utils.Constantes;
import org.example.chatssecretos.utils.config.Configuration;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.util.Date;

@Log4j2
@Component
public class Asimetrico {

    private final Configuration config;

    public Asimetrico(Configuration config) {
        this.config = config;
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public Either <ErrorApp, Void> generarYGuardarClavesUsuario(String alias, String password) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
            keyPairGenerator.initialize(new ECGenParameterSpec("secp521r1"));
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            Either<ErrorApp, X509Certificate> certificate = generateSignedCertificate(keyPair);
            if (certificate.isLeft())
                return Either.left(certificate.getLeft());

            KeyStore keystore = KeyStore.getInstance("JKS");
            try (FileInputStream fis = new FileInputStream(config.getPathKeyStore())) {
                keystore.load(fis, config.getKeystorePassword().toCharArray());
            } catch (IOException e) {
                log.error(Constantes.E_BBDD, e);
                return Either.left(ErrorAppSecurity.E_GENERANDO_CLAVES);
            }

            keystore.setKeyEntry(alias, keyPair.getPrivate(), password.toCharArray(),
                    new Certificate[]{certificate.get()});

            try (FileOutputStream fos = new FileOutputStream(config.getPathKeyStore())) {
                keystore.store(fos, config.getKeystorePassword().toCharArray());
            } catch (IOException e) {
                log.error(Constantes.E_BBDD, e);
                return Either.left(ErrorAppSecurity.E_GENERANDO_CLAVES);
            }
        }catch (Exception e){
            log.error(Constantes.E_GENERANDO_CLAVES, e);
            return Either.left(ErrorAppSecurity.E_GENERANDO_CLAVES);
        }
        return Either.right(null);
    }

    public Either<ErrorApp, PrivateKey> dameClavePrivada(String alias, String password) {
    try (FileInputStream fis = new FileInputStream(config.getPathKeyStore())) {
        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(fis, config.getKeystorePassword().toCharArray());
        return Either.right((PrivateKey) keystore.getKey(alias, password.toCharArray()));
    } catch (Exception e) {
        log.error(e);
        return Either.left(ErrorAppSecurity.E_PEDIR_CLAVE_PRIVADA);
    }
}

    public Either<ErrorApp,PublicKey> dameClavePublica(String alias) {
        try {
            KeyStore keystore = KeyStore.getInstance("JKS");
            try (FileInputStream fis = new FileInputStream(config.getPathKeyStore())) {
                keystore.load(fis, config.getKeystorePassword().toCharArray());
            }
            Certificate cert = keystore.getCertificate(alias);
            return Either.right(cert.getPublicKey());
        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException e) {
            log.error(e);
            return Either.left(ErrorAppSecurity.E_PEDIR_CLAVE_PRIVADA);
        }
    }

    private static Either<ErrorApp, X509Certificate> generateSignedCertificate(KeyPair keyPair){
        X500Name issuer = new X500Name("CN=Test Certificate");

        Date notBefore = new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24);
        Date notAfter = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365 * 2);
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());

        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());
        X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(
                issuer, serialNumber, notBefore, notAfter, issuer, subjectPublicKeyInfo
        );

        ContentSigner contentSigner = null;
        try {
            contentSigner = new JcaContentSignerBuilder("SHA256withECDSA")
                    .setProvider("BC")
                    .build(keyPair.getPrivate());

        } catch (OperatorCreationException e) {
            log.error(Constantes.E_GENERANDO_CLAVES, e);
            return Either.left(ErrorAppSecurity.E_GENERANDO_CLAVES);
        }
        X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);

        try {
            return Either.right(new JcaX509CertificateConverter()
                    .setProvider("BC")
                    .getCertificate(certificateHolder));
        } catch (CertificateException e){
            log.error(Constantes.E_GENERANDO_CLAVES, e);
            return Either.left(ErrorAppSecurity.E_GENERANDO_CLAVES);
        }
    }

    public Either<ErrorApp, String> generarClaveSimetrica()  {
        SecureRandom secureRandom = new SecureRandom();
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            log.error(Constantes.E_GENERANDO_CLAVES, e);
            return Either.left(ErrorAppSecurity.E_GENERANDO_CLAVES);
        }
        keyGenerator.init(256, secureRandom);
        SecretKey secretKey = keyGenerator.generateKey();
        return Either.right(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
    }

    public Either<ErrorApp, byte[]> encriptarAsimetricamente(String mensaje, PublicKey clavePublica) {
        try {
            Cipher cipher = Cipher.getInstance(Constantes.ECIESWITH_AES_CBC, "BC");

            SecureRandom random = new SecureRandom();
            byte[] iv = new byte[16];
            random.nextBytes(iv);

            IESParameterSpec params = new IESParameterSpec(null, null, 128, 128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, clavePublica, params);

            byte[] encryptedMessage = cipher.doFinal(mensaje.getBytes(StandardCharsets.UTF_8));

            ByteBuffer buffer = ByteBuffer.allocate(iv.length + encryptedMessage.length);
            buffer.put(iv);
            buffer.put(encryptedMessage);

            return Either.right(buffer.array());
        } catch (Exception e) {
            log.error(Constantes.E_ENCRIPTAR_MENSAJE, e);
            return Either.left(ErrorAppSecurity.E_ENCRIPTAR_MENSAJE);
        }
    }

    public Either<ErrorApp, String> desencriptarAsimetricamente(byte[] mensajeConIv,
                                                                PrivateKey clavePrivada) {
            ByteBuffer buffer = ByteBuffer.wrap(mensajeConIv);
            byte[] iv = new byte[16];
            buffer.get(iv);
            byte[] encryptedMessage = new byte[buffer.remaining()];
            buffer.get(encryptedMessage);

            try {
                Cipher cipher = Cipher.getInstance(Constantes.ECIESWITH_AES_CBC, "BC");
                IESParameterSpec params = new IESParameterSpec(null, null, 128, 128, iv);
                cipher.init(Cipher.DECRYPT_MODE, clavePrivada, params);

                byte[] decryptedBytes = cipher.doFinal(encryptedMessage);
                return Either.right(new String(decryptedBytes, StandardCharsets.UTF_8));

            } catch (Exception e) {
                log.error(Constantes.E_DESENCRIPTAR_MENSAJE, e);
                return Either.left(ErrorAppSecurity.E_DESENCRIPTAR_MENSAJE);
            }
    }
}

