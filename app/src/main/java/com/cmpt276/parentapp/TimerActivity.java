package com.cmpt276.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class TimerActivity extends AppCompatActivity {

    private CountDownTimer timer;
    private static final String MINUTES_TAG = "minutes tag";
    private static final int DEFAULT_MINUTES = 5;
    private int minutes;

    public static Intent getIntent(Context context, int minutes){
        Intent i = new Intent(context, TimerActivity.class);
        i.putExtra(MINUTES_TAG, minutes);
        return i;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        minutes = this.getIntent().getIntExtra(MINUTES_TAG, DEFAULT_MINUTES);
        setUpPausePlayButton();
        setUpResetButton();
        setupTimeOutTimer();
        Toast.makeText(this, "You chose " + minutes + " minutes", Toast.LENGTH_SHORT).show();

    }

    private void setupTimeOutTimer() {

    }

    private void setUpResetButton() {
    }

    private void setUpPausePlayButton() {
    }


}