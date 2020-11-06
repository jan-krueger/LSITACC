package edu.um.maspalomas.filters;

import edu.um.core.Person;
import edu.um.core.PersonRegister;
import edu.um.core.protocol.PacketFactory;
import edu.um.core.protocol.Packets;
import edu.um.core.protocol.packets.*;
import edu.um.maspalomas.Maspalomas;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.List;

public class ProtocolFilter extends ChannelInboundHandlerAdapter {

    private final Maspalomas maspalomas;

    public ProtocolFilter(Maspalomas maspalomas) {
        this.maspalomas = maspalomas;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws InterruptedException {

        Packet packet = (Packet) obj;

        //Optional<PersonRegister.Entry> authenticatedPerson = maspalomas.getPersonRegister().byChannel(ctx.channel());;

        switch (Packets.byId(packet.getId()).get()) {
            case GREET_SERVER:
                Person person = packet.as(GreetServerPacket.class).getPerson();

                if (maspalomas.getPersonRegister().byId(person.getId()).isPresent()) {
                    ctx.writeAndFlush(PacketFactory.createNotAcknowledgePacket("A session for this" +
                            "user already exists").build()).sync();
                    //TODO either allow this connection, or update the connection and kick the old one?
                    ctx.close().sync();
                    return;
                }

                if (maspalomas.getPersonRegister().add(person, ctx.channel())) {
                    ctx.writeAndFlush(
                            PacketFactory.createGreetClientPacket(maspalomas.getServerPublicKey(), person.getPublicKey()).build()
                    ).sync();
                    ctx.writeAndFlush(PacketFactory.createAcknowledgePacket().build()).sync();
                } else {
                    throw new IllegalStateException();
                }
                break;

            case SEND_MESSAGE:
                SendMessagePacket messagePacket = packet.as(SendMessagePacket.class);
                List<PersonRegister.Entry> receivers = maspalomas.getPersonRegister().find(messagePacket.get("receiver"));

                if (receivers.isEmpty()) {
                    ctx.writeAndFlush(PacketFactory.createNotAcknowledgePacket("Unknown receiver").build()).sync();
                    return;
                }


                for (PersonRegister.Entry receiver : receivers) {
                    receiver.getChannel().writeAndFlush(
                            PacketFactory.createSendMessagePacket(receiver.getPerson(), messagePacket.get("message"),
                                    messagePacket.get("ivParameterSpec"), messagePacket.get("messageKey"),
                                    receiver.getPerson().getPublicKey()).build()
                    ).sync();
                    System.out.printf("message for %s: %s\n", receiver.getPerson().getId(), messagePacket.get("message"));
                }
                ctx.writeAndFlush(PacketFactory.createAcknowledgePacket().build()).sync();
                break;

            case REQUEST_PUBLIC_KEY:
                List<PersonRegister.Entry> list = maspalomas.getPersonRegister().find(packet.get("identifier"));
                if(list.isEmpty()) {
                    ctx.writeAndFlush(PacketFactory.createNotAcknowledgePacket("Unknown person").build()).sync();
                    return;
                }
                for(PersonRegister.Entry entry : list) {
                    ctx.writeAndFlush(entry.getPerson().asPersonPacket().build()).sync();
                }
                ctx.writeAndFlush(PacketFactory.createAcknowledgePacket().build()).sync();
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + Packets.byId(packet.getId()));
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

}