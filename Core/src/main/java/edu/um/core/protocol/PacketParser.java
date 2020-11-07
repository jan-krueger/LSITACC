package edu.um.core.protocol;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import edu.um.core.Core;
import edu.um.core.protocol.packets.ProxyPacket;
import edu.um.core.protocol.types.EncryptedPacket;
import edu.um.core.protocol.types.ProxiedPacket;
import edu.um.core.security.RSA;
import edu.um.core.protocol.types.Packet;
import edu.um.core.security.SymmetricEncryption;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.Optional;

public class PacketParser {

    private final static Gson gson = new Gson();

    public PacketParser() {}

    /**
     * @param data
     * @param privateKey
     * @return
     */
    private static Result parse(String data, PrivateKey privateKey) {
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
                    return null;
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

                return new Result(data, object, packet.get());

            }


        }

        return null;
    }


    public static class Server {

        public static Optional<Packet> parse(String data, PrivateKey privateKey) {
            Result result = PacketParser.parse(data, privateKey);

            if(result == null) {
                return Optional.empty();
            }

            Packet packet = result.packet;
            JsonObject object = result.object;

            if(packet instanceof ProxiedPacket) {
                return Optional.of(new ProxyPacket(packet.getType(), (ProxiedPacket) packet, object));
            }

            return Optional.of(packet);

        }

    }

    public static class Client {

        public static Optional<Packet> parse(String data, PrivateKey privateKey) {
            Result result = PacketParser.parse(data, privateKey);

            if (result == null) {
                return Optional.empty();
            }

            JsonObject object = result.object;
            Packet packet = result.packet;
            if(object.has("encryptedPayload")) {
                //--- recover key
                JsonObject encryptedPayload = object.getAsJsonObject("encryptedPayload");
                JsonArray encryptedArray = encryptedPayload.getAsJsonArray("data");

                final String encryptedKey = encryptedPayload.get("key").getAsString();
                final String encryptedIvParameterSpec = encryptedPayload.get("ivParameterSpec").getAsString();

                SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(RSA.decrypt(encryptedKey, privateKey)), "AES");
                IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(RSA.decrypt(encryptedIvParameterSpec, privateKey)));

                encryptedArray.forEach(e -> {
                    JsonObject entry = (JsonObject) e;
                    final String key = entry.get("key").getAsString();
                    final String value = SymmetricEncryption.decrypt(secretKeySpec, ivParameterSpec,
                            entry.get("value").getAsString());
                    ((EncryptedPacket)packet).addRawToEncrypted(key, value);
                });

            }

            return Optional.of(packet);
        }

    }

    private static class Result {

        public String rawData;
        public JsonObject object;
        public Packet packet;

        public Result(String rawData, JsonObject object, Packet packet) {
            this.rawData = rawData;
            this.object = object;
            this.packet = packet;
        }

    }

}
