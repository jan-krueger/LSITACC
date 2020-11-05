package edu.um.core.protocol.packets;

import edu.um.core.protocol.Packets;

import java.util.HashSet;

public class RequestPublicKeyPacket extends Packet {

    public RequestPublicKeyPacket() {
        super(Packets.REQUEST_PUBLIC_KEY, new HashSet<>() {{
            this.add("identifier");
        }});
    }

}
