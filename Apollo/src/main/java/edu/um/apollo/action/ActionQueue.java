package edu.um.apollo.action;

import java.util.LinkedList;
import java.util.Optional;

public class ActionQueue {

    private final LinkedList<Action> actions = new LinkedList<>();
    private Action current;

    public ActionQueue() { }

    public void add(Action action) {
        this.actions.add(action);
    }

    public boolean isEmpty() {
        return actions.isEmpty();
    }


    public Action getCurrent() {
        return this.current;
    }

    public Optional<Action> next() {

        if(current == null) {
            if(actions.isEmpty()) {
                return Optional.empty();
            }
            this.current = this.actions.peek();
            return Optional.of(this.current);
        }

        if(this.current.hasExpired() || this.current.isSuccess()) {
            this.actions.poll();
            if(this.actions.isEmpty()) {
                return Optional.empty();
            }
            this.current = this.actions.peek();
            return Optional.of(this.current);
        }

        return Optional.of(this.current);
    }

}