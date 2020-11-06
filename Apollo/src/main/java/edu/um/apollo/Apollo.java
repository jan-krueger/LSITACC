package edu.um.apollo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.um.apollo.action.ActionParser;
import edu.um.apollo.action.ActionQueue;
import edu.um.apollo.action.actions.SendPacketAction;
import edu.um.core.*;
import edu.um.core.protocol.PacketFactory;
import edu.um.core.security.RSA;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PublicKey;
import java.util.logging.Logger;

public class Apollo {

    public final static Gson gson = new Gson();
    public static final Logger LOGGER = Logger.getLogger(Apollo.class.getSimpleName());

    public static void main(String[] args) throws IOException, InterruptedException {

        CommandLine cmd = parseArguments(args);

        ActionQueue actionQueue = new ActionQueue();
        Person person;
        String server_ip;
        int server_port;

        final String path = cmd.getOptionValue("file");
        JsonObject data = gson.fromJson(new String(Files.readAllBytes(Path.of(path))), JsonObject.class);

        {
            JsonObject personData = data.getAsJsonObject("person");
            Person.Builder builder = Person.builder();

            String[] names = personData.get("name").getAsString().split(", ");
            builder.id(personData.get("id").getAsString());
            builder.lastName(names[0]);
            builder.publicKey(RSA.getPublicKey(personData.getAsJsonObject("keys").get("public").getAsString()));
            builder.privateKey(RSA.getPrivateKey(personData.getAsJsonObject("keys").get("private").getAsString()));

            for (int i = 1; i < names.length; i++) {
                builder.firstName(names[i]);
            }

            person = builder.build();
        }

        {
            JsonObject serverData = data.getAsJsonObject("server");
            server_ip = serverData.get("ip").getAsString();
            server_port = serverData.get("port").getAsInt();
        }

        {
            JsonArray actionsArray = data.getAsJsonArray("actions");
            actionQueue.add(new SendPacketAction(PacketFactory.createGreetServerPacket(person)));

            actionsArray.forEach(entry -> {
                actionQueue.add(ActionParser.parse(entry.getAsString()));
            });
        }

        new Apollo(person, actionQueue, server_ip, server_port).run();

    }

    //--- configuration
    public PublicKey serverPublicKey = null;
    private Person person;
    private ActionQueue actionQueue;

    private final PersonRegister personRegister = new PersonRegister();
    private String server_ip;
    private int server_port;

    private SocketChannel channel;

    public Apollo(Person person, ActionQueue actionQueue, String server_ip, int server_port) {
        this.person = person;
        this.actionQueue = actionQueue;
        this.server_ip = server_ip;
        this.server_port = server_port;
    }

    public PersonRegister getPersonRegister() {
        return personRegister;
    }

    public void run() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(server_ip, server_port))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel sc)  {
                        sc.pipeline().addLast(new PacketDecoder(person.getPrivateKey()));
                        sc.pipeline().addLast(new ClientFilter(Apollo.this));
                    }
                });
                ChannelFuture f = b.connect().sync();
            this.channel = (SocketChannel) f.channel();

            Thread.sleep(2500);
            advanceActionQueue();

            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public void advanceActionQueue() {
        actionQueue.next().ifPresent(action -> {
            action.run(Apollo.this, this.channel);
        });
    }

    public Person getPerson() {
        return person;
    }

    public ActionQueue getActionQueue() {
        return this.actionQueue;
    }

    public PublicKey getServerPublicKey() {
        return serverPublicKey;
    }

    public void setServerPublicKey(PublicKey serverPublicKey) {
        this.serverPublicKey = serverPublicKey;
    }

    private static CommandLine parseArguments(String[] args) {
        Options options = new Options();
        options.addRequiredOption("f", "file", true, "The path to the configuration file");

        CommandLineParser commandLineParser = new DefaultParser();
        try {
            return commandLineParser.parse(options, args);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "apollo", options);
            System.exit(1);
        }
        return null;
    }

}
