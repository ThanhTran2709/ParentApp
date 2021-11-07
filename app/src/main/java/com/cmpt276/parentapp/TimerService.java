package com.cmpt276.parentapp;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;


public class TimerService extends Service {

    private static final String MINUTES_IN_MILLI_SECONDS_TAG = "minutes_in_milli_seconds_tag";
    private static final String ORIGINAL_TIME_IN_MILLI_SECONDS_TAG = "original_time_in_milli_seconds_tag";
    private static final long DEFAULT_MINUTES_IN_MILLI_SECONDS = 0L;
    private static final String TIMER_SERVICE_BROADCAST = "timer_service_broadcast";
    private static final String MAIN_ACTIVITY_BROADCAST = "main_activity_broadcast";
    private static final String START_SERVICE_BROADCAST = "start_service_broadcast";
    private static final String RESET_TIMER_BROADCAST = "reset_timer_broadcast";
    public static final String CHANNEL_ID = "timer_service_channel";
    private static final String PAUSE_TIMER_BROADCAST = "pause_timer_broadcast";
    private static final String PLAY_TIMER_BROADCAST = "play_timer_broadcast";
    private static final String ACTION_STOP_ALARM = "action_stop_alarm";
    private static final String STOP_ALARM_ID = "stop_alarm_id";

    private CountDownTimer timer;
    private long remainingMilliSeconds;
    private long originalTimeInMilliSeconds;
    private BroadcastReceiver mainActivityReceiver;
    private BroadcastReceiver pauseTimerReceiver;
    private BroadcastReceiver playTimerReceiver;
    private BroadcastReceiver resetTimerReceiver;

    public static Intent getIntent(Context context, long remainingMilliSeconds, long originalTimeInMilliSeconds){
        Intent i = new Intent(context, TimerService.class);
        i.putExtra(MINUTES_IN_MILLI_SECONDS_TAG, remainingMilliSeconds);
        i.putExtra(ORIGINAL_TIME_IN_MILLI_SECONDS_TAG, originalTimeInMilliSeconds);
        return i;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "started service", Toast.LENGTH_SHORT).show();
        remainingMilliSeconds = intent.getLongExtra(MINUTES_IN_MILLI_SECONDS_TAG, DEFAULT_MINUTES_IN_MILLI_SECONDS);
        originalTimeInMilliSeconds = intent.getLongExtra(ORIGINAL_TIME_IN_MILLI_SECONDS_TAG, DEFAULT_MINUTES_IN_MILLI_SECONDS);
        communicateWithMainActivity();
        setUpTimerBroadcast(remainingMilliSeconds);
        setUpPauseBroadcastReceiver();
        setUpPlayBroadcastReceiver();
        setUpResetBroadcastReceiver();
        return START_STICKY;
    }

    private void setUpResetBroadcastReceiver() {
        resetTimerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                timer.cancel();
                long milliSeconds = intent.getLongExtra(MINUTES_IN_MILLI_SECONDS_TAG, DEFAULT_MINUTES_IN_MILLI_SECONDS);
                setUpTimerBroadcast(milliSeconds);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(RESET_TIMER_BROADCAST);
        registerReceiver(resetTimerReceiver, filter);
    }

    private void setUpPauseBroadcastReceiver() {
        pauseTimerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                timer.cancel();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(PAUSE_TIMER_BROADCAST);
        registerReceiver(pauseTimerReceiver, filter);

    }

    private void setUpPlayBroadcastReceiver() {
        playTimerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long milliSeconds = intent.getLongExtra(MINUTES_IN_MILLI_SECONDS_TAG, DEFAULT_MINUTES_IN_MILLI_SECONDS);
                setUpTimerBroadcast(milliSeconds);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(PLAY_TIMER_BROADCAST);
        registerReceiver(playTimerReceiver, filter);
    }

    private void communicateWithMainActivity() {
        mainActivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent broadcastIntent = new Intent();
                broadcastIntent.putExtra(ORIGINAL_TIME_IN_MILLI_SECONDS_TAG, originalTimeInMilliSeconds);
                broadcastIntent.setAction(START_SERVICE_BROADCAST);
                sendBroadcast(broadcastIntent);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(MAIN_ACTIVITY_BROADCAST);
        registerReceiver(mainActivityReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mainActivityReceiver);
        unregisterReceiver(resetTimerReceiver);
        unregisterReceiver(playTimerReceiver);
        unregisterReceiver(pauseTimerReceiver);
        timer.cancel();
    }

    private void setUpTimerBroadcast(long milliSeconds) {

        timer = new CountDownTimer(milliSeconds, 1000) {

            @Override
            public void onTick(long l) {
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(TIMER_SERVICE_BROADCAST);
                broadcastIntent.putExtra(MINUTES_IN_MILLI_SECONDS_TAG, l);
                broadcastIntent.putExtra(ORIGINAL_TIME_IN_MILLI_SECONDS_TAG, originalTimeInMilliSeconds);
                sendBroadcast(broadcastIntent);
            }

            @Override
            public void onFinish() {
                setUpNotification();

            }
        };
        timer.start();
    }

    public void setUpNotification(){
        Intent stopAlarmIntent = TimerActivity.getIntent(this, originalTimeInMilliSeconds, true);
        stopAlarmIntent.setAction(ACTION_STOP_ALARM);
        stopAlarmIntent.putExtra(STOP_ALARM_ID, 0);
        PendingIntent stopAlarmPendingIntent =
                PendingIntent.getBroadcast(this, 0, stopAlarmIntent, PendingIntent.FLAG_IMMUTABLE);
        Intent notificationIntent = TimerActivity.getIntent(this, remainingMilliSeconds, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Timeout Timer")
        .setContentText("Time's Up!")
        .setSmallIcon(R.drawable.ic_baseline_check_circle_24)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
                .addAction(R.drawable.ic_launcher_background, "STOP", stopAlarmPendingIntent)
                .build();

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Timer Service", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
        startForeground(1, notification);

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}