package edu.um.core.protocol.packets;

import com.google.gson.JsonObject;
import edu.um.core.protocol.Packets;
import edu.um.core.protocol.types.Packet;
import edu.um.core.protocol.types.ProxiedPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A proxy packet is used by the server if it has to simply forward the data unprocessed to the receiver.
 */
public class ProxyPacket extends Packet {

    private final JsonObject rawData;
    private final ProxiedPacket proxiedPacket;

    public ProxyPacket(Packets id, ProxiedPacket proxiedPacket, JsonObject rawData) {
        super(id, new HashSet<>());
        this.rawData = rawData;
        this.proxiedPacket = proxiedPacket;
    }

    public String getReceiverId() {
        AtomicReference<String> receiver = new AtomicReference<>();
        rawData.getAsJsonArray("payload").forEach(e -> {
            JsonObject entry = (JsonObject) e;
            if(entry.get("key").getAsString().equals(proxiedPacket.getIdentifierKey())) {
                receiver.set(entry.get("value").getAsString());
            }
        });
        return receiver.get();
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
