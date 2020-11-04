package edu.um.core.protocol.packets;

import java.lang.reflect.InvocationTargetException;

public enum Packets {

    //--- This packet is send as a response to a received package from the client to ensure that the action
    // has been successfully executed.
    ACK(1, AcknowledgePacket.class),



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

    public Packet.Builder create() {
        try {
            return packet.getDeclaredConstructor().newInstance().create();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Failed to create packet");
    }

}
