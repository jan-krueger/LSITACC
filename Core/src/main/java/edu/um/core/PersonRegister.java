package edu.um.core;

import io.netty.channel.Channel;

import java.util.*;

public class PersonRegister {

    private final Map<String, Entry> persons_by_id = new HashMap<>();
    private final Map<String, List<Entry>> persons_by_name = new HashMap<>();

    public PersonRegister() {}

    public boolean add(Person person) {
        return add(person, null);
    }

    public boolean add(Person person, Channel channel) {

        //--- note: the id has to be unique
        if(persons_by_id.containsKey(person.getId())) {
            return false;
        }

        final Entry entry = new Entry(person, channel);
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

    public Optional<Entry> byId(String id) {
        return Optional.ofNullable(persons_by_id.get(id));
    }

    public Optional<List<Entry>> byName(String fullNameId) {
        return Optional.ofNullable(persons_by_name.get(fullNameId));
    }

    public List<Entry> find(String input) {
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
        private final Channel channel;

        public Entry(Person person, Channel channel) {
            this.person = person;
            this.channel = channel;
        }

        public Person getPerson() {
            return person;
        }

        public Channel getChannel() {
            return channel;
        }
    }

}
