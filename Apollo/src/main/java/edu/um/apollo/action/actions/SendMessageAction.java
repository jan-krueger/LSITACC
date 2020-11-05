package edu.um.apollo.action.actions;

import edu.um.apollo.Apollo;
import edu.um.apollo.action.Action;
import edu.um.core.protocol.PacketFactory;
import org.glassfish.grizzly.Connection;

public class SendMessageAction extends Action {

    public SendMessageAction() {
        //TODO deal with max trials
        super(5);
    }


    @Override
    protected boolean execute(Connection connection) {
        connection.write(PacketFactory.createSendMessagePacket(Apollo.getPerson(), getArg("receiver"),
                getArg("message")).build());
        return true;
    }

}
