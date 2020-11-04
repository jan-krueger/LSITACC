package edu.um.core.protocol.packets;

import java.util.HashSet;

public class GreetClient extends AuthenticatedPacket {

    protected GreetClient(Packets id, String authToken, HashSet<String> requiredData) {
        super(id, authToken, requiredData);
    }

}
