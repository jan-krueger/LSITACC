package edu.um.core.protocol.packets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.um.core.protocol.Packets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Packet {

    private final int id;
    private final Set<String> requiredData;
    private Map<String, String> payload = new HashMap<>();

    /**
     *
     * @param id The UNIQUE id of the packet.
     * @param requiredData The data keys required for this packet.
     */
    protected Packet(Packets id, HashSet<String> requiredData) {
        this.id = id.getId();
        this.requiredData = requiredData;
    }

    public <T extends Packet> T as(Class<T> clazz) {
        return clazz.cast(this);
    }

    public int getId() {
        return id;
    }

    public Set<String> getRequiredData() {
        return requiredData;
    }

    public String get(String key) {
        return this.payload.get(key);
    }

    public Packet add(String key, String value) {
        if(this.requiredData.contains(key)) {
            this.payload.put(key, value);
        } else {
            throw new IllegalArgumentException(String.format("'%s' is not a valid option for %s (%d)",
                    key, this.getClass().getName(), this.id));
        }
        return this;
    }

    public String build() {
        return buildObject().toString();
    }

    protected JsonObject buildObject() {
        if(this.requiredData.size() != this.payload.size()) {
            throw new IllegalStateException("Data set is not complete");
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("id", this.id);
        JsonArray dataArray = new JsonArray();
        this.payload.forEach((key, value) -> {
            JsonObject entryObject = new JsonObject();
            entryObject.addProperty("key", key);
            entryObject.addProperty("value", value);
            dataArray.add(entryObject);
            payload.add("payload", dataArray);
        });

        return payload;
    }

    @Override
    public String toString() {
        return this.build();
    }
}
