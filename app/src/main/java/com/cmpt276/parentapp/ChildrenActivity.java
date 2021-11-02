package com.cmpt276.parentapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.model.Child;
import com.cmpt276.model.Options;

public class ChildrenActivity extends AppCompatActivity {

    private Options options;

    public static Intent getIntent(Context context){
        return new Intent(context, ChildrenActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children);
        options = Options.getInstance(this);

        listItemClick();
        setUpAddBtn();
        populateList();
    }


    private void setUpAddBtn() {
        Button addBtn = (Button) findViewById(R.id.addBtn);
        addBtn.setText(R.string.addBtnText);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create dialog to edit children
                AlertDialog.Builder builder = new AlertDialog.Builder(ChildrenActivity.this);
                LinearLayout dialogLayout = new LinearLayout(ChildrenActivity.this);

                //TO DO: Reformat UI with XML
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(5, 5, 5, 5);

                dialogLayout.setOrientation(LinearLayout.VERTICAL);

                builder.setTitle(R.string.addBtnTitle);

                // displays the user input bar
                final EditText nameInput = new EditText(ChildrenActivity.this);
                nameInput.setLayoutParams(params);

                nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
                nameInput.setHint(R.string.nameHint);
                builder.setView(nameInput);

                dialogLayout.addView(nameInput);
                builder.setView(dialogLayout);

                // Set up the buttons to add or exit
                builder.setPositiveButton(R.string.addText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {

                        options.addChild(new Child(nameInput.getText().toString()));

                        Options.saveChildListInPrefs(ChildrenActivity.this, options.getChildList());
                        Options.saveStringListInPrefs(ChildrenActivity.this, options.getChildListToString());
                        populateList();

                    }
                });
                builder.setNegativeButton(R.string.cancelText, new DialogInterface.OnClickListener() {
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
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(5, 5, 5, 5);

                    dialogLayout.setOrientation(LinearLayout.VERTICAL);

                    builder.setTitle(R.string.addBtnTitle);

                    // displays the user input bar
                    final EditText nameInput = new EditText(ChildrenActivity.this);
                    nameInput.setLayoutParams(params);

                    nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
                    nameInput.setText(options.getChildList().get(index).getName());
                    builder.setView(nameInput);

                    dialogLayout.addView(nameInput);
                    builder.setView(dialogLayout);

                    // Set up the buttons to save or exit
                    builder.setPositiveButton(R.string.saveText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int choice) {
                            
                            options.editChild(index, nameInput.getText().toString());
                            Options.saveChildListInPrefs(ChildrenActivity.this, options.getChildList());
                            Options.saveStringListInPrefs(ChildrenActivity.this, options.getChildListToString());
                            Toast.makeText(ChildrenActivity.this, ChildrenActivity.this.getString(R.string.emptyNameErrorTxt), Toast.LENGTH_SHORT).show();
                            populateList();


                        }
                    });
                    builder.setNegativeButton(R.string.cancelText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int choice) {
                            dialog.cancel();
                        }
                    });

                    builder.setNeutralButton(R.string.deleteText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int choice) {
                            options.removeChild(index);
                            Options.saveChildListInPrefs(ChildrenActivity.this, options.getChildList());
                            Options.saveStringListInPrefs(ChildrenActivity.this, options.getChildListToString());
                            populateList();
                        }
                    });

                    builder.show();
                }
            });
        }
    }

}