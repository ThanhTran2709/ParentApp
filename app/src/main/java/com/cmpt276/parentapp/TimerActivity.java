package com.cmpt276.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class TimerActivity extends AppCompatActivity {

    private CountDownTimer timer;

    public static Intent getIntent(Context context){
        return new Intent(context, TimerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        setUpTimerOptionsButton();
        setUpPausePlayButton();
        setUpResetButton();
        setupTimeOutTimer();

    }

    private void setupTimeOutTimer() {
    }

    private void setUpResetButton() {
    }

    private void setUpPausePlayButton() {
    }

    private void setUpTimerOptionsButton() {
        Button timerOptions = findViewById(R.id.timer_options_button);
        timerOptions.setOnClickListener(view -> {
            Intent i = TimerOptions.getIntent(TimerActivity.this);
            startActivity(i);
        });
    }
}