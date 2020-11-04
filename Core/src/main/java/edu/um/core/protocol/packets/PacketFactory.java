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

    public static GreetClient createGreetClientPacket(String publicKey) {
        GreetClient packet =  Packets.GREET_CLIENT.create().as(GreetClient.class);
        packet.add("publicKey", publicKey);
        return packet;
    }

    public static GreetServer createGreetServerPacket(Person person) {
        GreetServer packet =  Packets.GREET_SERVER.create().as(GreetServer.class);
        packet.add("id", person.getId());
        packet.add("firstNames", String.join(" ", person.getFirstNames()));
        packet.add("lastName", person.getLastName());
        packet.add("publicKey", person.getPublicKey());
        return packet;
    }

}
