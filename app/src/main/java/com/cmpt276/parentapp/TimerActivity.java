package com.cmpt276.parentapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimerActivity extends AppCompatActivity {

    private CountDownTimer timer;
    private static final String MINUTES_TAG = "minutes tag";
    private static final int DEFAULT_MINUTES = 5;
    private int minutes;
    TextView remainingTime;

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
        remainingTime = findViewById(R.id.time_text);
        timer = new CountDownTimer(minutes* 60000L, 1000) {
            @Override
            public void onTick(long l) {
                long remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(l);
                long remainingSeconds =  TimeUnit.MILLISECONDS.toSeconds(l) -
                                TimeUnit.MINUTES.toSeconds(remainingMinutes);
                String remainingMinutesText = remainingMinutes + "";
                String remainingSecondsText = remainingSeconds + "";
                if(remainingMinutes < 10){
                    remainingMinutesText = "0" + remainingMinutesText;
                }
                if(remainingSeconds < 10){
                    remainingSecondsText = "0" + remainingSecondsText;
                }
                remainingTime.setText(remainingMinutesText + " : " + remainingSecondsText);
            }

            @Override
            public void onFinish() {
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                long[] pattern = {0, 100, 100, 100};
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, 1));
                final MediaPlayer mp = MediaPlayer.create(TimerActivity.this, R.raw.alarm_sound);
                mp.setLooping(true);
                mp.start();
                new AlertDialog.Builder(TimerActivity.this).setTitle("Times Up!")
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                vibrator.cancel();
                                mp.stop();
                                resetTimer();
                            }
                        }).show();
            }
        };

        timer.start();

    }

    private void resetTimer(){
        if(minutes < 10){
            remainingTime.setText("0" + minutes + " : " + "00");
        }
        else{
            remainingTime.setText(minutes + " : " + "00");
        }
    }



    private void setUpResetButton() {
    }

    private void setUpPausePlayButton() {
    }


}