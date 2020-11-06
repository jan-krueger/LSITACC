import edu.um.core.security.SymmetricEncryption;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;

public class RSATest {

    public static void main(String[] args) throws IOException {
        /*PublicKey publicKey = RSA.getPublicKey(Base64.getDecoder().decode("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA28dC4WagJBeAgS+bZdAcis2woP6xZAunsH1gWtDwYLR6vr3F13ro8BLzmRSpTMGTcvcxufW3Svepit0KNh6znJ5Dq7LY4WOLurm4cdsOWHo5kg0EVDw1Xjg6RlnNLblvmkMU8yoec4e4jfNk7VX1+VhQD6V7EOEyL0oXktEYUe9GC2s1VS8YfLVCn0zgx7voFgSP4DapgJE1PFSrB110uX8fRtisx9mM4Z1P7Q7NXkI1b0s12vPxake8P8eMcsLiXgkYU0Rd8GMi6sgMuPtVxpshieltgLHQIUySc6+EH3L4cUk/GNViiAJTkC4UyfZ8Jo3LGLciXd3QgC5r8zaY7QIDAQAB"));
        PrivateKey privateKey = RSA.getPrivateKey(Base64.getDecoder().decode("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDbx0LhZqAkF4CBL5tl0ByKzbCg/rFkC6ewfWBa0PBgtHq+vcXXeujwEvOZFKlMwZNy9zG59bdK96mK3Qo2HrOcnkOrstjhY4u6ubhx2w5YejmSDQRUPDVeODpGWc0tuW+aQxTzKh5zh7iN82TtVfX5WFAPpXsQ4TIvSheS0RhR70YLazVVLxh8tUKfTODHu+gWBI/gNqmAkTU8VKsHXXS5fx9G2KzH2YzhnU/tDs1eQjVvSzXa8/FqR7w/x4xywuJeCRhTRF3wYyLqyAy4+1XGmyGJ6W2AsdAhTJJzr4QfcvhxST8Y1WKIAlOQLhTJ9nwmjcsYtyJd3dCALmvzNpjtAgMBAAECggEAP6SKJ39ybEEqAz7n/ymuujsqJNTcRZ5iZudHmcTbdwWMbePCgM2k2rCCPhgPlpyPuEEA81rGQz1IqV+s71Mgq//glL3rtAkBk4bodH/8vX5XYYXHdSUiRzB9H5YtpkoGmWNHk8rkmov405Jh7aBw5E3AkWdYJeEwiSUpgmhuZHseHygWE99RL8cp/l1ZnnHQ6n3U+LvtePGlgaceIhg8IJ9ARYsPfw5UCDHZ0SOkAtMaHfIiRnDtVn0gyhMo0HtYsJOMvccOTB/WgoJOBGIif3zlhPkd71ZPZrN20t7yOtbXaTAJuHf2U4BrOaHlZhW17mDLGriA2DROwsfGAa0J+QKBgQDzc3+FKddcI3aV+/WHKd1N6Zzse3C3LX9gYoQWTsHbNliFwwNTWDQzcCYF5Y8yYDZHMSJ/t+TcTuIcZTPmiFXz2LUJfrdhhkTXCqdZCvorAslrxrN/vFag+TRFwAW5ZOjt/z4HfoIi33Iapzhc6+ZGe+9iHhduWwIBR9MzisxVlwKBgQDnG2J1VFu9w4JCRIwX7TG7YUXXX7BqNKZQzM17NcGwwZ1Y8ce89Wh/edufWWmu7FxQbkkVM6lZXusLGsJxqbqIiu/Jo6Qo2/5DpHCozEyrFIweXEBwLOwFuxb1BE4vR+SGD6Ifjt0SV3Sj7vYARzzpuHL0W2bB13YkG22VCGk+GwKBgCQnwgd/yEkZxzHNIpC5SfGZQUHprzq1GhaxatZ5SdcJgioTZToKfm4pOKxlhFfB7C20gWAgam/Bk1kOOqABHWrCg1hupkNokm6xWVsjAzZCCrHj56KP0PnFKNs3AmKfNuypnIe2ZRNSxfppXTiHnEJZg5q5x1cPUwPnq9kawu/vAoGBAIspIz1xYZi/7GTI7+7MZkDlzu9kcE9Tir5mxGinsw0jWQMg+9wdjiIMl6G5bnjlXKUV863voM7u5+uU3ncg8/CacM9jWUs9RTsn/qq18oil48W4WRjzA+BjiuxY/ldcHBBZw47rMNWFkffQwZLjUpuN9LKNgy+/9STC21Z9bRn3AoGBAKJvDc0tnZIhACvkYXw5T4S5OTbIYgAMXytOTkJJ0JSOeFAHmRUwrcELq69RSCBR2EOJqDe23tIP0X5cvPmNmRTMJaeujmDw703MIn93ODpYm4J27ZjkLp6tHEUqS5u3UhmbstrvkeqTU3nYb/Np8x6Anl6XD4wX2w2JYFRMyW7f")); //Files.readAllBytes(Path.of("./test-certificates/client_3.pri")));

        System.out.println(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        System.out.println("-----------------------");
        System.out.println(Base64.getEncoder().encodeToString(privateKey.getEncoded()));*/

        SecretKey secretKey = SymmetricEncryption.generateKey();
        IvParameterSpec ivParameterSpec = SymmetricEncryption.generateIvParameterSpec();

        String message = "Hello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, WorldHello, World";
        System.out.println(message);
        System.out.println(SymmetricEncryption.encrypt(secretKey, ivParameterSpec, message));
        System.out.println(SymmetricEncryption.decrypt(secretKey, ivParameterSpec, SymmetricEncryption.encrypt(secretKey, ivParameterSpec, message)));
    }

}
