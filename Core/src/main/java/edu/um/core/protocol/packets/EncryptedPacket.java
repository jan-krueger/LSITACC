package edu.um.core.protocol.packets;

import edu.um.core.protocol.Packets;
import io.netty.buffer.ByteBuf;

import java.security.PublicKey;
import java.util.HashSet;

public class EncryptedPacket extends AuthenticatedPacket {

    public static String ENCRYPTED_PART_DELIMITER = ",";

    private final PublicKey publicKey;

    protected EncryptedPacket(Packets id, String authToken, PublicKey publicKey, HashSet<String> requiredData) {
        super(id, authToken, requiredData);
        this.publicKey = publicKey;
    }

    @Override
    public ByteBuf build() {
        /*String[] chunks = RSA.encryptInChunks(buildObject().toString(), this.publicKey);
        return Unpooled.copiedBuffer(String.join(ENCRYPTED_PART_DELIMITER, chunks),
                CharsetUtil.UTF_8);*/
        return super.build();

    }

}
