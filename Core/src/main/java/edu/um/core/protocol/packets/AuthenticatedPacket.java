package edu.um.core.protocol.packets;

import com.google.gson.JsonObject;
import edu.um.core.protocol.Packets;

import java.util.HashSet;

public class AuthenticatedPacket extends Packet {

    private final String authToken;

    public AuthenticatedPacket(Packets id, String authToken, HashSet<String> requiredData) {
        super(id, requiredData);
        this.authToken = authToken;
    }


    public String getAuthToken() {
        return this.authToken;
    }

    @Override
    protected JsonObject buildObject() {
        JsonObject object = super.buildObject();
        object.addProperty("authToken", this.authToken);
        return object;
    }

}
