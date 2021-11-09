package com.cmpt276.parentapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.concurrent.TimeUnit;

/**
 * Service to keep the timer running while using other apps
 */
public class TimerService extends Service {

    private static final String ORIGINAL_TIME_IN_MILLI_SECONDS_TAG = "original_time_in_milli_seconds_tag";
    private static final long DEFAULT_MINUTES_IN_MILLI_SECONDS = 0L;
    private static final String TIMER_SERVICE_BROADCAST = "timer_service_broadcast";
    private static final String CHANNEL_ID = "timer_service_channel";
    private static final int NOTIFICATION_ID = 1;
    private static final int REPEAT_ONCE = 1;
    private static final String STOP_ALARM_BROADCAST = "stop_alarm_broadcast";

    private CountDownTimer timer;
    private long remainingMilliSeconds;
    private long originalTimeInMilliSeconds;
    private boolean isPaused;
    private boolean isFinish;
    private Vibrator vibrator;
    private MediaPlayer mp;

    /**
     * https://developer.android.com/guide/components/bound-services#Binder
     */
    private final IBinder binder = new LocalBinder();

    public static Intent getIntent(Context context, long originalTimeInMilliSeconds) {
        Intent i = new Intent(context, TimerService.class);
        i.putExtra(ORIGINAL_TIME_IN_MILLI_SECONDS_TAG, originalTimeInMilliSeconds);
        return i;
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, TimerService.class);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        setUpNotificationChannel();

        originalTimeInMilliSeconds = intent.getLongExtra(ORIGINAL_TIME_IN_MILLI_SECONDS_TAG, DEFAULT_MINUTES_IN_MILLI_SECONDS);
        remainingMilliSeconds = originalTimeInMilliSeconds;

        setUpTimerBroadcast(originalTimeInMilliSeconds);

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private void setUpTimerBroadcast(long milliSeconds) {
        isPaused = false;
        isFinish = false;
        timer = new CountDownTimer(milliSeconds, 1000) {

            @Override
            public void onTick(long l) {

                remainingMilliSeconds = l;

                setUpNotification(getTimeString());

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(TIMER_SERVICE_BROADCAST);
                sendBroadcast(broadcastIntent);

            }

            @Override
            public void onFinish() {
                isFinish = true;
                isPaused = true;
                setUpAlarmNotification();

            }
        };
        timer.start();
    }

    public String getTimeString() {
        int remainingMinutes = (int) TimeUnit.MILLISECONDS.toMinutes(remainingMilliSeconds);
        int remainingSeconds = (int) TimeUnit.MILLISECONDS.toSeconds(remainingMilliSeconds) -
                (int) TimeUnit.MINUTES.toSeconds(remainingMinutes);

        return String.format("%02d : %02d", remainingMinutes, remainingSeconds);
    }

    /**
     * https://developer.android.com/training/notify-user/build-notification
     */
    private void setUpNotification(String timeString) {

        Intent notificationIntent = TimerActivity.getIntent(this, originalTimeInMilliSeconds, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Timeout Timer")
                .setContentText(timeString)
                .setSmallIcon(R.drawable.timer_icon)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSilent(true)
                .build();

        startForeground(NOTIFICATION_ID, notification);

    }

    private void setUpAlarmNotification() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] pattern = {0, 100, 100, 100};
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, REPEAT_ONCE));

        mp = MediaPlayer.create(TimerService.this, R.raw.alarm_sound);
        mp.setLooping(true);
        mp.start();


        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(STOP_ALARM_BROADCAST);
        sendBroadcast(broadcastIntent);


        setUpNotification("TIMES UP! Tap to stop the alarm");
    }

    public void stopSoundAndVibration() {
        if (vibrator != null) {
            vibrator.cancel();
        }
        if (mp != null) {
            mp.stop();
        }
    }

    /**
     * https://developer.android.com/training/notify-user/build-notification
     */
    private void setUpNotificationChannel() {

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Timer Service", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);

    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void pauseTimer() {
        timer.cancel();
        isPaused = true;
    }

    public void playTimer() {
        setUpTimerBroadcast(remainingMilliSeconds);
    }

    public String getOriginalTimeString() {
        long remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(originalTimeInMilliSeconds);
        long remainingSeconds = TimeUnit.MILLISECONDS.toSeconds(originalTimeInMilliSeconds) -
                TimeUnit.MINUTES.toSeconds(remainingMinutes);

        return String.format("%02d : %02d", remainingMinutes, remainingSeconds);
    }

    public long getOriginalMilliSeconds() {
        return originalTimeInMilliSeconds;
    }

    /**
     * https://developer.android.com/guide/components/bound-services#Binder
     */
    public class LocalBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }

    /**
     * https://developer.android.com/guide/components/bound-services#Binder
     */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}