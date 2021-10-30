package com.cmpt276.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * List of coin flips, with the ability to add and get individual flips, and the ability to clear the list.
 * */
public class CoinList {
	private List<Coin> coinFlipRecords = new ArrayList<>();

	public CoinList(){}

	public Coin get(int index){
		return coinFlipRecords.get(index);
	}

	public void set(int index, Coin coin){
		coinFlipRecords.set(index, coin);
	}

	public void add(Coin coin){
		coinFlipRecords.add(coin);
	}

	public void clear(){
		coinFlipRecords.clear();
	}
}
