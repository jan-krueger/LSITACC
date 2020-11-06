package edu.um.apollo.action.actions;

import edu.um.apollo.Apollo;
import edu.um.apollo.action.Action;
import edu.um.core.protocol.packets.Packet;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.SocketChannel;

public class SendPacketAction extends Action {

    private final Packet packet;

    public SendPacketAction(Packet packet) {
        super(5);
        this.packet = packet;
    }

    @Override
    protected boolean execute(Apollo apollo, SocketChannel socketChannel) throws InterruptedException {
        socketChannel.writeAndFlush(packet.build()).sync().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                System.out.println("one");
            }
        });
        return true;
    }

}

