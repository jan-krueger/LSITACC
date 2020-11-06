package edu.um.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class PacketEncoder extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext channelHandlerContext, Object o, ChannelPromise channelPromise) throws Exception {

        ByteBuf byteBuf = (ByteBuf) o;
        System.out.println(o);
        channelHandlerContext.writeAndFlush(o).sync();

    }
}
