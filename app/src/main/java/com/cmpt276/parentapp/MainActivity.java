package com.cmpt276.parentapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.parentapp.databinding.ActivityMainBinding;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String MAIN_ACTIVITY_BROADCAST = "main_activity_broadcast";
    private static final String START_SERVICE_BROADCAST = "start_service_broadcast";
    private ActivityMainBinding binding;
    private boolean isStartService = false;
    private BroadcastReceiver startServiceReceiver;
    private static final String ORIGINAL_TIME_IN_MILLI_SECONDS_TAG = "original_time_in_milli_seconds_tag";
    private static final long DEFAULT_MINUTES_IN_MILLI_SECONDS = 0L;
    private long originalTimeInMilliSeconds;

    public static Intent getIntent(Context context) {
        Intent i = new Intent(context, MainActivity.class);
        return i;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        communicateWithTimerService();
        setContentView(binding.getRoot());
        setUpWelcomeText();
        setUpAnimation();
        setUpMyChildrenButton();
        setUpCoinFlipButton();
        setUpTimerButton();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(startServiceReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        communicateWithTimerService();
    }

    private void communicateWithTimerService() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MAIN_ACTIVITY_BROADCAST);
        sendBroadcast(broadcastIntent);

        startServiceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isStartService = true;
                originalTimeInMilliSeconds = intent.getLongExtra(ORIGINAL_TIME_IN_MILLI_SECONDS_TAG,DEFAULT_MINUTES_IN_MILLI_SECONDS);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(START_SERVICE_BROADCAST);
        registerReceiver(startServiceReceiver, filter);
    }


    private void setUpTimerButton() {
        Button timerButton = findViewById(R.id.timer_button);
        timerButton.setOnClickListener(view -> {
            if(isStartService){
                Intent i = TimerActivity.getIntent(MainActivity.this, originalTimeInMilliSeconds, true);
                startActivity(i);
            }
            else{
                Intent i = TimerOptions.getIntent(MainActivity.this);
                startActivity(i);
            }
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
            welcomeText.setText("Good Morning!");
        }
        else if(hour < 18){
            welcomeText.setText("Good Afternoon!");
        }
        else{
            welcomeText.setText("Good Evening!");
        }

    }

    private void setUpAnimation(){
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        Animation slideIn2 = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        Animation slideIn3 = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        Button timerButton = findViewById(R.id.timer_button);
        Button coinFlipButton = findViewById(R.id.flip_a_coin_button);
        Button myChildrenButton = findViewById(R.id.my_children_button);
        coinFlipButton.setVisibility(View.INVISIBLE);
        timerButton.setVisibility(View.INVISIBLE);
        myChildrenButton.startAnimation(slideIn);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            coinFlipButton.setVisibility(View.VISIBLE);
            coinFlipButton.startAnimation(slideIn2);
        }, 500);
        handler.postDelayed(() -> {
            timerButton.setVisibility(View.VISIBLE);
            timerButton.startAnimation(slideIn3);
        }, 1300);
    }
}