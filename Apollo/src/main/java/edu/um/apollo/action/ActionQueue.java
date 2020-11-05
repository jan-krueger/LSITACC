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

    public void scheduleNow(Action action) {
        //TODO give actions ids, otherwise if we send a packet then call scheduleNow for a new action then it will
        // mark this action as successful or failure even though it meant another packet
        this.current = action;
        this.actions.addFirst(action);
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