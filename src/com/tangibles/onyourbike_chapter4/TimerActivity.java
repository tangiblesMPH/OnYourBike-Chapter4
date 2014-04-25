package com.tangibles.onyourbike_chapter4;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TimerActivity extends ActionBarActivity {

	protected String CLASS_NAME = TimerActivity.class.getSimpleName();
	protected TextView counter;
	protected Button start;
	protected Button stop;
	protected Handler handler;
	protected UpdateTimer updateTimer;

	protected boolean timerRunning = false;
	protected long startedAt;
	protected long lastStopped;
	protected String display;
	protected static long UPDATE_EVERY = 200;

	protected Vibrator vibrate;
	protected long lastSeconds;

	public TimerActivity() {
		CLASS_NAME = getClass().getName();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timer);

		counter = (TextView) findViewById(R.id.timer);
		start = (Button) findViewById(R.id.start_button);
		stop = (Button) findViewById(R.id.stop_button);

		Log.d(CLASS_NAME, "Setting text.");
		if (BuildConfig.DEBUG) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyLog().penaltyDeath().build());
		}

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (timerRunning) {
			handler = new Handler();
			updateTimer = new UpdateTimer();
			handler.postDelayed(updateTimer, UPDATE_EVERY);

		}
		vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		if (vibrate == null) {
			Log.w(CLASS_NAME, "No Vibration service exists.");
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (timerRunning) {
			handler.removeCallbacks(updateTimer);
			updateTimer = null;
			handler = null;

		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		enableButtons();
		setTimeDisplay();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(CLASS_NAME, "Showing menu.");

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void clickedStart(View view) {
		Log.d(CLASS_NAME, "Clicked start button.");
		timerRunning = true;
		enableButtons();

		startedAt = System.currentTimeMillis();
		setTimeDisplay();
		handler = new Handler();
		updateTimer = new UpdateTimer();
		handler.postDelayed(updateTimer, UPDATE_EVERY);
	}

	public void clickedStop(View view) {
		Log.d(CLASS_NAME, "Clicked stop button.");
		timerRunning = false;
		enableButtons();
		lastStopped = System.currentTimeMillis();
		handler.removeCallbacks(updateTimer);
		handler = null;
	}

	protected void enableButtons() {
		Log.d(CLASS_NAME, "Set buttons endabled/disaled.");

		try {
			start.setEnabled(!timerRunning);

			stop.setEnabled(timerRunning);
		} catch (Exception e) {
			Log.e(CLASS_NAME, "Error" + e);
		}
	}

	public void setTimeDisplay() {

		long timeNow;
		long diff;
		long seconds;
		long minutes;
		long hours;

		Log.d(CLASS_NAME, "Setting the display");
		if (timerRunning) {
			timeNow = System.currentTimeMillis();

		} else {
			timeNow = lastStopped;
		}
		diff = timeNow - startedAt;

		if (diff < 0) {
			diff = 0;
		}

		seconds = diff / 1000;
		minutes = seconds / 60;
		hours = minutes / 60;
		seconds = seconds % 60;
		minutes = minutes % 60;

		display = String.format("%d", hours) + ":"
				+ String.format("%02d", minutes) + ":"
				+ String.format("%02d", seconds);
		counter.setText(display);

	}

	class UpdateTimer implements Runnable {

		@Override
		public void run() {
			Log.d(this.toString(), "run");
			setTimeDisplay();
			counter.setText(display);
			if (handler != null) {
				handler.postDelayed(this, UPDATE_EVERY);
			}
			if(timerRunning){
				vibrateCheck();
			}
		}

	}

	protected void vibrateCheck() {
		long timeNow = System.currentTimeMillis();
		long diff = timeNow - startedAt;
		long seconds = diff / 1000;
		long minutes = seconds / 60;

		Log.d(CLASS_NAME, "vibrateCheck");

		seconds = seconds % 60;
		minutes = minutes % 60;
		if (vibrate != null & seconds == 0 && seconds != lastSeconds) {
			long[] once = { 0, 100 };
			long[] twice = { 0, 100, 400, 100 };
			long[] thrice = { 0, 100, 400, 100, 400, 100 };

			// every hour
			if (minutes == 0) {
				Log.i(CLASS_NAME, "Vibrate 3 times");
				vibrate.vibrate(thrice, -1);
			} else if (minutes % 15 == 0) {
				Log.i(CLASS_NAME, "Vibrate 2 times");
				vibrate.vibrate(twice, -1);
			} else if (minutes % 15 == 0) {
				Log.i(CLASS_NAME, "Vibrate 1 time");
				vibrate.vibrate(once, -1);
			}
		}
	}
}
