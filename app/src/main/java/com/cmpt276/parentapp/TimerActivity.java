package com.cmpt276.parentapp;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class TimerActivity extends AppCompatActivity{

    private static final String MINUTES_IN_MILLI_SECONDS_TAG = "minutes_in_milli_seconds_tag";
    private static final String TIMER_SERVICE_BROADCAST = "timer_service_broadcast";
    private static final String PAUSE_TIMER_BROADCAST = "pause_timer_broadcast";
    private static final String PLAY_TIMER_BROADCAST = "play_timer_broadcast";
    private static final String SERVICE_RUNNING_FLAG = "service_running_flag";
    private static final String RESET_TIMER_BROADCAST = "reset_timer_broadcast";
    private static final long DEFAULT_MINUTES_IN_MILLI_SECONDS = 0L;
    private static final int TEN = 10;
    private boolean isPaused;
    TextView remainingTime;
    private long minutesInMilliSeconds;
    private long remainingMilliSeconds;
    private boolean isServiceRunning;
    BroadcastReceiver timerReceiver;
    private Intent serviceIntent;

    public static Intent getIntent(Context context, long minutesInMilliSeconds, boolean isServiceRunning){
        Intent i = new Intent(context, TimerActivity.class);
        i.putExtra(MINUTES_IN_MILLI_SECONDS_TAG, minutesInMilliSeconds);
        i.putExtra(SERVICE_RUNNING_FLAG, isServiceRunning);
        return i;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isPaused = false;
        setContentView(R.layout.activity_timer);
        minutesInMilliSeconds = this.getIntent().getLongExtra(MINUTES_IN_MILLI_SECONDS_TAG, DEFAULT_MINUTES_IN_MILLI_SECONDS);
        remainingMilliSeconds = minutesInMilliSeconds;
        isServiceRunning = this.getIntent().getBooleanExtra(SERVICE_RUNNING_FLAG, false);
        setUpServiceIntent();
        setUpPausePlayButton();
        setUpResetButton();
        Toast.makeText(this, "You chose " + minutesInMilliSeconds + " minutes", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setupBroadCastReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timerReceiver);
    }

    private void setUpServiceIntent() {
        serviceIntent = TimerService.getIntent(this, remainingMilliSeconds, minutesInMilliSeconds);
        if(!isServiceRunning){
            startService(serviceIntent);
            isServiceRunning = true;
        }
    }

    private void setupBroadCastReceiver() {
        remainingTime = findViewById(R.id.time_text);
        timerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                remainingMilliSeconds = intent.getLongExtra(MINUTES_IN_MILLI_SECONDS_TAG, DEFAULT_MINUTES_IN_MILLI_SECONDS);
                long remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(remainingMilliSeconds);
                long remainingSeconds =  TimeUnit.MILLISECONDS.toSeconds(remainingMilliSeconds) -
                        TimeUnit.MINUTES.toSeconds(remainingMinutes);
                String remainingMinutesText = remainingMinutes + "";
                String remainingSecondsText = remainingSeconds + "";
                if(remainingMinutes < TEN){
                    remainingMinutesText = "0" + remainingMinutesText;
                }
                if(remainingSeconds < TEN){
                    remainingSecondsText = "0" + remainingSecondsText;
                }
                remainingTime.setText(remainingMinutesText + " : " + remainingSecondsText);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(TIMER_SERVICE_BROADCAST);
        registerReceiver(timerReceiver, filter);
    }

    private void resetTimer(){
        isPaused = false;
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(RESET_TIMER_BROADCAST);
        broadcastIntent.putExtra(MINUTES_IN_MILLI_SECONDS_TAG, minutesInMilliSeconds);
        sendBroadcast(broadcastIntent);
    }

    private void setUpResetButton() {

        Button resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(view -> {
            Button pausePlayButton = findViewById(R.id.pause_play);
            pausePlayButton.setText("Pause");
            resetTimer();
        });
    }

    private void setUpPausePlayButton() {

        Button pausePlayButton = findViewById(R.id.pause_play);
        pausePlayButton.setOnClickListener(view -> {
            if(isPaused) {
                isPaused = false;
                pausePlayButton.setText("Pause");
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(PLAY_TIMER_BROADCAST);
                broadcastIntent.putExtra(MINUTES_IN_MILLI_SECONDS_TAG, remainingMilliSeconds);
                sendBroadcast(broadcastIntent);
            }
            else{
                isPaused = true;
                pausePlayButton.setText("Play");
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(PAUSE_TIMER_BROADCAST);
                sendBroadcast(broadcastIntent);
            }
        });
    }

}