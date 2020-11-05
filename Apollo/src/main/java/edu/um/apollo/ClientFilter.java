package edu.um.apollo;

import edu.um.core.protocol.PacketFactory;
import edu.um.core.protocol.PacketParser;
import edu.um.core.protocol.Packets;
import edu.um.core.protocol.packets.Packet;
import edu.um.core.protocol.packets.SendPersonPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ClientFilter extends SimpleChannelInboundHandler<ByteBuf> {

    private final Apollo apollo;

    public ClientFilter(Apollo apollo) {
        this.apollo = apollo;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(PacketFactory.createGreetServerPacket(apollo.getPerson()).build());
    }


    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) {
        final String message = in.toString(StandardCharsets.UTF_8);
        Apollo.LOGGER.info(message);
        Optional<Packet> packetOptional = PacketParser.parse(message);

        if(packetOptional.isPresent()) {
            final Packet packet = packetOptional.get();

            switch (Packets.byId(packet.getId()).get()) {
                case GREET_CLIENT:
                    apollo.setServerPublicKey(packet.get("publicKey"));
                    Apollo.LOGGER.info("Server is greeting you");
                    break;

                case EXECUTED_ACTION:
                    apollo.getActionQueue().getCurrent().setSuccess(Boolean.parseBoolean(packet.get("success")));
                    break;

                case SEND_MESSAGE:
                    System.out.println("RECEIVED message: " + packet.toString());
                    break;


                case SEND_PERSON:
                    SendPersonPacket sendPersonPacket = packet.as(SendPersonPacket.class);
                    apollo.getPersonRegister().add(sendPersonPacket.getPerson());
                    break;

                case ACK:
                    break;

                case NAK: break;
                default:
                    throw new IllegalStateException("Unexpected value: " + Packets.byId(packet.getId()));
            }

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
