import edu.um.core.RSA;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RSATest {

    public static void main(String[] args) throws IOException {
        RSA.getPublicKey(Files.readAllBytes(Path.of("./test-certificates/server.pub")));
        RSA.getPrivateKey(Files.readAllBytes(Path.of("./test-certificates/server.der")));
    }

}
