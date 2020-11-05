package edu.um.core.protocol;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.um.core.protocol.packets.Packet;

import java.util.Optional;

public class PacketParser {

    private final static Gson gson = new Gson();

    private PacketParser() {}

    public static Optional<Packet> parse(String data) {
        JsonObject object = gson.fromJson(data, JsonObject.class);

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

        }

        return Optional.empty();
    }

}
