package edu.um.maspalomas.organisation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Organisation {

    private final String name;
    private final HashSet<String> roles;
    private final List<Employee> employees = new ArrayList<>();

    public Organisation(String name, HashSet<String> roles) {
        this.name = name;
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public HashSet<String> getRoles() {
        return roles;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void add(Employee employee) {
        this.employees.add(employee);
    }
}
