package edu.um.core.protocol.packets;

import edu.um.core.protocol.Packets;

import java.util.HashSet;

public class NotAcknowledgePacket extends Packet {

    public NotAcknowledgePacket() {
        super(Packets.NAK, new HashSet<>() {{
            this.add("message");
        }});
    }
}
