package edu.um.core.protocol.packets;

import edu.um.core.protocol.Packets;

public class SendPersonPacket extends PersonPacket {

    public SendPersonPacket() {
        super(Packets.SEND_PERSON);
    }

}
