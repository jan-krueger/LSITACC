package edu.um.maspalomas;

import edu.um.core.Person;

import java.util.*;

public class PersonRegister {

    private static final Map<String, Person> persons_by_id = new HashMap<>();
    private static final Map<String, List<Person>> persons_by_name = new HashMap<>();

    private PersonRegister() {}

    public static boolean add(Person person) {

        //--- note: the id has to be unique
        if(persons_by_id.containsKey(person.getId())) {
            return false;
        }
        persons_by_id.put(person.getId(), person);

        //--- the name does not need to be unique, so we need to store a list for people with the same name
        if(persons_by_name.containsKey(person.getFullNameIdentifier())) {
            persons_by_name.get(person.getFullNameIdentifier()).add(person);
        } else {
            persons_by_name.put(person.getFullNameIdentifier(), new ArrayList<>() {{
                this.add(person);
            }});
        }

        return true;
    }

    public static Optional<Person> byId(String id) {
        return Optional.ofNullable(persons_by_id.get(id));
    }

    public static Optional<List<Person>> byName(String fullNameId) {
        return Optional.ofNullable(persons_by_name.get(fullNameId));
    }

}
