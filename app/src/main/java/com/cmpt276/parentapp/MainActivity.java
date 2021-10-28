package com.cmpt276.parentapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
}