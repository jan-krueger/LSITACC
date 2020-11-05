package edu.um.core.protocol.packets;

import edu.um.core.protocol.Packets;

public class GreetServerPacket extends PersonPacket {

    public GreetServerPacket() {
        super(Packets.GREET_SERVER);
    }

}
