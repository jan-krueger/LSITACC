package edu.um.maspalomas.filters;

import edu.um.core.Person;
import edu.um.core.protocol.PacketFactory;
import edu.um.core.protocol.PacketParser;
import edu.um.core.protocol.Packets;
import edu.um.core.protocol.packets.*;
import edu.um.maspalomas.PersonRegister;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.List;
import java.util.Optional;

public class ProtocolFilter extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)

        ByteBuf byteBuf = (ByteBuf) msg;
        Optional<Packet> packetOptional = PacketParser.parse(byteBuf.toString());

        if (packetOptional.isPresent()) {
            final Packet packet = packetOptional.get();

            switch (Packets.byId(packet.getId()).get()) {
                case GREET_SERVER:
                    Person person = packet.as(GreetServerPacket.class).getPerson();

                    if (PersonRegister.byId(person.getId()).isPresent()) {
                        ctx.write(PacketFactory.createNotAcknowledgePacket().build());
                        return;
                    }

                    if (PersonRegister.add(person)) {
                        ctx.write(PacketFactory.createGreetClientPacket("server-public-key").build());
                    } else {
                        throw new IllegalStateException();
                    }
                    break;

                case SEND_MESSAGE:
                    SendMessagePacket messagePacket = packet.as(SendMessagePacket.class);
                    List<PersonRegister.Entry> receivers = PersonRegister.find(messagePacket.get("receiver"));

                    if (receivers.isEmpty()) {
                        //TODO return ExecutedActionPacket = false
                    }


                    for (PersonRegister.Entry receiver : receivers) {
                        //ctx.write(receiver.getAddress(), PacketFactory.createSendMessagePacket(receiver.getPerson(), receiver.getPerson().getId(), messagePacket.get("message")).build(), null);
                        System.out.printf("message for %s: %s\n", receiver.getPerson().getId(), messagePacket.get("message"));
                    }
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + Packets.byId(packet.getId()));
            }

        }


        byteBuf.release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

}