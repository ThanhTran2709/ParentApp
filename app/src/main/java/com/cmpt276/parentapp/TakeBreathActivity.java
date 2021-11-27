package com.cmpt276.parentapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for the deep breathing exercise as described in User Stories.
 * */
public class TakeBreathActivity extends AppCompatActivity {

	private State currentState = new IdleState();

	public void setState(State newState) {
		currentState.handleExit();
		currentState = newState;
		currentState.handleEnter();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_breath);

		setUpBackButton();
		setUpBreatheButton();
	}

	public static Intent getIntent(Context context) {
		return new Intent(context, TakeBreathActivity.class);
	}

	private void setUpBreatheButton() {
		BreatheButton breatheButton = findViewById(R.id.buttonBreathe);
		breatheButton.setOnTouchListener((view, motionEvent) -> {
			switch (motionEvent.getAction()){
				case MotionEvent.ACTION_DOWN:
					currentState.onHoldButton();
					return true;
				case MotionEvent.ACTION_UP:
					view.performClick();
					currentState.onReleaseButton();
					return false;
				default:
					return false;
			}
		});
		breatheButton.performClick();
	}

	private void setUpBackButton() {
		Button backButton = findViewById(R.id.buttonBackTakeBreath);
		backButton.setOnClickListener(view -> {
			TakeBreathActivity.this.finish();
		});
	}



	//////////////////////////////////
	//            STATES            //
	//////////////////////////////////

	private abstract class State {

		//methods meant to be overridden, default implementation is empty.
		void handleEnter() {}

		void handleExit() {}

		void onHoldButton() {}

		void onReleaseButton() {}

	}

	private class ReadyState extends State {

		@Override
		void handleEnter() {

		}

		@Override
		void handleExit() {

		}

		@Override
		void onHoldButton() {

		}

		@Override
		void onReleaseButton() {

		}
	}

	private class InhaleState extends State {

		@Override
		void handleEnter() {

		}

		@Override
		void handleExit() {

		}

		@Override
		void onHoldButton() {

		}

		@Override
		void onReleaseButton() {

		}
	}

	private class InhalingState extends State {

		@Override
		void handleEnter() {

		}

		@Override
		void handleExit() {

		}

		@Override
		void onHoldButton() {

		}

		@Override
		void onReleaseButton() {

		}
	}

	private class InhaleReleaseState extends State {

		@Override
		void handleEnter() {

		}

		@Override
		void handleExit() {

		}

		@Override
		void onHoldButton() {

		}

		@Override
		void onReleaseButton() {

		}
	}

	private class InhaleReleaseHelpState extends State {

		@Override
		void handleEnter() {

		}

		@Override
		void handleExit() {

		}

		@Override
		void onHoldButton() {

		}

		@Override
		void onReleaseButton() {

		}
	}

	private class InhaleDoneState extends State {

		@Override
		void handleEnter() {

		}

		@Override
		void handleExit() {

		}

		@Override
		void onHoldButton() {

		}

		@Override
		void onReleaseButton() {

		}
	}

	private class ExhaleState extends State {

		@Override
		void handleEnter() {

		}

		@Override
		void handleExit() {

		}

		@Override
		void onHoldButton() {

		}

		@Override
		void onReleaseButton() {

		}
	}

	private class ExhaleReleaseState extends State {

		@Override
		void handleEnter() {

		}

		@Override
		void handleExit() {

		}

		@Override
		void onHoldButton() {

		}

		@Override
		void onReleaseButton() {

		}
	}

	private class ExhaleDoneState extends State {

		@Override
		void handleEnter() {

		}

		@Override
		void handleExit() {

		}

		@Override
		void onHoldButton() {

		}

		@Override
		void onReleaseButton() {

		}
	}

	private class PromptMoreState extends State {

		@Override
		void handleEnter() {

		}

		@Override
		void handleExit() {

		}

		@Override
		void onHoldButton() {

		}

		@Override
		void onReleaseButton() {

		}
	}

	private class FinishState extends State {

		@Override
		void handleEnter() {

		}

		@Override
		void handleExit() {

		}

		@Override
		void onHoldButton() {

		}

		@Override
		void onReleaseButton() {

		}
	}

	private class IdleState extends State {}
}