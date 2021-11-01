package com.cmpt276.parentapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.parentapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpAnimation();
        setUpMyChildrenButton();
        setUpCoinFlipButton();
        setUpTimerButton();
    }

    private void setUpTimerButton() {
        Button timerButton = findViewById(R.id.timer_button);
        timerButton.setOnClickListener(view -> {
            Intent i = TimerActivity.getIntent(MainActivity.this);
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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                coinFlipButton.setVisibility(View.VISIBLE);
                coinFlipButton.startAnimation(slideIn2);
            }
        }, 500);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timerButton.setVisibility(View.VISIBLE);
                timerButton.startAnimation(slideIn3);
            }
        }, 1300);


    }
}