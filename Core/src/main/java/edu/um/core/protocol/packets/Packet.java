package edu.um.core.protocol.packets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Packet {

    private final int id;
    private final Set<String> requiredData;

    /**
     *
     * @param id The UNIQUE id of the packet.
     * @param requiredData The data keys required for this packet.
     */
    protected Packet(Packets id, HashSet<String> requiredData) {
        this.id = id.getId();
        this.requiredData = requiredData;
    }

    public int getId() {
        return id;
    }

    public Set<String> getRequiredData() {
        return requiredData;
    }

    public Builder create() {
        return new Builder(this);
    }

    public static class Builder {

        private final Packet packet;
        private Map<String, String> payload = new HashMap<>();

        private Builder(Packet packet) {
            this.packet = packet;
        }

        public Builder add(String key, String value) {
            if(this.packet.getRequiredData().contains(key)) {
                this.payload.put(key, value);
            } else {
                throw new IllegalArgumentException(String.format("'%s' is not a valid option for %s (%d)",
                        key, packet.getClass().getName(), packet.getId()));
            }
            return this;
        }

        public String build() {
            JsonObject payload = new JsonObject();
            payload.addProperty("id", this.packet.id);

            JsonArray dataArray = new JsonArray();
            this.payload.forEach((key, value) -> {
                JsonObject entryObject = new JsonObject();
                entryObject.addProperty("key", key);
                entryObject.addProperty("value", value);
                dataArray.add(entryObject);
            });

            return payload.toString();
        }

    }

}
