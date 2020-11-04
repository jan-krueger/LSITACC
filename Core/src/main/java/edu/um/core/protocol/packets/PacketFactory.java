package edu.um.core.protocol.packets;

import edu.um.core.Person;

public class PacketFactory {

    private PacketFactory() {}

    public static AcknowledgePacket createAcknowledgePacket() {
        return (AcknowledgePacket) Packets.ACK.create();
    }

    public static NotAcknowledgePacket createNotAcknowledgePacket() {
        return (NotAcknowledgePacket) Packets.NAK.create();
    }

    public static GreetServer createGreetServerPacket(Person person) {
        GreetServer packet =  (GreetServer) Packets.GREET_SERVER.create();
        packet.add("id", person.getId());
        packet.add("firstNames", String.join(" ", person.getFirstNames()));
        packet.add("lastName", person.getLastName());
        packet.add("publicKey", person.getPublicKey());
        return packet;
    }

}
