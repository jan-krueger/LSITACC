package edu.um.maspalomas;

import edu.um.core.Person;

import java.util.*;

public class PersonRegister {

    private final Map<String, Person> persons_by_id = new HashMap<>();
    private final Map<String, List<Person>> persons_by_name = new HashMap<>();

    public PersonRegister() {}

    public boolean add(Person person) {

        //--- note: the id has to be unique
        if(this.persons_by_id.containsKey(person.getId())) {
            return false;
        }
        this.persons_by_id.put(person.getId(), person);

        //--- the name does not need to be unique, so we need to store a list for people with the same name
        if(this.persons_by_name.containsKey(person.getFullNameIdentifier())) {
            this.persons_by_name.get(person.getFullNameIdentifier()).add(person);
        } else {
            this.persons_by_name.put(person.getFullNameIdentifier(), new ArrayList<>() {{
                this.add(person);
            }});
        }

        return true;
    }

    public Optional<Person> byId(String id) {
        return Optional.of(this.persons_by_id.get(id));
    }

    public Optional<List<Person>> byName(String fullNameId) {
        return Optional.of(this.persons_by_name.get(fullNameId));
    }

}
