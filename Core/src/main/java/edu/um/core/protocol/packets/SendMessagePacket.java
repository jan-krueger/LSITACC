package edu.um.core.protocol.packets;

import edu.um.core.protocol.Packets;

import java.util.HashSet;

public class SendMessagePacket extends EncryptedPacket {

    public SendMessagePacket() {
        //TODO authToken and publicKey
        super(Packets.SEND_MESSAGE, "authToken", "publicKey", new HashSet<>() {{
            this.add("receiver");
            this.add("message");
        }});
    }

}
