package com.cmpt276.parentapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for the deep breathing exercise as described in User Stories.
 * */
public class TakeBreathActivity extends AppCompatActivity {
	//TODO: after implementation is finished, replace println() debugging statements with exceptions

	//units are in milliseconds
	private static final long TIME_BREATHE_IN = 3000L;
	private static final long TIME_BREATHE_IN_HELP = 10000L;
	private static final long TIME_BREATHE_OUT = 3000L;
	private static final long TIME_BREATHE_OUT_STOP_ANIMATION = 10000L;
	private static final long TIME_BREATHING_DELAY = 1000L; //subject to change depending on needs

	private State currentState = new IdleState();
	private int numberOfBreaths;

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
		setUpStartButton();
		setUpBreatheButton();

		setState(new ReadyState());

		//TODO: add way to change number of breaths, and pull this number from options
		numberOfBreaths = 10; //hardcoded
	}

	public static Intent getIntent(Context context) {
		return new Intent(context, TakeBreathActivity.class);
	}

	private void setUpStartButton() {
		Button startButton = findViewById(R.id.button_start_breathing);
		startButton.setOnClickListener(view -> {
			State inhaleState = new InhaleState();
			setState(inhaleState);
		});
	}

	private void setUpBreatheButton() {
		BreatheButton breatheButton = findViewById(R.id.button_breathe);
		breatheButton.setOnTouchListener((view, motionEvent) -> {
			//OnTouchListener's onTouch method returns true if the event was handled and false if not.
			//ACTION_DOWN is when the user initially presses on the view, and ACTION_UP is when the user
			//releases the view.
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
			Button startButton = findViewById(R.id.button_start_breathing);
			startButton.setVisibility(View.VISIBLE);
		}

		@Override
		void handleExit() {
			Button startButton = findViewById(R.id.button_start_breathing);
			startButton.setVisibility(View.GONE);
		}

		@Override
		void onHoldButton() {
			//do nothing
		}

		@Override
		void onReleaseButton() {
			//do nothing
		}
	}

	private class InhaleState extends State {

		@Override
		void handleEnter() {
			Button breatheButton = findViewById(R.id.button_breathe);
			breatheButton.setText(R.string.breathe_in);
		}

		@Override
		void handleExit() {
			//do nothing
		}

		@Override
		void onHoldButton() {
			State inhalingState = new InhalingState();
			setState(inhalingState);
		}

		@Override
		void onReleaseButton() {
			//do nothing
			System.out.println("INVALID ACTION IN INHALE STATE");
		}
	}

	private class InhalingState extends State {

		@Override
		void handleEnter() {
			//start animation and sound
			Handler handler = new Handler();
			handler.postDelayed(() -> {
				//Check if we're still in the same instance of the state after the allotted time.
				//This prevents situations such as the user pressing the button, releasing after one
				//second, then holding it, which results in the state changing too early.
				if (currentState == InhalingState.this){
					State inhaleReleaseState = new InhaleReleaseState();
					setState(inhaleReleaseState);
				}
			}, TIME_BREATHE_IN);
		}

		@Override
		void handleExit() {
			//do nothing
		}

		@Override
		void onHoldButton() {
			//do nothing
			System.out.println("INVALID ACTION IN INHALING STATE");
		}

		@Override
		void onReleaseButton() {
			State inhaleState = new InhaleState();
			setState(inhaleState);
		}
	}

	private class InhaleReleaseState extends State {

		@Override
		void handleEnter() {
			//TODO: ask Brian what exactly he means by "state 'out'" in the flowchart
			Button breatheButton = findViewById(R.id.button_breathe);
			breatheButton.setText(R.string.breathe_out);
		}

		@Override
		void handleExit() {

		}

		@Override
		void onHoldButton() {
			//do nothing
			System.out.println("INVALID ACTION IN INHALE RELEASE STATE");
		}

		@Override
		void onReleaseButton() {
			State inhaleDoneState = new InhaleDoneState();
			setState(inhaleDoneState);
		}
	}

	private class InhaleReleaseHelpState extends State {
		//TODO: add help message telling user to release the button.

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
			//TODO: ask Brian if this state is necessary, and if so for how long should we pause between breathing in and out

			//stop animation and sound

			Handler handler = new Handler();
			handler.postDelayed(() -> {
				State exhaleState = new ExhaleState();
				setState(exhaleState);
			}, TIME_BREATHING_DELAY);
		}

		@Override
		void handleExit() {
			//do nothing
		}

		@Override
		void onHoldButton() {
			//TODO: ask Brian what should be done if the user presses the button between now and the next inhale
		}

		@Override
		void onReleaseButton() {
			//do nothing
		}
	}

	private class ExhaleState extends State {

		@Override
		void handleEnter() {
			//play exhaling animation/sound
			Handler handler = new Handler();
			handler.postDelayed(() -> {
				State exhaleReleaseState = new ExhaleReleaseState();
				setState(exhaleReleaseState);
			}, TIME_BREATHE_OUT);
		}

		@Override
		void handleExit() {
			//do nothing
		}

		@Override
		void onHoldButton() {
			//do nothing
		}

		@Override
		void onReleaseButton() {
			//do nothing
		}
	}

	private class ExhaleReleaseState extends State {

		@Override
		void handleEnter() {
			numberOfBreaths--;
			//update display of number of breaths remaining

			Button breatheButton = findViewById(R.id.button_breathe);
			if (numberOfBreaths > 0){
				breatheButton.setText(R.string.breathe_in);
			}
			else {
				breatheButton.setText(R.string.good_job);
			}

			Handler handler = new Handler();
			handler.postDelayed(() -> {
				if (currentState == ExhaleReleaseState.this){
					State exhaleDoneState = new ExhaleDoneState(ExhaleDoneState.WAITED);
					setState(exhaleDoneState);
				}
			}, TIME_BREATHE_OUT_STOP_ANIMATION - TIME_BREATHE_OUT);
		}

		@Override
		void handleExit() {

		}

		@Override
		void onHoldButton() {
			State exhaleDoneState = new ExhaleDoneState(ExhaleDoneState.BUTTON_PRESSED);
			setState(exhaleDoneState);
		}

		@Override
		void onReleaseButton() {
			//do nothing
			System.out.println("INVALID ACTION IN EXHALE RELEASE STATE");
		}
	}

	//TODO: find out why we have this transition state
	private class ExhaleDoneState extends State {

		public static final int WAITED = 0;
		public static final int BUTTON_PRESSED = 1;

		private final int transition;

		public ExhaleDoneState(int transition) {
			this.transition = transition;
		}

		@Override
		void handleEnter() {
			//change either to InhaleState or InhalingState depending on whether the button is currently being held or not.
			State buttonHeldState;
			switch (transition){
				case WAITED:
					buttonHeldState = new InhaleState();
					break;
				case BUTTON_PRESSED:
					buttonHeldState = new InhalingState();
					break;
				default:
					throw new IllegalStateException("Cannot have transition outside defined boundaries.");
			}
			setState(buttonHeldState);
		}

		@Override
		void handleExit() {
			//stop exhale animation/sound
		}

		@Override
		void onHoldButton() {
			//do nothing
		}

		@Override
		void onReleaseButton() {
			//do nothing
		}
	}

	private class PromptMoreState extends State {

		@Override
		void handleEnter() {
			//prompt the user to set the number of breaths needed
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
			TakeBreathActivity.this.finish();
		}

		@Override
		void handleExit() {
			//do nothing
		}

		@Override
		void onHoldButton() {
			//do nothing
		}

		@Override
		void onReleaseButton() {
			//do nothing
		}
	}

	private class IdleState extends State {}
}