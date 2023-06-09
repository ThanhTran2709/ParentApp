package com.cmpt276.model;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Object for storing individual tasks. Does not actually store a list of children,
 * but is meant to store the indices of who has to do what.
 * */
public class Task {
	private static final int NO_CHILD = -1;
	private String taskName;
	private int currentChildIndex;

	public Task(String taskName, int numberOfChildren) {
		this.taskName = taskName;
		setChildIndex(numberOfChildren);
	}

	private void setChildIndex(int numberOfChildren) {
		if (numberOfChildren == 0) {
			currentChildIndex = NO_CHILD;
		}
		else {
			currentChildIndex = ThreadLocalRandom.current().nextInt(0, numberOfChildren);
		}

	}

	public void assignNextChild(int index) {
		currentChildIndex = index;
	}

	public void editTaskName(String newTaskName) {
		taskName = newTaskName;
	}

	public String getTaskName() {
		return taskName;
	}

	public int getCurrentChildIndex() {
		return currentChildIndex;
	}

	public void incrementNextChildIndex(int childListSize) {
		currentChildIndex = (currentChildIndex + 1) % childListSize;
	}

	public void updateIndexOnChildDelete(int deletedChildIndex, int childListSize) {
		if (childListSize == 0) {
			currentChildIndex = NO_CHILD;
		}
		else {
			if (deletedChildIndex < currentChildIndex) {
				currentChildIndex--;
			}
			else if (deletedChildIndex == childListSize) {
				currentChildIndex = 0;
			}
		}
	}
}
