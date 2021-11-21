package com.cmpt276.parentapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.model.Child;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


/**
 * Activity for adding, editing, and deleting saved children
 * */
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

        setUpAddBtn();
        populateList();
        setUpBackBtn();
        listItemClick();
    }

    private void setUpBackBtn() {
        Button backBtn = findViewById(R.id.backBtn_children);
        backBtn.setText(R.string.backTxt);
        backBtn.setOnClickListener((view) -> finish());
    }

    private void setUpAddBtn() {
        Button addBtn = findViewById(R.id.addBtn);
        addBtn.setText(R.string.addBtnText);

        addBtn.setOnClickListener((view) -> {
            AddChildDialog alert = new AddChildDialog();
            alert.showDialog(ChildrenActivity.this);
        });
    }

    //Populate list view with name and age of children
    private void populateList(){
        ArrayAdapter<Child> adapter = new ChildrenListViewAdapter();
        ListView childrenListView = findViewById(R.id.childrenListView);
        childrenListView.setAdapter(adapter);
        childrenListView.setDivider(null);
        childrenListView.setDividerHeight(16);
    }

    private class ChildrenListViewAdapter extends ArrayAdapter<Child>{

        public ChildrenListViewAdapter(){
            super(ChildrenActivity.this, R.layout.children_view, options.getChildList());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View gamesView = convertView;
            if (gamesView == null){
                gamesView = getLayoutInflater().inflate(R.layout.children_view, parent, false);
            }

            Child currentChild = options.getChildList().get(position);

            //todo setup child image
            ImageView childImage = gamesView.findViewById(R.id.children_name_list_image);


            // set up game ListView item
            TextView childName = gamesView.findViewById(R.id.child_name);
            childName.setText(currentChild.getName());
            return gamesView;
        }

    }

    //Click handling for children list view
    private void listItemClick(){
        if (options.getChildList().size() == 0) {
            return;
        }
        ListView childrenListView = findViewById(R.id.childrenListView);
        childrenListView.setOnItemClickListener((adapterView, childClicked, index, position) -> {
            EditChildDialog alert = new EditChildDialog();
            alert.showDialog(ChildrenActivity.this, index);
        });
    }

    public class AddChildDialog {


        public void showDialog(Activity activity) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.add_child_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            EditText nameInput = dialog.findViewById(R.id.childNameedittext);

            //basically i just made a listview that will appear when user click the image
            //i was trying to make the listview disappear when user click outside of the listview
            //but i can't figure out how to do that so i just created a cancel button
            String[] optionItem = getResources().getStringArray(R.array.add_image_option);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChildrenActivity.this, R.layout.pick_image_text_view, optionItem);

            ListView pickImage = dialog.findViewById(R.id.addImage);
            pickImage.setAdapter(adapter);
            pickImage.setVisibility(View.INVISIBLE);

            ImageView childImage = dialog.findViewById(R.id.child_image);
            childImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickImage.setVisibility(View.VISIBLE);
                }
            });

            pickImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 0:
                            // TODO code to pick image
                            break;

                        case 1:
                            // TODO code to take photo
                            break;

                        case 2:
                            pickImage.setVisibility(View.INVISIBLE);
                            break;
                    }
                }
            });


            FloatingActionButton cancelFab = dialog.findViewById(R.id.cancelfab);
            cancelFab.setOnClickListener(getCancelFabListener(dialog));

            FloatingActionButton addFab = dialog.findViewById(R.id.addfab);
            addFab.setOnClickListener(getAddFabListener(dialog, nameInput));

            dialog.show();
        }



        private View.OnClickListener getAddFabListener(Dialog dialog, EditText nameInput) {
            return (view) -> {
                if(nameInput.getText().toString().isEmpty()) {
                    Toast.makeText(ChildrenActivity.this, R.string.error_validate_name, Toast.LENGTH_SHORT).show();
                }
                else {
                    options.addChild(new Child(nameInput.getText().toString()));

                    Options.saveChildListInPrefs(ChildrenActivity.this, options.getChildList());
                    Options.saveStringListInPrefs(ChildrenActivity.this, options.getChildListToString());

                    Options.saveTaskListInPrefs(ChildrenActivity.this, options.getTaskList());

                    populateList();
                    listItemClick();


                    dialog.cancel();
                }
            };
        }

        private View.OnClickListener getCancelFabListener(Dialog dialog) {
            return (view) -> dialog.dismiss();
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

            //similar to add child
            String[] optionItem = getResources().getStringArray(R.array.add_image_option);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ChildrenActivity.this, R.layout.pick_image_text_view, optionItem);

            ListView pickImage = dialog.findViewById(R.id.edit_image);
            pickImage.setAdapter(adapter);
            pickImage.setVisibility(View.INVISIBLE);

            ImageView childImage = dialog.findViewById(R.id.child_image_edit);
            childImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickImage.setVisibility(View.VISIBLE);
                }
            });

            pickImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 0:
                            // TODO code to pick image
                            break;

                        case 1:
                            // TODO code to take photo
                            break;

                        case 2:
                            pickImage.setVisibility(View.INVISIBLE);
                            break;
                    }
                }
            });


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
                    Toast.makeText(ChildrenActivity.this, R.string.error_validate_name, Toast.LENGTH_SHORT).show();
                } else {
                    options.editChild(index, nameInput.getText().toString());
                    Options.saveChildListInPrefs(ChildrenActivity.this, options.getChildList());
                    populateList();
                    listItemClick();
                    dialog.cancel();
                }
            };
        }

        private View.OnClickListener getDeleteFabListener(Dialog dialog, int index) {
            return (view) -> {
                options.removeChild(index);
                Options.saveChildListInPrefs(ChildrenActivity.this, options.getChildList());
                Options.saveTaskListInPrefs(ChildrenActivity.this, options.getTaskList());
                populateList();
                listItemClick();

                dialog.cancel();
            };
        }
    }

}