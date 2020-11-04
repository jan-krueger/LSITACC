package edu.um.core;

import java.util.ArrayList;
import java.util.List;

public class Person {

    private final String id;
    private final String lastName;
    private final String[] firstNames;
    private final String privateKey;
    private final String publicKey;

    private Person(String id, String lastName, String[] firstNames, String privateKey, String publicKey) {
        this.id = id;
        this.lastName = lastName;
        this.firstNames = firstNames;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String[] getFirstNames() {
        return firstNames;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String id;
        private String lastName;
        private List<String> firstNames = new ArrayList<>();
        private String privateKey;
        private String publicKey;

        private Builder() {}

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstNames.add(firstName);
            return this;
        }

        public Builder privateKey(String privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        public Builder publicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public Person build() {
            return new Person(id, lastName, firstNames.toArray(new String[0]), privateKey, publicKey);
        }


    }
}
