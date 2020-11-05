package edu.um.maspalomas;

import edu.um.core.Person;

import java.net.InetSocketAddress;
import java.util.*;

public class PersonRegister {

    private static final Map<String, Entry> persons_by_id = new HashMap<>();
    private static final Map<String, List<Entry>> persons_by_name = new HashMap<>();

    private PersonRegister() {}

    public static boolean add(Person person, Object address) {

        //--- note: the id has to be unique
        if(persons_by_id.containsKey(person.getId())) {
            return false;
        }

        InetSocketAddress ins = (InetSocketAddress) address;
        final Entry entry = new Entry(person, new InetSocketAddress(ins.getHostName(), ins.getPort()));
        persons_by_id.put(person.getId(), entry);

        //--- the name does not need to be unique, so we need to store a list for people with the same name
        if(persons_by_name.containsKey(person.getFullNameIdentifier())) {
            persons_by_name.get(person.getFullNameIdentifier()).add(entry);
        } else {
            persons_by_name.put(person.getFullNameIdentifier(), new ArrayList<>() {{
                this.add(entry);
            }});
        }

        return true;
    }

    public static Optional<Entry> byId(String id) {
        return Optional.ofNullable(persons_by_id.get(id));
    }

    public static Optional<List<Entry>> byName(String fullNameId) {
        return Optional.ofNullable(persons_by_name.get(fullNameId));
    }

    public static List<Entry> find(String input) {
        List<Entry> persons = new ArrayList<>();

        Optional<Entry> optionalPerson = byId(input);
        if(optionalPerson.isPresent()) {
            persons.add(optionalPerson.get());
        } else {
            Optional<List<Entry>> personList = byName(input);
            personList.ifPresent(persons::addAll);
        }

        return persons;
    }

    public static class Entry {

        private final Person person;
        private final InetSocketAddress address;

        public Entry(Person person, InetSocketAddress address) {
            this.person = person;
            this.address = address;
        }

        public Person getPerson() {
            return person;
        }

        public InetSocketAddress getAddress() {
            return address;
        }
    }

}
