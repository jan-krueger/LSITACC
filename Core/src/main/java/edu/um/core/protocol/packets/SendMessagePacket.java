package edu.um.core.protocol.packets;

import edu.um.core.protocol.Packets;
import edu.um.core.protocol.types.EncryptedPacket;
import edu.um.core.protocol.types.ProxiedPacket;

import java.security.PublicKey;
import java.util.HashSet;

public class SendMessagePacket extends EncryptedPacket implements ProxiedPacket {

    public SendMessagePacket(PublicKey publicKey) {
        //TODO authToken
        super(Packets.SEND_MESSAGE, "authToken", publicKey, new HashSet<>() {{
            this.add("receiver");
            this.add("message");
        }}, new HashSet<>() {{
            this.add("message");
        }});
    }


    @Override
    public String getIdentifierKey() {
        return "receiver";
    }

}
