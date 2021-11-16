package com.cmpt276.model;

public class Task {
    private static final int NO_CHILD = -1;
    private String taskName;
    private int currentChildIndex;

    public Task(String taskName) {
        this.taskName = taskName;
        this.currentChildIndex = NO_CHILD;
    }

    public void assignNextChild(int index){
        currentChildIndex = index;
    }

    public void editTaskName(String newTaskName){
        taskName = newTaskName;
    }

    public String getTaskName() {
        return taskName;
    }

    public int getCurrentChildIndex() {
        return currentChildIndex;
    }

    public void incrementNextChildIndex(int childListSize){
        currentChildIndex = (currentChildIndex + 1) % childListSize;
    }

    public void updateIndexOnChildDelete(int deletedChildIndex, int childListSize){
        if(childListSize == 0){
            currentChildIndex = NO_CHILD;
        }
        else{
            if(deletedChildIndex == currentChildIndex){
            }
            else{
                currentChildIndex--;
            }
        }
    }
}
