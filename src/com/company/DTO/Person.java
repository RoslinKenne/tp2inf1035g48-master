package com.company.DTO;

import com.company.annotations.Alias;
import com.company.annotations.Ignored;

import java.util.List;

public class Person {
    @Alias("nom")
    private String name;
    @Ignored
    private int age;
    private Address address;
    private List<String> hobbies;

    public Person() {}

    public Person(String name, int age, Address address, List<String> hobbies) {
        this.name = name;
        this.age = age;
        this.address = address;
        this.hobbies = hobbies;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    public List<String> getHobbies() { return hobbies; }
    public void setHobbies(List<String> hobbies) { this.hobbies = hobbies; }
}
