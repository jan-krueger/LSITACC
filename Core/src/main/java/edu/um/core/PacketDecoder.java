package edu.um.core;

import edu.um.core.protocol.PacketParser;
import edu.um.core.protocol.packets.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LineBasedFrameDecoder;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Optional;

public class PacketDecoder extends LineBasedFrameDecoder {

    private final PrivateKey localPrivateKey;
    private final StringBuilder fullPacket = new StringBuilder();
    private final boolean isClient;

    public PacketDecoder(PrivateKey localPrivateKey, boolean isClient) {
        super(8192, true, true);
        this.localPrivateKey = localPrivateKey;
        this.isClient = isClient;
    }

    @Override
    protected Packet decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {

        final String data = buffer.toString(StandardCharsets.UTF_8);
        if(!hasEndOfPacket(data)) {
            fullPacket.append(data);
            return null;
        } else {
            fullPacket.append(data);
        }

        ByteBuf byteBuf = (ByteBuf) super.decode(ctx, buffer);

        try {

            final String message = byteBuf.toString(StandardCharsets.UTF_8);
            Core.LOGGER.info(message);

            final Optional<Packet> packetOptional = PacketParser.parse(message, localPrivateKey, isClient);

            if (packetOptional.isPresent()) {
                final Packet packet = packetOptional.get();
                return packet;
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            System.out.println(ex);
        }


        return null;
    }

    private boolean hasEndOfPacket(String buffer) {
        return buffer.endsWith(Packet.PACKET_SEPARATOR);
    }

}
