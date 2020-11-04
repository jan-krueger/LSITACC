package edu.um.core.protocol.packets;

import java.util.HashSet;

public class NotAcknowledgePacket extends Packet {

    protected NotAcknowledgePacket() {
        super(Packets.NAK, new HashSet<>());
    }
}
