package edu.um.maspalomas.filters;

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
