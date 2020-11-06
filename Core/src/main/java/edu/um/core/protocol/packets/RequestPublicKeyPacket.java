package edu.um.core.protocol.packets;

import edu.um.core.protocol.Packets;

import java.security.PublicKey;
import java.util.HashSet;

public class RequestPublicKeyPacket extends EncryptedPacket {

    public RequestPublicKeyPacket(PublicKey publicKey) {
        //TODO auth token
        super(Packets.REQUEST_PUBLIC_KEY, "auth-token", publicKey, new HashSet<>() {{
            this.add("identifier");
        }});
    }

}
