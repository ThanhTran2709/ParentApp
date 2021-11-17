package com.cmpt276.parentapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.cmpt276.model.Child;
import com.cmpt276.model.Coin;
import com.cmpt276.parentapp.serializer.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Options class implement Shared Preferences to save data between app runs
 */
public class Options {
	private ArrayList<Child> childList;
	private ArrayList<String> childListToString;
	private static Options instance;

	private static final String PREFS_TAG = "SharedPrefs";
	private static final String CHILD_TAG = "Child";
	private static final String STRING_TAG = "String";

	private static final String FLIP_LIST_TAG = "FlipList";
	private static final String FLIP_QUEUE_TAG = "FlipQueue";
	private static final String NO_CHILD_FLIPPING = "NoChildFlipping";

	private Options(Context context) {
		if (getChildListFromPrefs(context).size() == 0) {
			childList = new ArrayList<>();
			childListToString = new ArrayList<>();
		}
		else {
			childList = getChildListFromPrefs(context);
			childListToString = getStringListFromPrefs(context);
		}
	}

	public static Options getInstance(Context context){
		if (instance == null)
			instance = new Options(context);
		return instance;
	}

	public ArrayList<String> getChildListToString() {
		return childListToString;
	}

	public void addChild(Child child){
		childList.add(child);
		childListToString.add(child.getName());
	}

	public void removeChild(int index){
		childList.remove(index);
		childListToString.remove(index);
	}

	public void editChild(int index, String name){
		childList.get(index).setName(name);
		childListToString.set(index, name);
	}

	public ArrayList<Child> getChildList() {
		return childList;
	}

	/*
	* the queue of children is an array of indices which correspond to the children stored
	*
	* CHILDREN: [Bob(0), Joe(1), Jefferey(2), Jacob(3), Jimothy(4)]
	* QUEUE: [1, 3, 4, 2, 0]
	* indicates that the order should be Joe, Jacob, Jimothy, Jefferey, Bob
	*
	* the operations we can do to the queue is to
	* - get the queue
	* - move an element to the front
	* - move the front of the queue to the back
	*
	* Terminology:
	* queue index - index of queue array (e.g. queue index of 2 in the above example has value 4)
	* */
	public ArrayList<Integer> getQueueOrder(Context context){
		SharedPreferences pref = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		String jsonString = pref.getString(FLIP_QUEUE_TAG, null);

		Gson gson = new Gson();
		Type arrayListType = new TypeToken<ArrayList<Integer>>(){}.getType();

		ArrayList<Integer> queue = gson.fromJson(jsonString, arrayListType);

		if (queue == null){
			queue = new ArrayList<>();
			for (int i = 0; i < childList.size(); i++){
				queue.add(i);
			}
		}
		else if (queue.size() > childList.size()){
			//remove all indices that are greater than the current child list size to prevent out of bounds errors when removing children
			for (int i = 0; i < queue.size(); i++){
				if (queue.get(i) >= childList.size()){
					queue.remove(i--);
				}
			}
		}
		else if (queue.size() < childList.size()){
			//append the missing indices in order up to the number of children there are
			for (int i = queue.size(); i < childList.size(); i++){
				queue.add(i);
			}
		}

		String newQueueString = gson.toJson(queue, arrayListType);
		editor.putString(FLIP_QUEUE_TAG, newQueueString);
		editor.apply();

		return queue;
	}

	//moves the given queue index element to the front
	public void moveToFrontOfQueue(Context context, int index){
		SharedPreferences pref = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();

		ArrayList<Integer> queue = getQueueOrder(context);

		int element = queue.remove(index);
		queue.add(0, element);

		Gson gson = new Gson();
		Type arrayListType = new TypeToken<ArrayList<Integer>>(){}.getType();

		String newQueueString = gson.toJson(queue, arrayListType);
		editor.putString(FLIP_QUEUE_TAG, newQueueString);
		editor.apply();
	}

