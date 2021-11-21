package com.cmpt276.model;

/**
 * Container for information about a single child
 * */
public class Child {
    private String name;
    private String encodedImage;

    public Child(String name){
        this.name = name;
    }
    public Child(String name, String encodedImage){
        this.name = name;
        this.encodedImage = encodedImage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getEncodedImage() {
        return encodedImage;
    }

    public void setEncodedImage(String encodedImage) {
        this.encodedImage = encodedImage;
    }
}
