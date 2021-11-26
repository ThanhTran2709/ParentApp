package com.cmpt276.model;

import com.cmpt276.parentapp.serializer.LocalDateTimeAdapter;

import java.time.LocalDateTime;

public class TaskHistory {
    LocalDateTime dateTaskDone;
    int childIndex;

    public TaskHistory(int childIndex){
        this.childIndex = childIndex;
        dateTaskDone = LocalDateTime.now();
    }

    public void setChildIndex(int newChildIndex){
        childIndex = newChildIndex;
    }

    public LocalDateTime getDateTaskDone() {
        return dateTaskDone;
    }

    public int getChildIndex() {
        return childIndex;
    }
}
