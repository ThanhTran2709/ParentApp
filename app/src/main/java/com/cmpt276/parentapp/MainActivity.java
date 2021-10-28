package com.cmpt276.parentapp;

import android.os.Bundle;

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
    }

    private void setUpCoinFlipButton() {
    }

    private void setUpMyChildrenButton() {
    }
}