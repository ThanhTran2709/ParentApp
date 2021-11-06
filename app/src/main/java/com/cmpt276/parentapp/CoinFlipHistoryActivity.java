package com.cmpt276.parentapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cmpt276.model.Child;
import com.cmpt276.model.Coin;
import com.cmpt276.model.Options;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
			if(options.getFlipHistory(this).size() == 0) {
				Toast.makeText(this, "There are no coin flip history to clear", Toast.LENGTH_SHORT).show();
			}
			else {
				ClearHistoryDialog alert = new ClearHistoryDialog();
				alert.showDialog(CoinFlipHistoryActivity.this);
			}
		};
	}

	public class ClearHistoryDialog {

		public void showDialog(Activity activity) {
			final Dialog dialog = new Dialog(activity);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setCancelable(false);
			dialog.setContentView(R.layout.clear_dialog);
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

			FloatingActionButton cancelFab = dialog.findViewById(R.id.cancelFab);
			cancelFab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
			});

			FloatingActionButton addFab = dialog.findViewById(R.id.okFab);
			addFab.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					options.clearCoinFlips(CoinFlipHistoryActivity.this);
					CoinFlipHistoryActivity.this.finish();
				}
			});

			dialog.show();
		}
	}

	public static Intent getIntent(Context context){
		return new Intent(context, CoinFlipHistoryActivity.class);
	}
}