package com.cmpt276.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpt276.model.Child;
import com.cmpt276.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TaskActivity extends AppCompatActivity {

    public static Intent getIntent(Context context){
        return new Intent(context, TaskActivity.class);
    }

    private Options options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        options = Options.getInstance(this);
        setUpAddTaskFAB();
        setUpBackBtn();
        populateTaskList();
    }

    private void setUpBackBtn() {
        Button backBtn = findViewById(R.id.backBtn_tasklist);
        backBtn.setOnClickListener(view -> finish());
    }

    private void setUpAddTaskFAB(){
        FloatingActionButton addTaskFAB = findViewById(R.id.addTaskFAB);
        addTaskFAB.setOnClickListener(v -> {
            //TODO new dialog to add task
            AddTaskDialog taskDialog = new AddTaskDialog();
            taskDialog.showDialog(TaskActivity.this);
        });
    }

    public class AddTaskDialog {

        public void showDialog(Activity activity) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.add_child_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            EditText nameInput = dialog.findViewById(R.id.childNameedittext);
            nameInput.setHint(R.string.enter_task_name);
            TextView dialogTitle = dialog.findViewById(R.id.add_child_dialog_title);
            dialogTitle.setText(R.string.add_new_task);

            FloatingActionButton cancelFab = dialog.findViewById(R.id.cancelfab);
            cancelFab.setOnClickListener(getCancelFabListener(dialog));

            FloatingActionButton addFab = dialog.findViewById(R.id.addfab);
            addFab.setOnClickListener(getAddFabListener(dialog, nameInput));

            dialog.show();
        }

        private View.OnClickListener getAddFabListener(Dialog dialog, EditText nameInput) {
            return (view) -> {
                if(nameInput.getText().toString().isEmpty()) {
                    Toast.makeText(TaskActivity.this, "Error! Enter a valid Task Name", Toast.LENGTH_SHORT).show();
                }
                else {
                    options.addTask(new Task(nameInput.getText().toString(), options.getChildList().size()));

                    Options.saveTaskListInPrefs(TaskActivity.this, options.getTaskList());

                    populateTaskList();
                    setUpListItemClickListener();

                    dialog.cancel();
                }
            };
        }

        private View.OnClickListener getCancelFabListener(Dialog dialog) {
            return (view) -> dialog.dismiss();
        }
    }

    public class EditTaskDialog {

        public void showDialog(Activity activity, int index) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.edit_child_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            EditText nameInput = dialog.findViewById(R.id.enter_child_name);
            nameInput.setText(options.getTaskList().get(index).getTaskName());
            nameInput.setHint(R.string.enter_task_name);

            TextView dialogTitle = dialog.findViewById(R.id.edit_child_dialog_title);
            dialogTitle.setText(R.string.edit_task_name);

            FloatingActionButton cancelFab = dialog.findViewById(R.id.cancelfab2);
            cancelFab.setOnClickListener(getCancelFabListener(dialog));

            FloatingActionButton addFab = dialog.findViewById(R.id.addfab2);
            addFab.setOnClickListener(getAddFabListener(dialog, nameInput, index));

            FloatingActionButton deleteFab = dialog.findViewById(R.id.deletefab2);
            deleteFab.setOnClickListener(getDeleteFabListener(dialog, index));

            dialog.show();
        }

        private View.OnClickListener getCancelFabListener(Dialog dialog) {
            return (view) -> dialog.dismiss();
        }

        private View.OnClickListener getAddFabListener(Dialog dialog, EditText nameInput, int index) {
            return (view) -> {
                if (nameInput.getText().toString().isEmpty()) {
                    Toast.makeText(TaskActivity.this, "Error! Enter a valid Task Name", Toast.LENGTH_SHORT).show();
                } else {
                    options.editTaskName(nameInput.getText().toString(), index);
                    Options.saveTaskListInPrefs(TaskActivity.this, options.getTaskList());
                    populateTaskList();
                    setUpListItemClickListener();
                    dialog.cancel();
                }
            };
        }

        private View.OnClickListener getDeleteFabListener(Dialog dialog, int index) {
            return (view) -> {
                options.removeTask(index);
                Options.saveChildListInPrefs(TaskActivity.this, options.getChildList());
                populateTaskList();
                setUpListItemClickListener();
                dialog.cancel();
            };
        }
    }


    private void populateTaskList() {
        TaskListAdapter adapter = new TaskListAdapter();
        ListView taskList = findViewById(R.id.taskListView);
        taskList.setAdapter(adapter);
        setUpListItemClickListener();

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
            Button editButton = taskView.findViewById(R.id.edit_task_button);
            editButton.setOnClickListener(view -> {
                EditTaskDialog editTaskDialog = new EditTaskDialog();
                editTaskDialog.showDialog(TaskActivity.this, position);
            });

            TextView childName = taskView.findViewById(R.id.childNameInTaskList);
            childName.setText(options.getChildName(currentTask.getCurrentChildIndex()));
            return taskView;
        }

    }

    private void setUpListItemClickListener(){
        if (options.getTaskList().size() == 0) {
            return;
        }
        ListView taskListView = findViewById(R.id.taskListView);
        taskListView.setOnItemClickListener((adapterView, taskClicked, position, id) -> {
            ConfirmTaskDoneDialog dialog = new ConfirmTaskDoneDialog();
            dialog.showDialog(TaskActivity.this, position);

        });
    }

    public class ConfirmTaskDoneDialog {

        public void showDialog(Activity activity, int index) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.confirm_task_done_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            FloatingActionButton cancelFab = dialog.findViewById(R.id.cancelfab6);
            cancelFab.setOnClickListener(getCancelFabListener(dialog));

            FloatingActionButton confirmFab = dialog.findViewById(R.id.confirmfab);
            confirmFab.setOnClickListener(getAddFabListener(dialog, index));

            Task task = options.getTaskList().get(index);

            TextView confirmText = dialog.findViewById(R.id.confirm_textview);
            confirmText.setText(String.format(String.valueOf(R.string.confirm_message), options.getChildName(
                            task.getCurrentChildIndex()), task.getTaskName()));
            dialog.show();

        }

        private View.OnClickListener getCancelFabListener(Dialog dialog) {
            return (view) -> dialog.dismiss();
        }

        private View.OnClickListener getAddFabListener(Dialog dialog, int index) {
            return (view) -> {
                options.getTaskList().get(index).incrementNextChildIndex(options.getChildList().size());
                populateTaskList();
                dialog.dismiss();


            };
        }

    }

}