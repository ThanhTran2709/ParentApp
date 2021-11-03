package com.cmpt276.parentapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cmpt276.model.Coin;
import com.cmpt276.model.Options;

import java.util.ArrayList;

public class CoinFlipHistoryActivity extends AppCompatActivity {

	Options options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coin_flip_history);

		 options = Options.getInstance(this);

		ArrayList<Coin> coinFlips = options.getFlipHistory(this);

		RecyclerView rv = findViewById(R.id.recyclerViewFlipHistory);
		rv.setLayoutManager(new LinearLayoutManager(this));
		FlipHistoryAdapter adapter = new FlipHistoryAdapter(this, coinFlips);
		rv.setAdapter(adapter);

		Button buttonClear = findViewById(R.id.buttonClearFlipHistory);
		buttonClear.setOnClickListener(getClearListener());
	}

	private View.OnClickListener getClearListener() {
		return view -> {
			AlertDialog.Builder builder = new AlertDialog.Builder(CoinFlipHistoryActivity.this);

			builder.setTitle(R.string.confirm_reset);

			builder.setPositiveButton(R.string.reset, (dialogInterface, i) -> {
				options.clearCoinFlips(CoinFlipHistoryActivity.this);
				CoinFlipHistoryActivity.this.finish();
			});

			builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
				dialogInterface.dismiss();
			});

			AlertDialog dialog = builder.create();
			dialog.show();
		};
	}

	public static Intent getIntent(Context context){
		return new Intent(context, CoinFlipHistoryActivity.class);
	}
}