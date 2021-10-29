package com.cmpt276.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

        populateList();
    }

    private void populateList(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.children_list, options.getChildListToString());

        ListView childrenListView = (ListView) findViewById(R.id.childrenListView);
        childrenListView.setAdapter(adapter);
    }

    /*private void listItemClick(){
        if (options.getChildList().size() == 0)
            return;
        else {
            ListView childrenListView = (ListView) findViewById(R.id.childrenListView);
            childrenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View gameClicked, int index, long position) {
                    Intent intentEditGame = EditGame.makeIntent(MainActivity.this, index);
                    startActivity(intentEditGame);
                }
            });
        }
    }*/
}