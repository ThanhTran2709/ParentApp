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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmpt276.model.Child;
import com.cmpt276.model.Options;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
        setUpBackBtn();
        setUpAddBtn();
        populateList();
    }

    private void setUpBackBtn() {
        Button backBtn = (Button) findViewById(R.id.backBtn_children);
        backBtn.setText(R.string.backTxt);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void setUpAddBtn() {
        Button addBtn = (Button) findViewById(R.id.addBtn);
        addBtn.setText(R.string.addBtnText);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddChildDialog alert = new AddChildDialog();
                alert.showDialog(ChildrenActivity.this);
            }
        });
    }

    //Populate list view with name and age of children
    private void populateList(){
        ArrayAdapter<Child> adapter = new MyListAdapter();
        ListView childrenListView = (ListView) findViewById(R.id.childrenListView);
        childrenListView.setAdapter(adapter);
        childrenListView.setDivider(null);
        childrenListView.setDividerHeight(16);
    }

    private class MyListAdapter extends ArrayAdapter<Child>{

        public MyListAdapter(){
            super(ChildrenActivity.this, R.layout.children_view, options.getChildList());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View gamesView = convertView;
            if (gamesView == null){
                gamesView = getLayoutInflater().inflate(R.layout.children_view, parent, false);
            }

            Child currentChild = options.getChildList().get(position);

            // set up game ListView item
            TextView childName = gamesView.findViewById(R.id.child_name);
            childName.setText(currentChild.getName());
            return gamesView;
        }

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

                    EditChildDialog alert = new EditChildDialog();
                    alert.showDialog(ChildrenActivity.this, index);
                }
            });
        }
    }

    public class AddChildDialog {

        public void showDialog(Activity activity) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.add_child_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            EditText nameInput = dialog.findViewById(R.id.childNameedittext);

            FloatingActionButton cancelFab = dialog.findViewById(R.id.cancelfab);
            cancelFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            FloatingActionButton addFab = dialog.findViewById(R.id.addfab);
            addFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(nameInput.getText().toString().isEmpty()) {
                        Toast.makeText(ChildrenActivity.this, "Enter a valid name for the child", Toast.LENGTH_SHORT).show();
                    }else {
                        options.addChild(new Child(nameInput.getText().toString()));

                        Options.saveChildListInPrefs(ChildrenActivity.this, options.getChildList());
                        Options.saveStringListInPrefs(ChildrenActivity.this, options.getChildListToString());
                        populateList();

                        dialog.cancel();
                    }
                }
            });

            dialog.show();
        }
    }

    public class EditChildDialog {

        public void showDialog(Activity activity, int index) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.edit_child_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            EditText nameInput = dialog.findViewById(R.id.childNameEditText2);
            nameInput.setText(options.getChildList().get(index).getName());


            FloatingActionButton cancelFab = dialog.findViewById(R.id.cancelfab2);
            cancelFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            FloatingActionButton addFab = dialog.findViewById(R.id.addfab2);
            addFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (nameInput.getText().toString().isEmpty()) {
                        Toast.makeText(ChildrenActivity.this, "Enter a valid name for the child", Toast.LENGTH_SHORT).show();
                    } else {
                        options.editChild(index, nameInput.getText().toString());
                        Options.saveChildListInPrefs(ChildrenActivity.this, options.getChildList());
                        Options.saveStringListInPrefs(ChildrenActivity.this, options.getChildListToString());
                        populateList();
                        dialog.cancel();
                    }
                }
            });

            FloatingActionButton deleteFab = dialog.findViewById(R.id.deletefab2);
            deleteFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    options.removeChild(index);
                    Options.saveChildListInPrefs(ChildrenActivity.this, options.getChildList());
                    Options.saveStringListInPrefs(ChildrenActivity.this, options.getChildListToString());
                    populateList();
                    dialog.cancel();
                }
            });

            dialog.show();
        }
    }

}