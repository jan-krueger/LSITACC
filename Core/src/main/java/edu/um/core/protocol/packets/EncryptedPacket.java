package edu.um.core.protocol.packets;

import com.google.gson.JsonObject;
import edu.um.core.protocol.Packets;
import io.netty.buffer.ByteBuf;

import java.util.HashSet;

public class EncryptedPacket extends AuthenticatedPacket {

    private final String publicKey;

    protected EncryptedPacket(Packets id, String authToken, String publicKey, HashSet<String> requiredData) {
        super(id, authToken, requiredData);
        this.publicKey = publicKey;
    }

    public ByteBuf build() {

        JsonObject object = super.buildObject();
        //TODO add encryption
        // We have to decide whether the server is responsible for encryption or the client... server is simpler but stupid...
        // client-side is a bit more annoying because then we have to implement that the client exchanges public keys for the recipients

        return super.build();
    }

}
