package com.cmpt276.model;

import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Class for storing individual coin flips, including (but not necessarily limited to):
 * - which child picked heads/tails
 * - what the child chose
 * - the outcome of the flip
 * - the date and time the flip occurred
 * */
public class Coin {
	//TODO: once child is done, make serializer to and from JSON objects.

	public static final int NO_CHOICE = -1;
	public static final int HEADS = 0;
	public static final int TAILS = 1;

	private static final int NUM_RESULTS = 2;

	private static final Random rng = new Random();

	private Child child;
	private int flipChoice;
	private int flipResult;
	private LocalDateTime time;

	//on generating coin flip, records the time and chooses hads or tails randomly
	public Coin(Child child, int flipChoice){
		this.child = child;
		this.flipChoice = flipChoice;
		this.time = LocalDateTime.now();
		this.flipResult = randomCoinFlip();
	}

	public Coin(){
		this.child = null;
		this.flipChoice = NO_CHOICE;
		this.time = LocalDateTime.now();
		this.flipResult = randomCoinFlip();
	}

	public Coin(JSONObject jsonObject){

	}

	public Child getChild() {
		return child;
	}

	public int getFlipChoice() {
		return flipChoice;
	}

	public int getFlipResult() {
		return flipResult;
	}

	public LocalDateTime getTime() {
		return time;
	}

	private static int randomCoinFlip(){
		int result = rng.nextInt(NUM_RESULTS);
		return result;
	}

	public String getTimeFormatted(){
		String pattern = "uuuu-MMM-dd hh:mm a";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return time.format(formatter);
	}
}
