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

    public PacketDecoder(PrivateKey localPrivateKey) {
        super(8192, true, true);
        this.localPrivateKey = localPrivateKey;
    }

    @Override
    protected Packet decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        ByteBuf byteBuf = (ByteBuf) super.decode(ctx, buffer);

        final String message = byteBuf.toString(StandardCharsets.UTF_8);
        Core.LOGGER.info(message);

        final Optional<Packet> packetOptional = PacketParser.parse(message, localPrivateKey);


        if (packetOptional.isPresent()) {
            final Packet packet = packetOptional.get();
            return packet;
        }

        return null;
    }

}
