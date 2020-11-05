package edu.um.core;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSA {

    private static KeyFactory keyFactory;

    static {
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private RSA() {}

    public static PublicKey getPublicKey(String base64) {
        return getPublicKey(Base64.getDecoder().decode(base64));
    }

    public static PrivateKey getPrivateKey(String base64) {
        return getPrivateKey(Base64.getDecoder().decode(base64));
    }

    public static PublicKey getPublicKey(Path path) throws IOException {
        return getPublicKey(Files.readAllBytes(path));
    }

    public static PrivateKey getPrivateKey(Path path) throws IOException {
        return getPrivateKey(Files.readAllBytes(path));
    }

    public static PublicKey getPublicKey(byte[] publicKey){
        try{
            X509EncodedKeySpec spec =
                    new X509EncodedKeySpec(publicKey);
            return keyFactory.generatePublic(spec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PrivateKey getPrivateKey(byte[] privateKey){
        try {
            PKCS8EncodedKeySpec spec =
                    new PKCS8EncodedKeySpec(privateKey);
            return keyFactory.generatePrivate(spec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt(String data, PublicKey publicKey) throws BadPaddingException, IllegalBlockSizeException,
            InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    public static String decrypt(String data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(data)));
    }


}
