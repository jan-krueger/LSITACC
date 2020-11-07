package edu.um.maspalomas;

import edu.um.core.PacketDecoder;
import edu.um.core.PersonRegister;
import edu.um.core.security.RSA;
import edu.um.maspalomas.filters.ProtocolFilter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Logger;

public class Maspalomas {

    public static final Logger LOGGER = Logger.getLogger(Maspalomas.class.getSimpleName());

    public static void main(String[] args) throws Exception {

        CommandLine arguments = parseArguments(args);
        final int port = Integer.parseUnsignedInt(arguments.getOptionValue("port"));

        new Maspalomas("localhost", port).run();
    }

    private final String host;
    private final int port;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    private final PersonRegister personRegister = new PersonRegister();

    private final EventExecutorGroup eventExecutors = new DefaultEventExecutorGroup(4);

    public Maspalomas(String host, int port) {
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

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new PacketDecoder(privateKey, false));
                            ch.pipeline().addLast(new ProtocolFilter(Maspalomas.this));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private static CommandLine parseArguments(String[] args) {
        Options options = new Options();
        options.addRequiredOption("p", "port", true, "The port the server will listen to");

        CommandLineParser commandLineParser = new DefaultParser();
        try {
            return commandLineParser.parse(options, args);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "maspalomas", options);
            System.exit(1);
        }
        return null;
    }

    public PublicKey getServerPublicKey() {
        return this.publicKey;
    }

}
