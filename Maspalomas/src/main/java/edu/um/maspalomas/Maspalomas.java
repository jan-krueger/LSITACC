package edu.um.maspalomas;

import edu.um.maspalomas.filters.DecryptFilter;
import edu.um.maspalomas.filters.EchoFilter;
import edu.um.maspalomas.filters.ProtocolFilter;
import org.apache.commons.cli.*;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.utils.StringFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class Maspalomas {

    private static final Logger logger = Logger.getLogger(Maspalomas.class.getSimpleName());

    public static final String HOST = "localhost";


    public static void main(String[] args) throws IOException {

        CommandLine arguments = parseArguments(args);
        final int port = Integer.parseUnsignedInt(arguments.getOptionValue("port"));

        FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();

        filterChainBuilder.add(new TransportFilter());
        filterChainBuilder.add(new StringFilter());
        filterChainBuilder.add(new ProtocolFilter());
        //filterChainBuilder.add(new DecryptFilter());
        filterChainBuilder.add(new EchoFilter());

        // Create TCP transport
        final TCPNIOTransport transport = TCPNIOTransportBuilder.newInstance().build();

        transport.setProcessor(filterChainBuilder.build());
        try {
            transport.bind(HOST, port);
            logger.info(String.format("Starting server %s:%d", HOST, port));
            transport.start();

            logger.info("Press any key to stop the server...");
            System.in.read();
        } finally {
            logger.info("Stopping transport...");
            transport.shutdownNow();

            logger.info("Stopped transport...");
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

}
