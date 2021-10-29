package com.cmpt276.parentapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.cmpt276.model.Child;
import com.cmpt276.model.Options;

public class ChildrenActivity extends AppCompatActivity {

    private Options options = Options.getInstance();

    public static Intent getIntent(Context context){
        return new Intent(context, ChildrenActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children);

        refreshDisplay();
    }

    private void refreshDisplay(){
        populateList();
        listItemClick();
        setUpAddBtn();
    }

    private void setUpAddBtn() {
        Button addBtn = (Button) findViewById(R.id.addBtn);
        addBtn.setText("Add new child");

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create dialog to edit children
                AlertDialog.Builder builder = new AlertDialog.Builder(ChildrenActivity.this);
                LinearLayout dialogLayout = new LinearLayout(ChildrenActivity.this);

                //TO DO: Reformat UI with XML
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(5, 5, 5, 0);

                dialogLayout.setOrientation(LinearLayout.VERTICAL);

                builder.setTitle("Enter or edit name and age:");

                final EditText nameInput = new EditText(ChildrenActivity.this);
                final EditText ageInput = new EditText(ChildrenActivity.this);
                nameInput.setLayoutParams(params);
                ageInput.setLayoutParams(params);

                nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
                nameInput.setHint("Enter name here");
                builder.setView(nameInput);

                ageInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                ageInput.setHint("Enter age here");
                builder.setView(ageInput);

                dialogLayout.addView(nameInput);
                dialogLayout.addView(ageInput);// displays the user input bar
                builder.setView(dialogLayout);

                // Set up the buttons to add or exit
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {
                        options.addChild(new Child(nameInput.getText().toString(), Integer.parseInt(ageInput.getText().toString())));
                        refreshDisplay();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    //Populate list view with name and age of children
    private void populateList(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.children_list, options.getChildListToString());

        ListView childrenListView = (ListView) findViewById(R.id.childrenListView);
        childrenListView.setAdapter(adapter);
    }

    //Click handling for children list view
    private void listItemClick(){
        if (options.getChildList().size() == 0)
            return;
        else {
            ListView childrenListView = (ListView) findViewById(R.id.childrenListView);
            childrenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View childClicked, int index, long position) {

                    //Create dialog to edit children
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChildrenActivity.this);
                    LinearLayout dialogLayout = new LinearLayout(ChildrenActivity.this);

                    //TO DO: Reformat UI with XML
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(5, 5, 5, 0);

                    dialogLayout.setOrientation(LinearLayout.VERTICAL);

                    builder.setTitle("Enter or edit name and age:");

                    final EditText nameInput = new EditText(ChildrenActivity.this);
                    final EditText ageInput = new EditText(ChildrenActivity.this);
                    nameInput.setLayoutParams(params);
                    ageInput.setLayoutParams(params);

                    nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
                    nameInput.setHint("Enter name here");
                    builder.setView(nameInput);

                    ageInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                    ageInput.setHint("Enter age here");
                    builder.setView(ageInput);

                    dialogLayout.addView(nameInput);
                    dialogLayout.addView(ageInput);// displays the user input bar
                    builder.setView(dialogLayout);

                    // Set up the buttons to save or exit
                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int choice) {
                            String newName = nameInput.getText().toString();
                            int newAge = Integer.parseInt(ageInput.getText().toString());
                            options.editChild(index, newName, newAge);
                            refreshDisplay();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int choice) {
                            dialog.cancel();
                        }
                    });

                    builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int choice) {
                            options.removeChild(index);
                            refreshDisplay();
                        }
                    });

                    builder.show();
                }
            });
        }
    }
}