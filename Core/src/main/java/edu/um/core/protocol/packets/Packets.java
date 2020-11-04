package edu.um.core.protocol.packets;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public enum Packets {

    //--- This packet is send as a response to a received package from the client to ensure that the action
    // has been successfully executed.
    ACK(1, AcknowledgePacket.class),

    GREET_SERVER(2, GreetServer.class),
    GREET_CLIENT(3, GreetClient.class),


    //--- This message is send as a response to a received package from the client to in case the action has not (!) been
    // executed.
    NAK(-1, NotAcknowledgePacket.class);

    private final int id;
    private final Class<? extends Packet> packet;

    Packets(int id, Class<? extends Packet> packet) {
        this.id = id;
        this.packet = packet;
    }

    public int getId() {
        return id;
    }

    public Packet create() {
        try {
            return packet.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            System.exit(1); // unrecoverable
        }
        throw new IllegalStateException("Failed to create packet");
    }

    public static Optional<Packet> create(int packetId) {
        Optional<Packets> optional = byId(packetId);
        if(optional.isPresent()) {
            return Optional.of(optional.get().create());
        }
        return Optional.empty();
    }

    public static Optional<Packets> byId(int packetId) {
        for(Packets p : values()) {
            if(p.getId() == packetId) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

}
