package edu.um.core.protocol;

import edu.um.core.Person;
import edu.um.core.protocol.packets.*;

public class PacketFactory {

    private PacketFactory() {}

    public static AcknowledgePacket createAcknowledgePacket() {
        return (AcknowledgePacket) Packets.ACK.create();
    }

    public static NotAcknowledgePacket createNotAcknowledgePacket() {
        return (NotAcknowledgePacket) Packets.NAK.create();
    }

    public static GreetClientPacket createGreetClientPacket(String publicKey) {
        GreetClientPacket packet =  Packets.GREET_CLIENT.create().as(GreetClientPacket.class);
        packet.add("publicKey", publicKey);
        return packet;
    }

    public static GreetServerPacket createGreetServerPacket(Person person) {
        GreetServerPacket packet =  Packets.GREET_SERVER.create().as(GreetServerPacket.class);
        packet.add("id", person.getId());
        packet.add("firstNames", String.join(" ", person.getFirstNames()));
        packet.add("lastName", person.getLastName());
        packet.add("publicKey", person.getPublicKey());
        return packet;
    }

    public static SendMessagePacket createSendMessagePacket(Person sender, String receiver, String message) {
        SendMessagePacket packet = Packets.SEND_MESSAGE.create().as(SendMessagePacket.class);
        packet.add("receiver", receiver);
        packet.add("message", message);
        return packet;
    }

}
