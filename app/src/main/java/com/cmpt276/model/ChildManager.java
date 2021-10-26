package com.cmpt276.model;

import java.util.ArrayList;
import java.util.List;

public class ChildManager {
    private List<Child> children = new ArrayList<>();

    public void addChild(Child child){
        children.add(child);
    }

    //remove child index n in the list
    public void removeChild(int n){
        if(isEmpty()) {
            throw new IllegalArgumentException("no child to be deleted");
        }
        else {
            if (n < 0) {
                throw new IllegalArgumentException("n less than 0");
            } else if (n >= children.size()) {
                throw new IllegalArgumentException("n bigger than size");
            } else {
                children.remove(n);
            }
        }
    }
    //get name of child index n in the list
    public String getChildName(int n){
        if (n < 0) {
            throw new IllegalArgumentException("n less than 0");
        } else if (n >= children.size()) {
            throw new IllegalArgumentException("n bigger than size");
        } else {
            return children.get(n).getName();
        }
    }

    //set the name of child index n in the list
    public void setChildName(String name, int n){
        if (n < 0) {
            throw new IllegalArgumentException("n less than 0");
        } else if (n >= children.size()) {
            throw new IllegalArgumentException("n bigger than size");
        } else {
            children.get(n).setName(name);
        }
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }
}
