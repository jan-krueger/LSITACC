package edu.um.core;

import edu.um.core.protocol.packets.SendPersonPacket;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Person {

    private final String id;
    private final String fullNameIdentifier;

    private final String lastName;
    private final String[] firstNames;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public Person(String id, String fullNameIdentifier, String lastName, String[] firstNames, PrivateKey privateKey, PublicKey publicKey) {
        this.id = id;
        this.fullNameIdentifier = fullNameIdentifier;
        this.lastName = lastName;
        this.firstNames = firstNames;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String getId() {
        return id;
    }

    public String getFullNameIdentifier() {
        return this.fullNameIdentifier;
    }

    public String getLastName() {
        return lastName;
    }

    public String[] getFirstNames() {
        return firstNames;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public SendPersonPacket asPersonPacket() {
        SendPersonPacket packet = new SendPersonPacket();
        packet.add("id", this.id);
        packet.add("firstNames", String.join(" ", this.firstNames));
        packet.add("lastName", this.lastName);
        packet.add("publicKey", Base64.getEncoder().encodeToString(this.publicKey.getEncoded()));
        return packet;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String id;
        private String lastName;
        private List<String> firstNames = new ArrayList<>();
        private PrivateKey privateKey;
        private PublicKey publicKey;

        private Builder() {}

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName.toUpperCase();
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstNames.add(firstName.toUpperCase());
            return this;
        }

        public Builder privateKey(PrivateKey privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        public Builder publicKey(PublicKey publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public Person build() {
            return new Person(
                    id, String.format("%s, %s", this.lastName, String.join(", ", firstNames)), lastName,
                    firstNames.toArray(new String[0]), privateKey, publicKey
            );
        }


    }
}
