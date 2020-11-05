package edu.um.maspalomas.filters;

import edu.um.core.Person;
import edu.um.core.protocol.PacketFactory;
import edu.um.core.protocol.PacketParser;
import edu.um.core.protocol.Packets;
import edu.um.core.protocol.packets.GreetServerPacket;
import edu.um.core.protocol.packets.Packet;
import edu.um.core.protocol.packets.SendMessagePacket;
import edu.um.maspalomas.Maspalomas;
import edu.um.core.PersonRegister;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class ProtocolFilter extends ChannelInboundHandlerAdapter {

    private final Maspalomas maspalomas;

    public ProtocolFilter(Maspalomas maspalomas) {
        this.maspalomas = maspalomas;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException {

        ByteBuf byteBuf = (ByteBuf) msg;
        final String message = byteBuf.toString(StandardCharsets.UTF_8);
        Maspalomas.LOGGER.info(message);

        final Optional<Packet> packetOptional = PacketParser.parse(message);


        if (packetOptional.isPresent()) {
            final Packet packet = packetOptional.get();

            switch (Packets.byId(packet.getId()).get()) {
                case GREET_SERVER:
                    Person person = packet.as(GreetServerPacket.class).getPerson();

                    if (maspalomas.getPersonRegister().byId(person.getId()).isPresent()) {
                        ctx.writeAndFlush(PacketFactory.createNotAcknowledgePacket().build()).sync();
                        ctx.close().sync();
                        return;
                    }

                    if (maspalomas.getPersonRegister().add(person, ctx.channel())) {
                        ctx.writeAndFlush(PacketFactory.createGreetClientPacket("server-public-key").build()).sync();
                    } else {
                        throw new IllegalStateException();
                    }
                    break;

                case SEND_MESSAGE:
                    SendMessagePacket messagePacket = packet.as(SendMessagePacket.class);
                    List<PersonRegister.Entry> receivers = maspalomas.getPersonRegister().find(messagePacket.get("receiver"));

                    if (receivers.isEmpty()) {
                        ctx.writeAndFlush(PacketFactory.createExecuteActionPacket(false).build()).sync();
                        return;
                    }


                    for (PersonRegister.Entry receiver : receivers) {
                        receiver.getChannel().writeAndFlush(PacketFactory.createSendMessagePacket(receiver.getPerson(), receiver.getPerson().getId(), messagePacket.get("message")).build())
                        .sync();
                        System.out.printf("message for %s: %s\n", receiver.getPerson().getId(), messagePacket.get("message"));
                    }
                    ctx.writeAndFlush(PacketFactory.createExecuteActionPacket(true).build()).sync();
                    break;

                case REQUEST_PUBLIC_KEY:
                    List<PersonRegister.Entry> list = maspalomas.getPersonRegister().find(packet.get("identifier"));
                    if(list.isEmpty()) {
                        ctx.writeAndFlush(PacketFactory.createNotAcknowledgePacket().build()).sync();
                        return;
                    }
                    for(PersonRegister.Entry entry : list) {
                        ctx.writeAndFlush(entry.getPerson().asPersonPacket().build()).sync();
                    }
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + Packets.byId(packet.getId()));
            }

        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

}