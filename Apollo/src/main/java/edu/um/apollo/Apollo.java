package edu.um.apollo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import edu.um.apollo.action.Action;
import edu.um.apollo.action.ActionParser;
import edu.um.apollo.action.ActionQueue;
import edu.um.core.Person;
import edu.um.core.protocol.PacketFactory;
import org.apache.commons.cli.*;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.utils.StringFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class Apollo {

    public final static Gson gson = new Gson();
    public static final Logger LOGGER = Grizzly.logger(Apollo.class);
    public static String serverPublicKey = null;

    //--- configuration
    private static Person person;
    private static ActionQueue actionQueue = new ActionQueue();

    private static String server_ip;
    private static int server_port;

    public static void main(String[] args) throws IOException,
            ExecutionException, InterruptedException, TimeoutException {

        CommandLine cmd = parseArguments(args);
        {
            final String path = cmd.getOptionValue("file");
            JsonObject data = gson.fromJson(new String(Files.readAllBytes(Path.of(path))), JsonObject.class);

            {
                JsonObject personData = data.getAsJsonObject("person");
                Person.Builder builder = Person.builder();

                String[] names = personData.get("name").getAsString().split(", ");
                builder.id(personData.get("id").getAsString());
                builder.lastName(names[0]);
                builder.publicKey(personData.getAsJsonObject("keys").get("public").getAsString());
                builder.privateKey(personData.getAsJsonObject("keys").get("private").getAsString());

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
                actionsArray.forEach(entry -> {
                    actionQueue.add(ActionParser.parse(entry.getAsString()));
                });
            }

        }

        assert person != null;

        Connection connection = null;
        // Create a FilterChain using FilterChainBuilder
        FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
        // Add TransportFilter, which is responsible
        // for reading and writing data to the connection
        filterChainBuilder.add(new TransportFilter());
        // StringFilter is responsible for Buffer <-> String conversion
        filterChainBuilder.add(new StringFilter(StandardCharsets.UTF_8));

        // ClientFilter is responsible for redirecting server responses to the standard output
        filterChainBuilder.add(new ClientFilter());

        // Create TCP transport
        final TCPNIOTransport transport = TCPNIOTransportBuilder.newInstance().build();
        transport.setProcessor(filterChainBuilder.build());

        try {
            // start the transport
            transport.start();

            // perform async. connect to the server
            Future<Connection> future = transport.connect(server_ip, server_port);
            // wait for connect operation to complete
            connection = future.get(10, TimeUnit.SECONDS);

            assert connection != null;

            //introduce us to the server
            connection.write(PacketFactory.createGreetServerPacket(Person.builder().id("5312313").firstName("Bob").lastName("Flower Tes2t").publicKey("publicKey").privateKey("privatekey").build()).build());


            System.out.println("Ready... (\"q\" to exit)");
            final BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));

            Optional<Action> action;
            do  {
                action = actionQueue.next();
                inReader.readLine();

                if(action.isPresent()) {
                    action.get().run(connection);
                }
                System.out.println("executed action " + action);
            } while (action.isPresent());
        } finally {
            // close the client connection
            if (connection != null) {
                connection.close();
            }

            // stop the transport
            transport.shutdownNow();
        }
    }

    public static Person getPerson() {
        return person;
    }

    public static String getServerPublicKey() {
        return serverPublicKey;
    }

    public static void setServerPublicKey(String serverPublicKey) {
        Apollo.serverPublicKey = serverPublicKey;
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
