package com.cmpt276.parentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Contains the UI for the timer
 */
public class TimerActivity extends AppCompatActivity {

	private static final String ORIGINAL_TIME_IN_MILLI_SECONDS_TAG = "original_time_in_milli_seconds_tag";
	private static final String TIMER_SERVICE_BROADCAST = "timer_service_broadcast";
	private static final String STOP_ALARM_BROADCAST = "stop_alarm_broadcast";
	private static final String SERVICE_RUNNING_FLAG = "service_running_flag";
	private static final long DEFAULT_MINUTES_IN_MILLI_SECONDS = 0L;

	TextView remainingTime;
	private Intent serviceIntent;
	private long originalTimeInMilliSeconds;
	private boolean isServiceRunning;

	BroadcastReceiver timerReceiver;
	BroadcastReceiver stopAlarmReceiver;

	TimerService timerService;
	private boolean timerServiceBound = false;


	public static Intent getIntent(Context context, long minutesInMilliSeconds) {
		return TimerActivity.getIntent(context, minutesInMilliSeconds, false);
	}

	public static Intent getIntent(Context context,
								   long originalTimeInMilliSeconds,
								   boolean isServiceRunning) {

		Intent intent = new Intent(context, TimerActivity.class);
		intent.putExtra(ORIGINAL_TIME_IN_MILLI_SECONDS_TAG, originalTimeInMilliSeconds);
		intent.putExtra(SERVICE_RUNNING_FLAG, isServiceRunning);

		return intent;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timer);

		originalTimeInMilliSeconds = this.getIntent().getLongExtra(ORIGINAL_TIME_IN_MILLI_SECONDS_TAG, DEFAULT_MINUTES_IN_MILLI_SECONDS);
		isServiceRunning = this.getIntent().getBooleanExtra(SERVICE_RUNNING_FLAG, false);
		setUpPausePlayButton();
		setUpResetButton();
		setUpNewTimerButton();
		setUpBackBtn();

	}

	private void setUpBackBtn() {
		Button backBtn = findViewById(R.id.backBtn_timer);
		backBtn.setOnClickListener(view -> finish());
	}

	@Override
	protected void onStart() {
		super.onStart();
		setUpStartService();
		setupTimerBroadCastReceiver();
		setUpStopAlarmReceiver();
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(timerReceiver);
		unregisterReceiver(stopAlarmReceiver);
		unbindService(connection);
		finish();
	}

	private void setUpStartService() {
		serviceIntent = TimerService.getIntent(this, originalTimeInMilliSeconds);

		if (!isServiceRunning) {
			startService(serviceIntent);
			isServiceRunning = true;
		}

		bindService(serviceIntent, connection, 0);
	}

	private void setupTimerBroadCastReceiver() {
		remainingTime = findViewById(R.id.time_text);

		timerReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				updateTimerLabelAndChart();
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(TIMER_SERVICE_BROADCAST);
		registerReceiver(timerReceiver, filter);
	}

	private void updateTimerLabelAndChart() {
		TextView timeText = findViewById(R.id.time_text);
		timeText.setText(timerService.getTimeString());

		ProgressBar progressBar = findViewById(R.id.progressBar);
		progressBar.setProgress((int)timerService.getProgress());
		Log.i("progress" ,progressBar.getProgress() + " ");
	}

	private void resetTimer() {

		String originalTime = timerService.getOriginalTimeString();

		stopService(serviceIntent);
		isServiceRunning = false;

		TextView timeText = findViewById(R.id.time_text);
		timeText.setText(originalTime);

		ProgressBar progressBar = findViewById(R.id.progressBar);
		progressBar.setProgress(100);
	}

	private void setUpNewTimerButton() {

		Button editTimeButton = findViewById(R.id.new_timer_button);

		editTimeButton.setOnClickListener(view -> {

			if (isServiceRunning) {
				stopService(serviceIntent);
				isServiceRunning = false;
			}

			Intent intent = TimerOptions.getIntent(this);
			startActivity(intent);

			finish();

		});
	}

	private void setUpResetButton() {

		Button resetButton = findViewById(R.id.reset_button);

		resetButton.setOnClickListener(view -> resetTimer());
	}

	private void updatePausePlayButtonText() {
		Button pausePlayButton = findViewById(R.id.pause_play);

		if ((!timerServiceBound) || timerService.isPaused()) {
			pausePlayButton.setText(R.string.play_button_text);

		}
		else {
			pausePlayButton.setText(R.string.pause_button_text);
		}

	}

	private void setUpPausePlayButton() {
		Button pausePlayButton = findViewById(R.id.pause_play);

		pausePlayButton.setOnClickListener(view -> {
			if (!timerServiceBound) {
				setUpStartService();
			}
			else {
				if (timerService.isPaused()) {
					timerService.playTimer();
				}
				else {
					timerService.pauseTimer();
					updateTimerLabelAndChart();
				}
			}
			updatePausePlayButtonText();
		});

	}

	private void setUpStopAlarmReceiver() {
		stopAlarmReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				setUpStopAlarmButton();
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(STOP_ALARM_BROADCAST);
		registerReceiver(stopAlarmReceiver, filter);
	}

	private void setUpStopAlarmButton() {

		if (timerService.isFinish()) {
			ProgressBar progressBar = findViewById(R.id.progressBar);
			progressBar.setProgress(0);

			Button stopAlarmButton = findViewById(R.id.stop_alarm_button);
			Button pausePlayButton = findViewById(R.id.pause_play);
			Button resetButton = findViewById(R.id.reset_button);
			Button newTimerButton = findViewById(R.id.new_timer_button);


			stopAlarmButton.setVisibility(View.VISIBLE);
			pausePlayButton.setVisibility(View.INVISIBLE);
			resetButton.setVisibility(View.INVISIBLE);
			newTimerButton.setVisibility(View.INVISIBLE);

			stopAlarmButton.setOnClickListener(view -> {
				timerService.stopSoundAndVibration();
				resetTimer();
				updatePausePlayButtonText();
				stopAlarmButton.setVisibility(View.INVISIBLE);
				pausePlayButton.setVisibility(View.VISIBLE);
				resetButton.setVisibility(View.VISIBLE);
				newTimerButton.setVisibility(View.VISIBLE);
			});
		}

	}

	/**
	 * https://developer.android.com/guide/components/bound-services#Binder
	 */
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className,
									   IBinder service) {

			TimerService.LocalBinder binder = (TimerService.LocalBinder) service;
			timerService = binder.getService();
			timerServiceBound = true;

			updateTimerLabelAndChart();
			updatePausePlayButtonText();
			setUpStopAlarmButton();

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			timerServiceBound = false;
			updatePausePlayButtonText();
		}
	};

}