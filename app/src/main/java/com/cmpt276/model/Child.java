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
	private final long id;

    public Child(String name, String encodedImage, long id){
        this.name = name;
        this.encodedImage = encodedImage;
        this.id = id;
    }
	public Child(String name, long id){
		this.name = name;
		this.id = id;
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

    //Use this to quickly return an image bitmap
    public Bitmap getImageBitmap(){
        byte[] decodedByte = Base64.decode(encodedImage, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

	public long getId(){
		return id;
	}

}
