package edu.um.apollo.action;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class ActionQueue {

    private final Queue<Action> actions = new LinkedList<>();
    private Action next;

    public ActionQueue() { }

    public void add(Action action) {
        this.actions.add(action);
    }

    public Optional<Action> next() {

        if(next == null) {
            if(actions.isEmpty()) {
                return Optional.empty();
            }
            this.next = this.actions.peek();
            return Optional.of(this.next);
        }

        if(this.next.hasExpired()) {
            if(this.actions.isEmpty()) {
                return Optional.empty();
            }
            this.next = this.actions.poll();
            return Optional.of(this.next);
        }

        return Optional.of(this.next);
    }

}