	//moves the element at the front of the queue to the back, thus advancing the queue
	public void advanceQueue(Context context){
		SharedPreferences pref = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();

		ArrayList<Integer> queue = getQueueOrder(context);

		int element = queue.remove(0);
		queue.add(element);

		Gson gson = new Gson();
		Type arrayListType = new TypeToken<ArrayList<Integer>>(){}.getType();

		String newQueueString = gson.toJson(queue, arrayListType);
		editor.putString(FLIP_QUEUE_TAG, newQueueString);
		editor.apply();
	}

	//Returns true if the current setting is that no child is selected for flipping
	//Does not interfere with queue order in any way.
	public boolean isNoChildFlipping(Context context){
		SharedPreferences pref = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
		boolean isFlipping = pref.getBoolean(NO_CHILD_FLIPPING, false);
		return isFlipping;
	}

	public void setNoChildFlipping(Context context, boolean isFlipping){
		SharedPreferences pref = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();

		editor.putBoolean(NO_CHILD_FLIPPING, isFlipping);
		editor.apply();
	}

	public void addCoinFlip(Context context, Coin coin){
		//pulls json encoded array from shared preferences, adds the coin to the list, then
		//re-encodes it to json to be sent back to preferences
		SharedPreferences pref = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		String jsonString = pref.getString(FLIP_LIST_TAG, null);

		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
				.create();
		Type type = new TypeToken<ArrayList<Coin>>(){}.getType();

		ArrayList<Coin> flipHistory;

		if (jsonString == null){
			flipHistory = new ArrayList<>();
		}
		else {
			flipHistory = gson.fromJson(jsonString, type);
		}
		flipHistory.add(coin);

		jsonString = gson.toJson(flipHistory);
		editor.putString(FLIP_LIST_TAG, jsonString);

		editor.apply();
	}

	public ArrayList<Coin> getFlipHistory(Context context){
		SharedPreferences pref = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
		String jsonString = pref.getString(FLIP_LIST_TAG, null);
		if (jsonString == null){
			return new ArrayList<>();
		}

		Gson gson = new GsonBuilder()
				.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
				.create();
		Type type = new TypeToken<ArrayList<Coin>>(){}.getType();

		return gson.fromJson(jsonString, type);
	}

	public void clearCoinFlips(Context context){
		SharedPreferences pref = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.remove(FLIP_LIST_TAG);

		editor.apply();
	}

	//The next 4 functions implement shared preferences for ArrayLists of Child and String type

	//Save Child List to Shared Prefs
	public static void saveChildListInPrefs(Context context, ArrayList<Child> list) {
		Gson gson = new Gson();
		String jsonString = gson.toJson(list);

		SharedPreferences pref  = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(CHILD_TAG, jsonString);
		editor.apply();
	}

	//Get Child List to Shared Prefs
	public static ArrayList<Child> getChildListFromPrefs(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
		String jsonString = pref.getString(CHILD_TAG, "");

		Gson gson = new Gson();
		Type type = new TypeToken<ArrayList<Child>>() {}.getType();
		ArrayList<Child> list = gson.fromJson(jsonString, type);
		if (list == null) {
			list = new ArrayList<>();
		}

		return list;
	}

	//Save Child List in String form to Shared Prefs
	public static void saveStringListInPrefs(Context context, ArrayList<String> list) {
		Gson gson = new Gson();
		String jsonString = gson.toJson(list);

		SharedPreferences pref  = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(STRING_TAG, jsonString);
		editor.apply();
	}

	//Get Child List in String form to Shared Prefs
	public static ArrayList<String> getStringListFromPrefs(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
		String jsonString = pref.getString(STRING_TAG, "");

		Gson gson = new Gson();
		Type type = new TypeToken<ArrayList<String>>() {}.getType();
		ArrayList<String > list = gson.fromJson(jsonString, type);
		if (list == null) {
			list = new ArrayList<>();
		}
		return list;
	}
}
