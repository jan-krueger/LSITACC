package edu.um.maspalomas.filters;

import edu.um.core.Person;
import edu.um.core.protocol.packets.*;
import edu.um.maspalomas.PersonRegister;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

import java.util.Optional;

public class ProtocolFilter extends BaseFilter {

    @Override
    public NextAction handleRead(FilterChainContext ctx) {

        Optional<Packet> packetOptional = PacketParser.parse(ctx.getMessage());

        if(packetOptional.isPresent()) {
            final Packet packet = packetOptional.get();

            switch (Packets.byId(packet.getId()).get()) {
                case GREET_SERVER:
                    Person person = packet.as(GreetServer.class).getPerson();

                    if(PersonRegister.byId(person.getId()).isPresent()) {
                        ctx.write(ctx.getAddress(), PacketFactory.createNotAcknowledgePacket().build(), null);
                        return ctx.getStopAction();
                    }

                    if(PersonRegister.add(person)) {
                        //TODO get actual server public key
                        ctx.write(ctx.getAddress(), PacketFactory.createGreetClientPacket("server-public-key").build(), null);
                    } else {
                        throw new IllegalStateException();
                    }
                    break;

                case ACK:
                    break;

                case NAK:
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + Packets.byId(packet.getId()));
            }

        }
        return ctx.getInvokeAction();
    }

}
