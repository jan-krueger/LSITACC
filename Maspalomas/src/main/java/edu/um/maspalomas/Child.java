package edu.um.maspalomas;

import edu.um.core.PacketDecoder;
import edu.um.core.PersonRegister;
import edu.um.core.security.RSA;
import edu.um.maspalomas.filters.CentralProtocolFilter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;

import java.io.IOException;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Logger;

public class Child implements Runnable {

    public static final Logger LOGGER = Logger.getLogger(Child.class.getSimpleName());

    private final String host;
    private final int port;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    private final PersonRegister personRegister = new PersonRegister();

    public Child(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            privateKey = RSA.getPrivateKey(Path.of("./test-certificates/server.pri"));
            publicKey = RSA.getPublicKey(Path.of("./test-certificates/server.pub"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PersonRegister getPersonRegister() {
        return personRegister;
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new LineBasedFrameDecoder(8192, true, true));
                            ch.pipeline().addLast(new PacketDecoder(privateKey, false));
                            ch.pipeline().addLast(new CentralProtocolFilter(Maspalomas.this));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            try {
                ChannelFuture f = b.bind(host, port).sync();
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public PublicKey getServerPublicKey() {
        return this.publicKey;
    }


}
