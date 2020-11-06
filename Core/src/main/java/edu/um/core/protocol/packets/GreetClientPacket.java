package edu.um.core.protocol.packets;

import edu.um.core.protocol.Packets;

import java.security.PublicKey;
import java.util.HashSet;

public class GreetClientPacket extends EncryptedPacket {

    public GreetClientPacket(PublicKey publicKey) {
        //TODO auth token
        super(Packets.GREET_CLIENT, null, publicKey, new HashSet<>() {{
            this.add("publicKey");
        }});
    }

}
