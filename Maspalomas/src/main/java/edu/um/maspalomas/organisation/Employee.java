package edu.um.maspalomas.organisation;

import edu.um.core.Person;

import java.util.HashSet;

public class Employee {

    private final Person person;
    private final HashSet<String> roles;

    public Employee(Person person, HashSet<String> roles) {
        this.person = person;
        this.roles = roles;
    }

    public Person getPerson() {
        return person;
    }

    public HashSet<String> getRoles() {
        return roles;
    }

}
