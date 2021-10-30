package com.cmpt276.parentapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.model.Coin;

public class CoinFlipActivity extends AppCompatActivity {

    public static Intent getIntent(Context context){
        return new Intent(context, CoinFlipActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);

        TextView childSelection = findViewById(R.id.textViewChild);

        Button buttonFlipCoin = findViewById(R.id.buttonFlipCoin);
        buttonFlipCoin.setOnClickListener(getFlipCoinListener());
    }

    private View.OnClickListener getFlipCoinListener() {
        return view -> {
            //get current child to be chosen from sharedpreferences
            //flip coin and animate it
            Coin coin = new Coin();

            //TODO: show result of coin flip through animated coin rather than a textview
            int result = coin.getFlipResult();
            TextView tv = findViewById(R.id.textViewShowResult);
            switch (result){
                case Coin.HEADS:
                    tv.setText("HEADS");
                    break;
                case Coin.TAILS:
                    tv.setText("TAILS");
                    break;
                default:
                    assert false;
                    break;
            }


        };
    }
}