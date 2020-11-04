package edu.um.core.protocol.packets;

import java.util.HashSet;

public class AcknowledgePacket extends Packet {

    protected AcknowledgePacket() {
        super(Packets.ACK, new HashSet<>());
    }
}
