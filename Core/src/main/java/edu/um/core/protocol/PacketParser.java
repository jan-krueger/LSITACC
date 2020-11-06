package edu.um.core.protocol;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import edu.um.core.Core;
import edu.um.core.RSA;
import edu.um.core.protocol.packets.Packet;

import java.security.PrivateKey;
import java.util.Optional;

public class PacketParser {

    private final static Gson gson = new Gson();

    private PacketParser() {}

    public static Optional<Packet> parse(String data, PrivateKey privateKey) {
        JsonObject object;

        try {
            object = gson.fromJson(data, JsonObject.class);
        } catch (JsonSyntaxException exception) {
            String decryptedData = RSA.decrypt(data, privateKey);
            if(decryptedData == null) {
                Core.LOGGER.warning("Invalid packet could not be decrypted.");
                return null;
            }
            object = gson.fromJson(data, JsonObject.class);
        }

        if(object.has("id")) {
            Optional<Packet> packet = Packets.create(object.get("id").getAsInt());

            // --- TODO check if authenticated packet

            if(packet.isPresent() && object.has("payload") && object.get("payload").isJsonArray()) {
                JsonArray array = object.getAsJsonArray("payload");
                array.forEach(entry -> {
                    JsonObject entryObject = entry.getAsJsonObject();
                    packet.get().add(entryObject.get("key").getAsString(), entryObject.get("value").getAsString());
                });
                return packet;
            }

            return packet;

        }

        return Optional.empty();
    }

}
