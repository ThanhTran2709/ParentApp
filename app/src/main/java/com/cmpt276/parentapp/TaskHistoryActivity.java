package com.cmpt276.parentapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.cmpt276.model.Coin;
import com.cmpt276.model.TaskHistory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TaskHistoryActivity extends AppCompatActivity {

    Options options;
    int taskIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_history);
        options = Options.getInstance();

        //get history list
        ArrayList<TaskHistory> taskHistoryList;

        RecyclerView rv = findViewById(R.id.recyclerViewTaskHistory);
        rv.setLayoutManager(new LinearLayoutManager(this));
        //TaskHistoryAdapter adapter = new TaskHistoryAdapter(this, taskHistoryList);
        //rv.setAdapter(adapter);

        setUpBackBtn();
        setUpClearBtn();
    }

    private void setUpBackBtn() {
        Button backBtn = findViewById(R.id.backBtn_TaskHistory);
        backBtn.setText(R.string.back);
        backBtn.setOnClickListener((view) -> finish());
    }

    private void setUpClearBtn() {
        Button buttonClear = findViewById(R.id.buttonClearTaskHistory);
        buttonClear.setOnClickListener((view) -> {
            /*if (options.getTaskHistory(this, index).size() == 0) {
                Toast.makeText(this, "No history to delete", Toast.LENGTH_SHORT).show();
            }
            else {
                CoinFlipHistoryActivity.ClearHistoryDialog alert = new CoinFlipHistoryActivity.ClearHistoryDialog();
                alert.showDialog(CoinFlipHistoryActivity.this);
            }*/
        });
    }

    /**
     * Custom dialog to clear history
     * */
    public class ClearHistoryDialog {

        public void showDialog(Activity activity) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.clear_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            FloatingActionButton cancelFab = dialog.findViewById(R.id.cancelFab);
            cancelFab.setOnClickListener(getCancelFabListener(dialog));

            FloatingActionButton addFab = dialog.findViewById(R.id.okFab);
            addFab.setOnClickListener(getAddFabListener());

            dialog.show();
        }

        private View.OnClickListener getCancelFabListener(Dialog dialog) {
            return (view) -> dialog.dismiss();
        }

        private View.OnClickListener getAddFabListener() {
            return (view) -> {
                //options.clearCoinFlips(CoinFlipHistoryActivity.this);
                //CoinFlipHistoryActivity.this.finish();
            };
        }
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, TaskHistoryActivity.class);
        return intent;
    }
}