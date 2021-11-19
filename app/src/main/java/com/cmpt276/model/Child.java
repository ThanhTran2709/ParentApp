package com.cmpt276.model;

/**
 * Container for information about a single child
 * */
public class Child {
	private String name;

	public Child(String name){
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

}
