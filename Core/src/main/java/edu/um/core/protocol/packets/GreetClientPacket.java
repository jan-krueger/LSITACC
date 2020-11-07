package edu.um.core.protocol.packets;

import edu.um.core.protocol.Packets;
import edu.um.core.protocol.types.Packet;

import java.util.HashSet;

public class GreetClientPacket extends Packet {

    public GreetClientPacket() {
        super(Packets.GREET_CLIENT, new HashSet<>() {{
            this.add("publicKey");
        }});
    }

}
