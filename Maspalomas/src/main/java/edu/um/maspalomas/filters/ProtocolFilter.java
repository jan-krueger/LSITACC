package edu.um.maspalomas.filters;

import edu.um.core.Person;
import edu.um.core.protocol.PacketFactory;
import edu.um.core.protocol.PacketParser;
import edu.um.core.protocol.Packets;
import edu.um.core.protocol.packets.*;
import edu.um.maspalomas.PersonRegister;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

import java.util.List;
import java.util.Optional;

public class ProtocolFilter extends BaseFilter {

    @Override
    public NextAction handleRead(FilterChainContext ctx) {

        Optional<Packet> packetOptional = PacketParser.parse(ctx.getMessage());

        if(packetOptional.isPresent()) {
            final Packet packet = packetOptional.get();

            switch (Packets.byId(packet.getId()).get()) {
                case GREET_SERVER:
                    Person person = packet.as(GreetServerPacket.class).getPerson();

                    if(PersonRegister.byId(person.getId()).isPresent()) {
                        ctx.write(ctx.getAddress(), PacketFactory.createNotAcknowledgePacket().build(), null);
                        return ctx.getStopAction();
                    }

                    if(PersonRegister.add(person, ctx.getAddress())) {
                        //TODO get actual server public key
                        ctx.write(ctx.getAddress(), PacketFactory.createGreetClientPacket("server-public-key").build(), null);
                    } else {
                        throw new IllegalStateException();
                    }
                    break;

                case SEND_MESSAGE:
                    SendMessagePacket messagePacket = packet.as(SendMessagePacket.class);
                    List<PersonRegister.Entry> receivers = PersonRegister.find(messagePacket.get("receiver"));

                    if(receivers.isEmpty()) {
                        //TODO return ExecutedActionPacket = false
                    }


                    for(PersonRegister.Entry receiver : receivers) {
                        ctx.write(receiver.getAddress(), PacketFactory.createSendMessagePacket(receiver.getPerson(), receiver.getPerson().getId(), messagePacket.get("message")).build(), null);
                        System.out.printf("message for %s: %s\n", receiver.getPerson().getId(), messagePacket.get("message"));
                    }
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + Packets.byId(packet.getId()));
            }

        }
        return ctx.getInvokeAction();
    }

}
