package edu.um.core;

import edu.um.core.protocol.PacketParser;
import edu.um.core.protocol.types.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.Charset;
import java.security.PrivateKey;
import java.util.List;
import java.util.Optional;

public class PacketDecoder extends MessageToMessageDecoder<ByteBuf> { // extends LineBasedFrameDecoder {

    private final PrivateKey localPrivateKey;
    private final StringBuilder fullPacket = new StringBuilder();
    private final boolean isClient;

    public PacketDecoder(PrivateKey localPrivateKey, boolean isClient) {
        this.localPrivateKey = localPrivateKey;
        this.isClient = isClient;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> list) throws Exception {

        try {

            final String message = buf.toString(Charset.defaultCharset());
            Core.LOGGER.info(message);

            final Optional<Packet> packetOptional = isClient ? PacketParser.Client.parse(message, localPrivateKey) :
                    PacketParser.Server.parse(message, localPrivateKey);

            if (packetOptional.isPresent()) {
                final Packet packet = packetOptional.get();
                list.add(packet);
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            System.out.println(ex);
        }



    }
}
