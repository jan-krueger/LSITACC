package edu.um.apollo.action;


import edu.um.apollo.Apollo;
import io.netty.channel.socket.SocketChannel;

import java.util.HashMap;
import java.util.Map;

public abstract class Action {

    private final Map<String, String> arguments = new HashMap<>();
    private final int max_trials;
    private int trials = 0;

    private boolean success = false;

    public Action(int max_trials) {
        this.max_trials = max_trials;
    }

    public boolean hasExpired() {
        return (this.trials >= max_trials);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void addArg(String argument, String value) {
        this.arguments.put(argument, value);
    }

    public String getArg(String argument) {
        return this.arguments.get("%" + argument);
    }

    protected abstract boolean execute(Apollo apollo, SocketChannel socketChannel) throws InterruptedException;

    public final boolean run(Apollo apollo, SocketChannel socketChannel) {
        this.trials++;
        if(hasExpired()) {
            return false;
        }
        try {
            this.execute(apollo, socketChannel);
        } catch (InterruptedException e) {
            e.printStackTrace(); //TODO handle
        }
        return true;
    }

}
