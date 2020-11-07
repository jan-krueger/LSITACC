package edu.um.core.protocol.packets;

import com.google.gson.JsonObject;
import edu.um.core.protocol.Packets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class ProxyPacket extends Packet {

    private final JsonObject rawData;

    public ProxyPacket(Packets id, JsonObject rawData) {
        super(id, new HashSet<>());
        this.rawData = rawData;
    }

    public String getReceiver() {
        AtomicReference<String> receiver = new AtomicReference<>();
        rawData.getAsJsonArray("payload").forEach(e -> {
            JsonObject entry = (JsonObject) e;
            if(entry.get("key").getAsString().equals("receiver")) {
                receiver.set(entry.get("value").getAsString());
            }
        });
        return receiver.get();
    }

    @Override
    public Set<String> getRequiredData() {
        throw new RuntimeException("Unsupported for proxy packets.");
    }

    @Override
    public String get(String key) {
        throw new RuntimeException("Unsupported for proxy packets.");
    }

    @Override
    public Packet add(String key, String value) {
        throw new RuntimeException("Unsupported for proxy packets.");
    }

    @Override
    public ByteBuf build() {
        return Unpooled.copiedBuffer(this.buildObject().toString() + Packet.PACKET_SEPARATOR, CharsetUtil.UTF_8);
    }

    @Override
    protected JsonObject buildObject() {
        return this.rawData;
    }

    @Override
    public String toString() {
        return rawData.toString();
    }
}
