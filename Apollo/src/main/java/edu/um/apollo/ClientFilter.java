package edu.um.apollo;

import edu.um.core.protocol.PacketParser;
import edu.um.core.protocol.Packets;
import edu.um.core.protocol.packets.*;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

import java.util.Optional;

public class ClientFilter extends BaseFilter {

    @Override
    public NextAction handleRead(FilterChainContext ctx) {
        System.out.println("Server response: " + ctx.getMessage());
        Optional<Packet> packetOptional = PacketParser.parse(ctx.getMessage());

        if(packetOptional.isPresent()) {
            final Packet packet = packetOptional.get();

            switch (Packets.byId(packet.getId()).get()) {
                case GREET_CLIENT:
                    Apollo.setServerPublicKey(packet.get("publicKey"));
                    Apollo.LOGGER.info("Server is greeting you");
                    break;


                case SEND_MESSAGE:
                    System.out.println("RECEIVED message: " + packet.toString());
                    break;

                case ACK:
                    break;

                case NAK: break;
                default:
                    throw new IllegalStateException("Unexpected value: " + Packets.byId(packet.getId()));
            }

        }
        return ctx.getInvokeAction();
    }

}
