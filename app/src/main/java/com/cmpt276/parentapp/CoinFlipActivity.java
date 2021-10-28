package com.cmpt276.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class CoinFlipActivity extends AppCompatActivity {

    public static Intent getIntent(Context context){
        return new Intent(context, CoinFlipActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);
    }
}