package edu.um.core.protocol.packets;

import java.util.HashSet;

public class GreetClient extends Packet {

    protected GreetClient() {
        super(Packets.GREET_CLIENT, new HashSet<>() {{
            this.add("publicKey");
        }});
    }

}
