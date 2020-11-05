package edu.um.apollo.action;

import edu.um.apollo.action.actions.SendMessageAction;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionParser {

    private static final Map<String, Entry> commands = new HashMap<>();
    private final static Pattern pattern = Pattern.compile("\\[([^\\[\\]]*)\\]", Pattern.MULTILINE | Pattern.DOTALL);

    static {
        commands.put("SEND", new Entry(SendMessageAction.class, "%receiver", "%message"));
    }

    private ActionParser() { }

    public static Action parse(String line) {
        String[] split = line.split(" ");
        final String key = split[0].toUpperCase();

        Entry entry = commands.get(key);
        Action action = entry.create();

        final Matcher matcher = pattern.matcher(line.substring(key.length() + 1));
        int i = 0;
        while (matcher.find()) {
            System.out.println("Full match: " + matcher.group(0));
            action.addArg(entry.arguments[i], matcher.group(1));
            i++;
        }

        return action;
    }

    private static class Entry {

        private String[] arguments;
        private Class<? extends Action> clazz;

        public Entry(Class<? extends Action> clazz, String... arguments) {
            this.arguments = arguments;
            this.clazz = clazz;
        }

        public String[] getArguments() {
            return arguments;
        }


        public Action create() {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
