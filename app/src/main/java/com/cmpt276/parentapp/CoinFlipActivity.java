package com.cmpt276.parentapp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.model.Child;
import com.cmpt276.model.Coin;
import com.cmpt276.model.CoinToss;
import com.cmpt276.model.Options;

import java.util.ArrayList;

//TODO: add a way to view and clear flip history
public class CoinFlipActivity extends AppCompatActivity {

    Options options;
    private int coinChoiceIndex = Coin.HEADS;
    private ImageView coinImage;

    private int currentSide = R.drawable.heads;

    public static Intent getIntent(Context context){
        return new Intent(context, CoinFlipActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);

        options = Options.getInstance(this);
        coinImage = (ImageView) findViewById(R.id.coin);
        coinImage.setImageResource(R.drawable.heads);

        updateUI();

        Button buttonFlipCoin = findViewById(R.id.buttonFlipCoin);
        buttonFlipCoin.setOnClickListener(getFlipCoinListener());

        Button buttonChangeChild = findViewById(R.id.buttonChangeChild);
        buttonChangeChild.setOnClickListener(getChangeChildListener());

        Button buttonViewHistory = findViewById(R.id.buttonViewFlipHistory);
        buttonViewHistory.setOnClickListener(getViewHistoryListener());

        RadioGroup group = findViewById(R.id.radioGroupFlipChoice);
        group.setOnCheckedChangeListener(getGroupOnCheckChangeListener());
    }

    //Set up coin animation
    private void animateCoin(boolean stayTheSame) {
        CoinToss coinAnimation;

        if (currentSide == R.drawable.heads)
            coinAnimation = new CoinToss(coinImage, R.drawable.heads, R.drawable.tails, 0, 180, 0, 0, 0, 0);
        else
            coinAnimation = new CoinToss(coinImage, R.drawable.tails, R.drawable.heads, 0, 180, 0, 0, 0, 0);

        if (stayTheSame)
            coinAnimation.setRepeatCount(7); // value + 1 must be even so the side will stay the same
        else
            coinAnimation.setRepeatCount(8); // value + 1 must be odd so the side will not stay the same

        coinAnimation.setDuration(100);
        coinAnimation.setInterpolator(new AccelerateInterpolator());
        coinImage.startAnimation(coinAnimation);
    }

    //Trigger coin animation
    public void flipCoinAnimationTrigger(int coinSide) {
        if (coinSide == 0) {  //Heads
            boolean stayTheSame = (currentSide == R.drawable.heads);
            animateCoin(stayTheSame);
            currentSide = R.drawable.heads;
        }
        if (coinSide == 1) {  //Tails
            boolean stayTheSame = (currentSide == R.drawable.tails);
            animateCoin(stayTheSame);
            currentSide = R.drawable.tails;
        }
    }


    private void updateUI() {
        TextView textViewChild = findViewById(R.id.textViewChild);

        ArrayList<Child> children = options.getChildList();
        int index = options.getChildFlipIndex(CoinFlipActivity.this);

        //if there's no children, essentially hide the text view.
        if (children.size() == 0){
            textViewChild.setText("");
        }
        else {
            //default index to 0 if the index is out of bounds, either by deletion of a child or some other means
            if (index < 0 || index >= children.size()){
                index = 0;
            }
            textViewChild.setText(getString(R.string.current_child, children.get(index).getName()));
        }
    }

    private View.OnClickListener getFlipCoinListener() {
        return view -> {
            ArrayList<Child> children = options.getChildList();
            int index = options.getChildFlipIndex(CoinFlipActivity.this);

            Coin coin;

            if (children.size() == 0){
                coin = new Coin();
            }
            else {
                //if index is out of bounds because of children array resizing, default to the first child added
                if (index < 0 || index >= children.size()){
                    index = 0;
                }
                int flipChoice;
                switch (coinChoiceIndex){
                    case Coin.HEADS:
                        flipChoice = Coin.HEADS;
                        break;
                    case Coin.TAILS:
                        flipChoice = Coin.TAILS;
                        break;
                    default:
                        throw new IllegalStateException("Cannot have selection that is neither heads nor tails.");
                }
                coin = new Coin(children.get(index), flipChoice);
                MediaPlayer mp = MediaPlayer.create(this, R.raw.coinflip);
                flipCoinAnimationTrigger(coin.getFlipResult());
                mp.start();
            }

            //cycles to the next child, looping back to the first if it reaches the end of the list
            if (children.size() > 0){
                int newChildIndex = (index + 1) % children.size();
                options.setChildFlipIndex(CoinFlipActivity.this, newChildIndex);
            }
            options.addCoinFlip(CoinFlipActivity.this, coin);

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

            updateUI();
        };
    }

    private View.OnClickListener getChangeChildListener() {
        return view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(CoinFlipActivity.this);

            View changeChildView = CoinFlipActivity.this.getLayoutInflater().inflate(R.layout.change_child_flip, null);
            builder.setView(changeChildView);

            builder.setTitle(R.string.change_child);

            builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            ArrayList<Child> children = options.getChildList();
            String[] names = new String[children.size()];
            for (int i = 0; i < names.length; i++){
                names[i] = children.get(i).getName();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(CoinFlipActivity.this, R.layout.children_list, names);

            ListView listView = changeChildView.findViewById(R.id.listViewChildSelect);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((adapterView, view1, i, l) -> {
                options.setChildFlipIndex(CoinFlipActivity.this, i);
                updateUI();
                dialog.dismiss();
            });
        };
    }

    private View.OnClickListener getViewHistoryListener() {
        return view -> {
            Intent intent = CoinFlipHistoryActivity.getIntent(CoinFlipActivity.this);
            startActivity(intent);
        };
    }

    private AdapterView.OnItemClickListener getListViewClickListener() {
        return (adapterView, view, i, l) -> {
            options.setChildFlipIndex(CoinFlipActivity.this, i);
        };
    }

    private RadioGroup.OnCheckedChangeListener getGroupOnCheckChangeListener() {
        return (RadioGroup radioGroup, int checkedId) -> {
            View radioButton = radioGroup.findViewById(checkedId);
            coinChoiceIndex = radioGroup.indexOfChild(radioButton);
        };
    }
}