package com.cmpt276.parentapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cmpt276.model.Child;
import com.cmpt276.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TaskActivity extends AppCompatActivity {

    private Options options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        options = Options.getInstance(this);
        setUpAddTaskFAB();

        ListView taskList = findViewById(R.id.taskListView);
        TaskListAdapter adapter = new TaskListAdapter();
        taskList.setAdapter(adapter);

    }

    private void setUpAddTaskFAB(){
        FloatingActionButton addTaskFAB = findViewById(R.id.addTaskFAB);
        addTaskFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO new dialog to add task
            }
        });
    }

    private class TaskListAdapter extends ArrayAdapter<Task> {

        public TaskListAdapter(){
            super(TaskActivity.this, R.layout.task_view, options.getTaskList());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View taskView = convertView;
            if (taskView == null){
                taskView = getLayoutInflater().inflate(R.layout.task_view, parent, false);
            }

            Task currentTask = options.getTaskList().get(position);

            // set up game ListView item
            TextView taskName = taskView.findViewById(R.id.taskName);
            taskName.setText(currentTask.getTaskName());
            return taskView;
        }

    }

    private void listItemClick(){
        if (options.getTaskList().size() == 0) {
            return;
        }
        ListView taskListView = findViewById(R.id.taskListView);
        taskListView.setOnItemClickListener((adapterView, taskClicked, index, position) -> {
            //TODO add a dialog to edit the task

        });
    }
}