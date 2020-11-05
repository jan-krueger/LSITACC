package edu.um.apollo.action;

import org.glassfish.grizzly.Connection;

import java.util.HashMap;
import java.util.Map;

public abstract class Action {

    private final int max_trials;

    private final Map<String, String> arguments = new HashMap<>();

    private int trials = 0;

    public Action(int max_trials) {
        this.max_trials = max_trials;
    }

    public boolean hasExpired() {
        return (this.trials >= max_trials);
    }

    public void addArg(String argument, String value) {
        this.arguments.put(argument, value);
    }

    public String getArg(String argument) {
        return this.arguments.get("%" + argument);
    }

    protected abstract boolean execute(Connection connection);

    public final boolean run(Connection connection) {
        this.trials++;
        if(hasExpired()) {
            return false;
        }
        this.execute(connection);
        return true;
    }

    public static interface Callback {
        void execute(Connection connection);
    }

}
