package edu.um.core.protocol.packets;

import edu.um.core.Person;
import edu.um.core.protocol.Packets;

import java.util.Arrays;
import java.util.HashSet;

public class GreetServerPacket extends Packet {

    public GreetServerPacket() {
        super(Packets.GREET_SERVER, new HashSet<>() {{
            this.add("id");
            this.add("firstNames");
            this.add("lastName");
            this.add("publicKey");
        }});
    }

    public Person getPerson() {
        Person.Builder builder = Person.builder()
                .id(this.get("id"))
                .firstName(this.get("id"))
                .publicKey(this.get("id"))
                .privateKey(this.get("id"));

        Arrays.stream(this.get("firstNames").split(" ")).forEach(builder::firstName);
        return builder.build();

    }

}
