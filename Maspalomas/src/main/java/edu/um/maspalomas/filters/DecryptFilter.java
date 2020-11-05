package edu.um.maspalomas.filters;

import edu.um.core.RSA;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

public class DecryptFilter  {

    /*@Override
    public NextAction handleWrite(FilterChainContext ctx) {
        Object o = ctx.getMessage();

        String message = "Hello, World!";

        try {
            PublicKey publicKey = RSA.getPublicKey(Files.readAllBytes(Paths.get("./test-certificates/server.pub")));
            PrivateKey privateKey = RSA.getPrivateKey(Files.readAllBytes(Paths.get("./test-certificates/server.pri")));
            byte[] encrypted = RSA.encrypt(message, publicKey);
            System.out.println(Arrays.toString(encrypted));
            System.out.println(RSA.decrypt(encrypted, privateKey));
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }


        System.out.println("DecryptFilter: " + o.toString());
        return ctx.getInvokeAction();
    }*/

}
