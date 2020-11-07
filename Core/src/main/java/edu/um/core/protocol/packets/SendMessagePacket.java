package edu.um.core.protocol.packets;

import edu.um.core.protocol.Packets;

import java.security.PublicKey;
import java.util.HashSet;

public class SendMessagePacket extends EncryptedPacket {

    public SendMessagePacket(PublicKey publicKey) {
        //TODO authToken
        super(Packets.SEND_MESSAGE, "authToken", publicKey, new HashSet<>() {{
            this.add("receiver");
            this.add("message");
        }}, new HashSet<>() {{
            this.add("message");
        }});
    }

}
