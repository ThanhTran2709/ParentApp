package com.cmpt276.model;

public class Child {
    private String name;
    private int age;

    public Child(String name, int age){
        this.name = name;
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age){
        this.age = age;
    }

    public String getName() {
        return this.name;
    }

    public int getAge(){
        return this.age;
    }
}
