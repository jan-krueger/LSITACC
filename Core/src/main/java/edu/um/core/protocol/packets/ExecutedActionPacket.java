package edu.um.core.protocol.packets;

import edu.um.core.protocol.Packets;

import java.util.HashSet;

public class ExecutedActionPacket extends AuthenticatedPacket {

    public ExecutedActionPacket() {
        //TODO authToken
        super(Packets.EXECUTED_ACTION, "authtoken-todo", new HashSet<>() {{
            this.add("success");
            //TODO maybe add a local task id so the server can handle multiple tasks at once
        }});
    }

}
