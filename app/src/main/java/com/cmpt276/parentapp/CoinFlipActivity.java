package com.cmpt276.parentapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.model.Child;
import com.cmpt276.model.Coin;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * Activity for flipping coin
 * */
public class CoinFlipActivity extends AppCompatActivity {

    private static final int NUMBER_OF_FLIPS = 7;

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

        setUpBackBtn();

        options = Options.getInstance(this);
        coinImage = findViewById(R.id.coin);
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

    private void setUpBackBtn() {
        Button backBtn = (Button) findViewById(R.id.backBtn_coin);
        backBtn.setText(R.string.backTxt);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //Set up coin animation
    private void animateCoin(boolean stayTheSame) {
        CoinToss coinAnimation;

        if (currentSide == R.drawable.heads) {
            coinAnimation = new CoinToss(coinImage, R.drawable.heads, R.drawable.tails, 0, 180, 0, 0, 0, 0);
        }
        else {
            coinAnimation = new CoinToss(coinImage, R.drawable.tails, R.drawable.heads, 0, 180, 0, 0, 0, 0);
        }

        if (stayTheSame) {
            coinAnimation.setRepeatCount(NUMBER_OF_FLIPS); // value + 1 must be even so the side will stay the same
        }
        else {
            coinAnimation.setRepeatCount(NUMBER_OF_FLIPS + 1); // value + 1 must be odd so the side will not stay the same
        }

        coinAnimation.setDuration(100);
        coinAnimation.setInterpolator(new AccelerateInterpolator());
        coinImage.startAnimation(coinAnimation);
    }

    //Trigger coin animation
    public void flipCoinAnimationTrigger(int coinSide) {
        if (coinSide == Coin.HEADS) {
            boolean stayTheSame = (currentSide == R.drawable.heads);
            animateCoin(stayTheSame);
            currentSide = R.drawable.heads;
        }
        if (coinSide == Coin.TAILS) {
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
            }

            MediaPlayer mp = MediaPlayer.create(this, R.raw.coinflip);
            flipCoinAnimationTrigger(coin.getFlipResult());
            mp.start();

            //cycles to the next child, looping back to the first if it reaches the end of the list
            if (children.size() > 0){
                int newChildIndex = (index + 1) % children.size();
                options.setChildFlipIndex(CoinFlipActivity.this, newChildIndex);
            }
            options.addCoinFlip(CoinFlipActivity.this, coin);

            int result = coin.getFlipResult();
            TextView tv = findViewById(R.id.textViewShowResult);
            tv.setText("");
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                switch (result){
                    case Coin.HEADS:
                        tv.setText(R.string.heads);
                        break;
                    case Coin.TAILS:
                        tv.setText(R.string.tails);
                        break;
                    default:
                        assert false;
                        break;
                }
                updateUI();
            }, 1100);

        };
    }

    private View.OnClickListener getChangeChildListener() {
        return view -> {
            Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.change_child_flip);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            FloatingActionButton cancelFab = dialog.findViewById(R.id.cancelfab3);
            cancelFab.setOnClickListener(getCancelFabListener(dialog));
            dialog.show();

            ArrayAdapter<Child> adapter = new ChildListAdapter();
            ListView listView = dialog.findViewById(R.id.listViewChildSelect);
            listView.setAdapter(adapter);
            listView.setDividerHeight(16);

            listView.setOnItemClickListener(getListViewClickListener(dialog));
        };
    }

    private View.OnClickListener getCancelFabListener(Dialog dialog) {
        return (view) -> {
            dialog.dismiss();
        };
    }

    private class ChildListAdapter extends ArrayAdapter<Child>{

        public ChildListAdapter(){
            super(CoinFlipActivity.this, R.layout.child_name_view, options.getChildList());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View gamesView = convertView;
            if (gamesView == null){
                gamesView = getLayoutInflater().inflate(R.layout.child_name_view, parent, false);
            }

            Child currentChild = options.getChildList().get(position);

            // set up game ListView item
            TextView childName = gamesView.findViewById(R.id.change_child_name);
            childName.setText(currentChild.getName());
            return gamesView;
        }

    }

    private View.OnClickListener getViewHistoryListener() {
        return (view) -> {
            Intent intent = CoinFlipHistoryActivity.getIntent(CoinFlipActivity.this);
            startActivity(intent);
        };
    }

    private AdapterView.OnItemClickListener getListViewClickListener(Dialog dialog) {
        return (adapterView, view1, i, l) -> {
            options.setChildFlipIndex(CoinFlipActivity.this, i);
            updateUI();
            dialog.dismiss();
        };
    }

    private RadioGroup.OnCheckedChangeListener getGroupOnCheckChangeListener() {
        return (RadioGroup radioGroup, int checkedId) -> {
            View radioButton = radioGroup.findViewById(checkedId);
            coinChoiceIndex = radioGroup.indexOfChild(radioButton);
        };
    }
}