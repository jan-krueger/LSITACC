package edu.um.apollo.action.actions;

import edu.um.apollo.Apollo;
import edu.um.apollo.action.Action;
import edu.um.core.protocol.PacketFactory;
import io.netty.channel.socket.SocketChannel;

public class SendMessageAction extends Action {

    public SendMessageAction() {
        //TODO deal with max trials
        super(5);
    }


    @Override
    protected boolean execute(Apollo apollo, SocketChannel socketChannel) throws InterruptedException {
        socketChannel.writeAndFlush(PacketFactory.createSendMessagePacket(apollo.getPerson(), getArg("receiver"),
                getArg("message")).build()).sync();
        //@TODO fix
        return true;
    }

}
