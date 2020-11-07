package edu.um.core.protocol.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.um.core.protocol.Packets;
import edu.um.core.security.RSA;
import edu.um.core.security.SymmetricEncryption;
import io.netty.buffer.ByteBuf;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This type of packets offers the ability to encrypt certain fields. This is done by using a symmetric AES encryption
 * to encrypt the fields. The AES key itself is then encrypted using the receiver's RSA public key, and all of that is
 * transmitted.
 */
public class EncryptedPacket extends AuthenticatedPacket {

    private final PublicKey publicKey;
    private final HashSet<String> encryptedKeys;

    private Map<String, String> encryptedData = new HashMap<>();


    //TODO maybe only generate them if we actually need them, could otherwise be a bit wasteful for the server to always
    // generate keys even though it does not need them (cuz it is just a proxy)
    private final IvParameterSpec ivParameterSpec = SymmetricEncryption.generateIvParameterSpec();
    private final SecretKey secretKey = SymmetricEncryption.generateKey();

    protected EncryptedPacket(Packets id, String authToken, PublicKey publicKey, HashSet<String> requiredData,
                              HashSet<String> encryptedKeys) {
        super(id, authToken, requiredData);
        this.publicKey = publicKey;
        this.encryptedKeys = encryptedKeys;
    }

    @Override
    public String get(String key) {
        if(this.encryptedKeys.contains(key)) {
            return this.encryptedData.get(key);
        }
        return super.get(key);
    }

    @Override
    public Packet add(String key, String value) {
        if(this.encryptedKeys.contains(key)) {
            encryptedData.put(key, SymmetricEncryption.encrypt(secretKey, ivParameterSpec, value));
            return this;
        } else {
            return super.add(key, value);
        }
    }

    public void addRawToEncrypted(String key, String encryptedValue) {
        encryptedData.put(key, encryptedValue);
    }

    @Override
    protected JsonObject buildObject() {
        JsonObject original = super.buildObject();
        JsonObject encrypted = original.deepCopy();

        //--- generate key for symmetric encryption

        //--- encrypt the key and the iv with the receiver's public RSA key, so they can retrieve the information
        JsonObject encryptedPayload = new JsonObject();
        encryptedPayload.addProperty("key",
                RSA.encrypt(Base64.getEncoder().encodeToString(secretKey.getEncoded()), this.publicKey));
        encryptedPayload.addProperty("ivParameterSpec",
                RSA.encrypt(Base64.getEncoder().encodeToString(ivParameterSpec.getIV()), this.publicKey));

        //--- remove the encrypted fields from the new object, and add it to the special collection and encrypt it
        JsonArray encryptedFields = new JsonArray();
        for(Map.Entry<String, String> entry : this.encryptedData.entrySet()) {
            JsonObject object = new JsonObject();
            object.addProperty("key", entry.getKey());
            object.addProperty("value", entry.getValue());
            encryptedFields.add(object);
        }
        encryptedPayload.add("data", encryptedFields);

        encrypted.add("encryptedPayload", encryptedPayload);
        return encrypted;
    }

    @Override
    public ByteBuf build() {
        return super.build();

    }

    @Override
    public String toString() {
        return "EncryptedPacket{" +
                "publicKey=" + publicKey +
                ", encryptedKeys=" + encryptedKeys +
                ", encryptedData=" + encryptedData +
                ", ivParameterSpec=" + ivParameterSpec +
                ", secretKey=" + secretKey +
                '}';
    }
}
