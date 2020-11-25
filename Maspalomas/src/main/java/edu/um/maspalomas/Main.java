package edu.um.maspalomas;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import edu.um.core.Person;
import edu.um.maspalomas.organisation.Employee;
import edu.um.maspalomas.organisation.Organisation;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public final static Gson GSON = new Gson();
    private final static ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws Exception {

        CommandLine arguments = parseArguments(args);
        final String confPath = arguments.getOptionValue("config");
        Main.Configuration configuration = parseConfig(confPath);

        executorService.submit(() -> new Maspalomas("localhost", configuration.port));

        System.out.println("hey");
    }

    public static Configuration parseConfig(String path) throws IOException {
        JsonObject root = GSON.fromJson(Files.readString(Paths.get(path)), JsonObject.class);
        Configuration configuration = new Configuration();
        configuration.port = root.getAsJsonObject("server").get("port").getAsInt();

        for(JsonElement e : root.getAsJsonArray("organizations")) {
            JsonObject orgObject = e.getAsJsonObject();

            HashSet<String> roles = new HashSet<>();
            for (JsonElement role : orgObject.getAsJsonArray("roles")) {
                roles.add(role.getAsString());
            }


            Organisation organisation = new Organisation(orgObject.get("name").getAsString(), roles);
            for(JsonElement element : orgObject.getAsJsonArray("employees")) {
                JsonObject employeeObject = element.getAsJsonObject();
                Person person = Person.builder().id(employeeObject.get("id").getAsString()).build();
                HashSet<String> employeesRoles = new HashSet<>();
                for(JsonElement role : employeeObject.getAsJsonArray("roles")) {
                    if(roles.contains(role.getAsString())) {
                        employeesRoles.add(role.getAsString());
                    } else {
                        throw new IllegalArgumentException(String.format("%s does not have '%s'. Valid roles: %s",
                                organisation.getName(), role.getAsString(), String.join(",", roles)));
                    }
                }

                organisation.add(new Employee(person, employeesRoles));
            }

            configuration.organisations.add(organisation);

        }

        return configuration;

    }

    private static class Configuration {
        public int port;
        public List<Organisation> organisations = new ArrayList<>();
    }

    private static CommandLine parseArguments(String[] args) {
        Options options = new Options();
        options.addRequiredOption("c", "config", true, "The configuration for this service");

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
