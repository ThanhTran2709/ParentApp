package com.cmpt276.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;

/**
 * Contains the UI for the timer
 */
public class TimerActivity extends AppCompatActivity {

    private static final String ORIGINAL_TIME_IN_MILLI_SECONDS_TAG = "original_time_in_milli_seconds_tag";
    private static final String TIMER_SERVICE_BROADCAST = "timer_service_broadcast";
    private static final String SERVICE_RUNNING_FLAG = "service_running_flag";
    private static final long DEFAULT_MINUTES_IN_MILLI_SECONDS = 0L;

    TextView remainingTime;
    BroadcastReceiver timerReceiver;
    private Intent serviceIntent;
    private long originalTimeInMilliSeconds;
    private boolean isServiceRunning;

    TimerService timerService;
    private boolean timerServiceBound = false;


    public static Intent getIntent(Context context, long minutesInMilliSeconds) {
        return TimerActivity.getIntent(context, minutesInMilliSeconds, false);
    }

    public static Intent getIntent(Context context,
                                   long originalTimeInMilliSeconds,
                                   boolean isServiceRunning) {

        Intent i = new Intent(context, TimerActivity.class);
        i.putExtra(ORIGINAL_TIME_IN_MILLI_SECONDS_TAG, originalTimeInMilliSeconds);
        i.putExtra(SERVICE_RUNNING_FLAG, isServiceRunning);

        return i;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        originalTimeInMilliSeconds = this.getIntent().getLongExtra(ORIGINAL_TIME_IN_MILLI_SECONDS_TAG, DEFAULT_MINUTES_IN_MILLI_SECONDS);
        isServiceRunning = this.getIntent().getBooleanExtra(SERVICE_RUNNING_FLAG, false);
        setUpPausePlayButton();
        setUpResetButton();
        setUpNewTimerButton();

    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpStartService();
        setupBroadCastReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(timerReceiver);
        unbindService(connection);
        timerServiceBound = false;
    }

    private void setUpStartService() {
        serviceIntent = TimerService.getIntent(this, originalTimeInMilliSeconds);

        if (!isServiceRunning) {
            startService(serviceIntent);
            isServiceRunning = true;
        }

        bindService(serviceIntent, connection, 0);
    }

    private void setupBroadCastReceiver() {
        remainingTime = findViewById(R.id.time_text);

        timerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateTimerLabel();
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(TIMER_SERVICE_BROADCAST);
        registerReceiver(timerReceiver, filter);
    }

    private void updateTimerLabel() {
        TextView timeText = findViewById(R.id.time_text);
        timeText.setText(timerService.getTimeString());
    }

    private void resetTimer() {

        String originalTime = timerService.getOriginalTimeString();

        stopService(serviceIntent);
        isServiceRunning = false;
        timerServiceBound = false;

        TextView timeText = findViewById(R.id.time_text);
        timeText.setText(originalTime);
    }

    private void setUpNewTimerButton() {
        Button editTimeButton = findViewById(R.id.new_timer_button);
        editTimeButton.setOnClickListener(view -> {
            if(isServiceRunning){
                stopService(serviceIntent);
                isServiceRunning = false;
            }
            Intent i = TimerOptions.getIntent(this);
            startActivity(i);
            finish();
        });
    }

    private void setUpResetButton() {

        Button resetButton = findViewById(R.id.reset_button);

        resetButton.setOnClickListener(view -> {
            resetTimer();
            updatePausePlayButtonText();
        });
    }

    private void updatePausePlayButtonText() {
        Button pausePlayButton = findViewById(R.id.pause_play);

        if ((!timerServiceBound) || timerService.isPaused()) {
            pausePlayButton.setText(R.string.play_button_text);

        } else {
            pausePlayButton.setText(R.string.pause_button_text);
        }

    }

    private void setUpPausePlayButton() {
        Button pausePlayButton = findViewById(R.id.pause_play);

        pausePlayButton.setOnClickListener(view -> {
            if (!timerServiceBound) {
                setUpStartService();
            }
            else {
                if (timerService.isPaused()) {
                    timerService.playTimer();
                } else {
                    timerService.pauseTimer();
                    updateTimerLabel();
                }
            }
            updatePausePlayButtonText();
        });

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

            updateTimerLabel();
            updatePausePlayButtonText();

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            timerServiceBound = false;
        }
    };

}