package edu.um.core.protocol.packets;

import java.util.HashSet;

class AuthenticatedPacket extends Packet {

    private final String authToken;

    protected AuthenticatedPacket(Packets id, String authToken, HashSet<String> requiredData) {
        super(id, requiredData);
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return this.authToken;
    }

}
