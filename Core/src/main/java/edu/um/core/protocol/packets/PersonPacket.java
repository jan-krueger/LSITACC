package edu.um.core.protocol.packets;

import edu.um.core.Person;
import edu.um.core.RSA;
import edu.um.core.protocol.Packets;

import java.util.Arrays;
import java.util.HashSet;

public class PersonPacket extends Packet {

    public PersonPacket(Packets packet) {
        super(packet, new HashSet<>() {{
            this.add("id");
            this.add("firstNames");
            this.add("lastName");
            this.add("publicKey");
        }});
    }

    public Person getPerson() {
        Person.Builder builder = Person.builder()
                .id(this.get("id"))
                .lastName(this.get("lastName"))
                .publicKey(RSA.getPublicKey(this.get("publicKey")));

        Arrays.stream(this.get("firstNames").split(" ")).forEach(builder::firstName);
        return builder.build();

    }
}
