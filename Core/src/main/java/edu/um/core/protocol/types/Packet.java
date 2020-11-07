package edu.um.core.protocol.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.um.core.protocol.Packets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class that represents the most basic package. It can represent a specified payload but NOTHING is encrypted or
 * protected in any way if it is send.
 */
public class Packet {

    public static final String PACKET_SEPARATOR = "\n";

    private final Packets packets;
    private final Set<String> requiredData;
    private Map<String, String> payload = new HashMap<>();

    /**
     *
     * @param id The UNIQUE id of the packet.
     * @param requiredData The data keys required for this packet.
     */
    protected Packet(Packets id, HashSet<String> requiredData) {
        this.packets = id;
        this.requiredData = requiredData;
    }

    public <T extends Packet> T as(Class<T> clazz) {
        return clazz.cast(this);
    }

    public int getId() {
        return packets.getId();
    }

    public Packets getType() {
        return packets;
    }

    public String get(String key) {
        return this.payload.get(key);
    }

    public Packet add(String key, String value) {
        if(this.requiredData.contains(key)) {
            this.payload.put(key, value);
        } else {
            throw new IllegalArgumentException(String.format("'%s' is not a valid option for %s (%d)",
                    key, this.getClass().getName(), this.getId()));
        }
        return this;
    }

    public ByteBuf build() {
        return Unpooled.copiedBuffer(buildObject().toString() + PACKET_SEPARATOR, CharsetUtil.UTF_8);
    }

    protected JsonObject buildObject() {
        JsonObject payload = new JsonObject();
        payload.addProperty("id", this.getId());
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
        return this.buildObject().toString();
    }
}
