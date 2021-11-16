package com.cmpt276.parentapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.cmpt276.model.Child;
import com.cmpt276.model.Coin;
import com.cmpt276.model.Task;
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
    private ArrayList<Task> taskList;
    private static Options instance;

    private static final String PREFS_TAG = "SharedPrefs";
    private static final String CHILD_TAG = "Child";
    private static final String TASK_TAG = "Task";
    private static final String STRING_TAG = "String";

    private static final String CHILD_FLIP_INDEX_TAG = "ChildFlipIndex";
    private static final String FLIP_LIST_TAG = "FlipList";

    private Options(Context context) {
        if (getChildListFromPrefs(context).size() == 0) {
            childList = new ArrayList<>();
            childListToString = new ArrayList<>();
        }
        else {
            childList = getChildListFromPrefs(context);
            childListToString = getStringListFromPrefs(context);
        }
        if (getTaskListFromPrefs(context).size() == 0){
            taskList = new ArrayList<>();
        }
        else{
            taskList = getTaskListFromPrefs(context);
        }
    }

    public static Options getInstance(Context context){
        if (instance == null)
            instance = new Options(context);
        return instance;
    }

    public void addTask(Task task){
        taskList.add(task);
    }

    public void removeTask(int index){
        taskList.remove(index);
    }

    public void editTaskName(String newTaskName, int taskIndex){
        taskList.get(taskIndex).editTaskName(newTaskName);
    }

    public void assignChildToTask(int childIndex, int taskIndex){
        taskList.get(taskIndex).assignNextChild(childIndex);
    }

    public ArrayList<Task> getTaskList(){
        return taskList;
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
        for(Task task : taskList){
            task.updateIndexOnChildDelete(index, childList.size());
        }
    }

    public void editChild(int index, String name){
        childList.get(index).setName(name);
        childListToString.set(index, name);
    }

    public ArrayList<Child> getChildList() {
        return childList;
    }

    public void setChildFlipIndex(Context context, int index){
        SharedPreferences pref = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(CHILD_FLIP_INDEX_TAG, index);

        editor.apply();
    }

    public int getChildFlipIndex(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        return pref.getInt(CHILD_FLIP_INDEX_TAG, 0);
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

    //Save Task List to Shared Prefs
    public static void saveTaskListInPrefs(Context context, ArrayList<Task> list) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);

        SharedPreferences pref  = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(TASK_TAG, jsonString);
        editor.apply();
    }

    //Get Task List to Shared Prefs
    public static ArrayList<Task> getTaskListFromPrefs(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFS_TAG, Context.MODE_PRIVATE);
        String jsonString = pref.getString(TASK_TAG, "");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Task>>() {}.getType();
        ArrayList<Task> list = gson.fromJson(jsonString, type);
        if (list == null) {
            list = new ArrayList<>();
        }

        return list;
    }
}
