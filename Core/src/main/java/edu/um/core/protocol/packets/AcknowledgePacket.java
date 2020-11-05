package edu.um.core.protocol.packets;

import edu.um.core.protocol.Packets;

import java.util.HashSet;

public class AcknowledgePacket extends Packet {

    public AcknowledgePacket() {
        super(Packets.ACK, new HashSet<>());
    }
}
