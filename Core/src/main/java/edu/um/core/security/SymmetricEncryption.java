package edu.um.core.security;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SymmetricEncryption {

    private final static SecureRandom random = new SecureRandom();
    private static KeyGenerator keyGenerator;

    static {
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static IvParameterSpec generateIvParameterSpec() {
        byte[] iv = new byte[16]; //TODO Well, in theory this depends on the key that we use to encrypt all of this in the end
        // in theory AES uses up 128bits, so a 2048bit key can encrypt up to 2048/8-11 bytes ((11 bytes for PKCS#1 v1.5 padding))
        random.nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static SecretKey generateKey() {
        return keyGenerator.generateKey();
    }

    public static String encrypt(SecretKey secretKey, IvParameterSpec ivParameterSpec, String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(SecretKey secretKey, IvParameterSpec ivParameterSpec, String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(data)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

}
