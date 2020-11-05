package edu.um.maspalomas.filters;


import edu.um.core.protocol.PacketFactory;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

public class EchoFilter extends BaseFilter {

    @Override
    public NextAction handleRead(FilterChainContext ctx) {
        ctx.write(ctx.getAddress(), PacketFactory.createAcknowledgePacket().build(), null);

        return ctx.getInvokeAction();
    }

}
