package com.cmpt276.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

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

    public Bitmap getImageBitmap(){
        byte[] decodedByte = Base64.decode(encodedImage, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
