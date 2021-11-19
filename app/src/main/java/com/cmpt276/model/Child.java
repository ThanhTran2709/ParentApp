package com.cmpt276.model;

/**
 * Container for information about a single child
 * */
public class Child {
	private String name;
	private long id;

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

	public long getId(){
		return id;
	}

}
