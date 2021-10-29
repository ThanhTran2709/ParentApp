package com.cmpt276.model;

import java.util.ArrayList;

public class Options {
    private ArrayList<Child> childList;

    public ArrayList<String> getChildListToString() {
        return childListToString;
    }

    private ArrayList<String> childListToString;
    private static Options instance;

    private Options() {
        childList = new ArrayList<>();
        childListToString = new ArrayList<>();
        fillChildListString();
    }

    private void fillChildListString() {
        if (childList.size() == 0)
            childListToString.add("No Games Yet");
        else
            for (int i = 0; i < childList.size(); i ++)
                childListToString.add(childList.get(i).getName() + "\t" + childList.get(i).getAge());

    }

    public static Options getInstance(){
        if (instance == null)
            instance = new Options();
        return instance;
    }


    public ArrayList<Child> getChildList() {
        return childList;
    }
}
