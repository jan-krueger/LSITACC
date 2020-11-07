package edu.um.core.protocol;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import edu.um.core.Core;
import edu.um.core.protocol.packets.EncryptedPacket;
import edu.um.core.protocol.packets.ProxyPacket;
import edu.um.core.security.RSA;
import edu.um.core.protocol.packets.Packet;
import edu.um.core.security.SymmetricEncryption;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.Optional;

public class PacketParser {

    private final static Gson gson = new Gson();

    private PacketParser() {}

    public static Optional<Packet> parse(String data, PrivateKey privateKey, boolean isClient) {
        JsonObject object;

        try {
            object = gson.fromJson(data, JsonObject.class);
        } catch (JsonSyntaxException exception) {

            StringBuilder decryptedData = new StringBuilder();

            String[] chunks = data.split("_\\|_\\|_");
            for(String chunk : chunks) {
                String decrypted = RSA.decrypt(chunk, privateKey);
                if(decrypted == null) {
                    Core.LOGGER.warning("Invalid packet could not be decrypted.");
                    return Optional.empty();
                }
                decryptedData.append(decrypted);
            }

            data = decryptedData.toString();
            object = gson.fromJson(decryptedData.toString(), JsonObject.class);
        }

        if(object.has("id")) {
            Optional<Packet> packet = Packets.create(object.get("id").getAsInt());

            // --- TODO check if authenticated packet

            if(packet.isPresent()) {
                if(object.has("payload") && object.get("payload").isJsonArray()) {
                    JsonArray array = object.getAsJsonArray("payload");
                    array.forEach(entry -> {
                        JsonObject entryObject = entry.getAsJsonObject();
                        packet.get().add(entryObject.get("key").getAsString(), entryObject.get("value").getAsString());
                    });
                }

                if(object.has("encryptedPayload")) {

                    JsonObject encryptedPayload = object.getAsJsonObject("encryptedPayload");
                    JsonArray encryptedArray = encryptedPayload.getAsJsonArray("data");

                    final String encryptedKey = encryptedPayload.get("key").getAsString();
                    final String encryptedIvParameterSpec = encryptedPayload.get("ivParameterSpec").getAsString();

                    if(isClient) {
                        //--- recover key
                        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(RSA.decrypt(encryptedKey, privateKey)), "AES");
                        IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(RSA.decrypt(encryptedIvParameterSpec, privateKey)));

                        encryptedArray.forEach(e -> {
                            JsonObject entry = (JsonObject) e;
                            final String key = entry.get("key").getAsString();
                            final String value = SymmetricEncryption.decrypt(secretKeySpec, ivParameterSpec,
                                    entry.get("value").getAsString());
                            ((EncryptedPacket)packet.get()).addRawToEncrypted(key, value);
                        });
                    } else {
                        return Optional.of(new ProxyPacket(packet.get().getType(), object));
                    }

                }

            }

            return packet;

        }

        return Optional.empty();
    }

}
