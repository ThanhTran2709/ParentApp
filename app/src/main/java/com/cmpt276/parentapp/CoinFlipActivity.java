package com.cmpt276.parentapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cmpt276.model.Child;
import com.cmpt276.model.Coin;
import com.cmpt276.model.Options;

import java.util.ArrayList;

//TODO: add a way to view and clear flip history
public class CoinFlipActivity extends AppCompatActivity {

    Options options;
    private int coinChoiceIndex = Coin.HEADS;

    public static Intent getIntent(Context context){
        return new Intent(context, CoinFlipActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_flip);

        options = Options.getInstance(this);

        updateUI();

        Button buttonFlipCoin = findViewById(R.id.buttonFlipCoin);
        buttonFlipCoin.setOnClickListener(getFlipCoinListener());

        Button buttonChangeChild = findViewById(R.id.buttonChangeChild);
        buttonChangeChild.setOnClickListener(getChangeChildListener());

        RadioGroup group = findViewById(R.id.radioGroupFlipChoice);
        group.setOnCheckedChangeListener(getGroupOnCheckChangeListener());
    }

    private void updateUI() {
        TextView textViewChild = findViewById(R.id.textViewChild);

        ArrayList<Child> children = options.getChildList();
        int index = options.getChildFlipIndex(CoinFlipActivity.this);

        //if there's no children, essentially hide the text view.
        if (children.size() == 0){
            textViewChild.setText("");
        }
        else if (index < 0 || index >= children.size()){
            textViewChild.setText(R.string.no_children);
        }
        else {
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

            ArrayList<Child> children = options.getChildList();
            String[] names = new String[children.size()];
            for (int i = 0; i < names.length; i++){
                names[i] = children.get(i).getName();
            }

            builder.setItems(names, (dialogInterface, i) -> {
                options.setChildFlipIndex(CoinFlipActivity.this, i);
                updateUI();
                dialogInterface.dismiss();
            });

            builder.setTitle(R.string.change_child);

            builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
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