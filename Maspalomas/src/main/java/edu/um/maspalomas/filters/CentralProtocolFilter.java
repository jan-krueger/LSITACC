package edu.um.maspalomas.filters;

import edu.um.core.Person;
import edu.um.core.PersonRegister;
import edu.um.core.protocol.PacketFactory;
import edu.um.core.protocol.Packets;
import edu.um.core.protocol.packets.*;
import edu.um.core.protocol.types.Packet;
import edu.um.core.protocol.packets.ProxyPacket;
import edu.um.maspalomas.Maspalomas;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.List;
import java.util.Optional;

public class CentralProtocolFilter extends ChannelInboundHandlerAdapter {

    private final Maspalomas maspalomas;

    public CentralProtocolFilter(Maspalomas maspalomas) {
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
                            PacketFactory.createGreetClientPacket(maspalomas.getServerPublicKey()).build()
                    ).sync();
                    ctx.writeAndFlush(PacketFactory.createAcknowledgePacket().build()).sync();
                } else {
                    throw new IllegalStateException();
                }
                break;

            case SEND_MESSAGE:
                ProxyPacket proxyPacket = packet.as(ProxyPacket.class);
                Optional<PersonRegister.Entry> receiverOptional = maspalomas.getPersonRegister().byId(proxyPacket.getReceiverId());

                if (receiverOptional.isEmpty()) {
                    ctx.writeAndFlush(PacketFactory.createNotAcknowledgePacket("Unknown receiver").build()).sync();
                    return;
                }
                PersonRegister.Entry receiver = receiverOptional.get();

                receiver.getChannel().writeAndFlush(proxyPacket.build()).sync();
                System.out.printf("message for %s\n", receiver.getPerson().getId());
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