package edu.um.core.protocol;

import edu.um.core.protocol.packets.*;

import java.lang.reflect.InvocationTargetException;
import java.security.PublicKey;
import java.util.Optional;

public enum Packets {

    //--- This packet is send as a response to a received package from the client to ensure that the action
    // has been successfully executed.
    ACK(1, AcknowledgePacket.class, false),

    GREET_SERVER(2, GreetServerPacket.class, false),
    GREET_CLIENT(3, GreetClientPacket.class, false),

    SEND_MESSAGE(4, SendMessagePacket.class, true),

    REQUEST_PUBLIC_KEY(6, RequestPublicKeyPacket.class, false),

    SEND_PERSON(7, SendPersonPacket.class, false),

    //--- This message is send as a response to a received package from the client to in case the action has not (!) been
    // executed.
    NAK(-1, NotAcknowledgePacket.class, false);

    private final int id;
    private final Class<? extends Packet> packet;
    private final boolean proxy;

    Packets(int id, Class<? extends Packet> packet, boolean proxy) {
        this.id = id;
        this.packet = packet;
        this.proxy = proxy;
    }

    public int getId() {
        return id;
    }

    public Class<? extends Packet> getClazz() {
        return packet;
    }

    public boolean isProxy() {
        return proxy;
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

    public EncryptedPacket createEncryptedPacket(PublicKey publicKey) {
        try {
            return (EncryptedPacket) packet.getDeclaredConstructor(PublicKey.class).newInstance(publicKey);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            System.exit(1); // unrecoverable
        }
        throw new IllegalStateException("Failed to create encrypted packet");
    }

    public static Optional<Packet> create(int packetId) {
        Optional<Packets> optional = byId(packetId);
        if(optional.isPresent()) {
            Packets packet = optional.get();

            if(EncryptedPacket.class.isAssignableFrom(packet.getClazz())) {
                return Optional.of(packet.createEncryptedPacket(null)); //TODO
            }

            return Optional.of(packet.create());
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
