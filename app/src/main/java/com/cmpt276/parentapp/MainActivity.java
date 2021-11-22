package com.cmpt276.parentapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.parentapp.databinding.ActivityMainBinding;
import java.util.Calendar;

/**
 * Main menu for the application, hub for accessing other functions of the app.
 * */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    TimerService timerService;
    private boolean timerServiceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpWelcomeText();
        setUpAnimation();
        setUpMyChildrenButton();
        setUpCoinFlipButton();
        setUpTimerButton();
        setUpTaskListButton();
        setUpExitBtn();
    }

    private void setUpTaskListButton() {
        Button taskListButton = findViewById(R.id.task_list_button);
        taskListButton.setOnClickListener(view -> {
            Intent i = TaskActivity.getIntent(MainActivity.this);
            startActivity(i);
        });
    }

    private void setUpExitBtn() {
        Button backBtn = findViewById(R.id.exitBtn);
        backBtn.setText(R.string.exit);
        backBtn.setOnClickListener((view) -> finishAffinity());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = TimerService.getIntent(this);
        bindService(intent, connection, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        timerServiceBound = false;
    }

    private void setUpTimerButton() {
        Button timerButton = findViewById(R.id.timer_button);
        timerButton.setOnClickListener(view -> {
            Intent i;
            if(timerServiceBound){
                i = TimerActivity.getIntent(MainActivity.this, timerService.getOriginalMilliSeconds(), true);
            }
            else{
                i = TimerOptions.getIntent(MainActivity.this);
            }
            startActivity(i);
        });
    }

    private void setUpCoinFlipButton() {
        Button coinFlipButton = findViewById(R.id.flip_a_coin_button);
        coinFlipButton.setOnClickListener(view -> {
            Intent i = CoinFlipActivity.getIntent(MainActivity.this);
            startActivity(i);
        });
    }

    private void setUpMyChildrenButton() {
        Button myChildrenButton = findViewById(R.id.my_children_button);
        myChildrenButton.setOnClickListener(view -> {
            Intent i = ChildrenActivity.getIntent(MainActivity.this);
            startActivity(i);
        });
    }

    private void setUpWelcomeText(){
        TextView welcomeText = findViewById(R.id.main_menu_title);
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        if(hour < 12){
            welcomeText.setText(R.string.good_morning);
        }
        else if(hour < 18){
            welcomeText.setText(R.string.good_afternoon);
        }
        else{
            welcomeText.setText(R.string.good_evening);
        }

    }

    private void setUpAnimation(){

        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        Animation slideIn2 = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        Animation slideIn3 = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        Animation slideIn4 = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        Animation slideIn5 = AnimationUtils.loadAnimation(this, R.anim.slide_in);


        Button timerButton = findViewById(R.id.timer_button);
        Button coinFlipButton = findViewById(R.id.flip_a_coin_button);
        Button myChildrenButton = findViewById(R.id.my_children_button);
        Button exitButton = findViewById(R.id.exitBtn);
        Button taskButton = findViewById(R.id.task_list_button);

        coinFlipButton.setVisibility(View.INVISIBLE);
        timerButton.setVisibility(View.INVISIBLE);
        exitButton.setVisibility(View.INVISIBLE);
        taskButton.setVisibility(View.INVISIBLE);

        myChildrenButton.startAnimation(slideIn);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            coinFlipButton.setVisibility(View.VISIBLE);
            coinFlipButton.startAnimation(slideIn2);
        }, 200);
        handler.postDelayed(() -> {
            timerButton.setVisibility(View.VISIBLE);
            timerButton.startAnimation(slideIn3);
        }, 400);
        handler.postDelayed(() -> {
            taskButton.setVisibility(View.VISIBLE);
            taskButton.startAnimation(slideIn4);
        }, 800);
        handler.postDelayed(() -> {
            exitButton.setVisibility(View.VISIBLE);
            exitButton.startAnimation(slideIn5);
        }, 1000);
    }

    /**
     * https://developer.android.com/guide/components/bound-services#Binder
     */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            TimerService.LocalBinder binder = (TimerService.LocalBinder) service;
            timerService = binder.getService();
            timerServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            timerServiceBound = false;
        }
    };
}