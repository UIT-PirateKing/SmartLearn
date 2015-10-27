package com.fpt.smac.tts;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

/**
 * @author A handsome guy whose name is Dũng The service running asynchronous
 *         with app Service starts when User navigates from application Service
 *         stops when User navigates to main application.
 */
public class InvokingService extends Service {

	private Handler customHandler = new Handler();

	public static long TIME_TO_DO_EXERCISE = 20 * 1000L;
	public static long TIME_TO_LOCK = 120 * 1000L;
	public static long TIME_CALLBACK_ACTIVITY = 10;

	public static boolean isDoingExercise = false;
	public static boolean isLocked = false;
	public static boolean isAppStarted = false;
	public static boolean isPlayGame = false;
	public static boolean isActive = true;

	private static long timeAppStart = 0L;
	private static long timeGameStart = 0L;
	private long timeAppPassed = 0L;
	private long timeGamePassed = 0L;
	public static long timeLimited = 1 * 1000L;

	Intent i;

	//
	public static void setTimeLimitted(long time) {
		timeLimited = time;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("Service started");
		if (isDoingExercise) {
			customHandler.postDelayed(threadTimer, 0);
			return 0;
		}
		if (!isPlayGame) {
			isPlayGame = true;
			setTimeGameStart(SystemClock.uptimeMillis());
		}
		customHandler.postDelayed(threadTimer, 0);

		return 0;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		customHandler.removeCallbacks(threadTimer);
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// Create a runnable that asynchronous run with service
	// threadtimer is used to determine when calling app after the specific
	// periods of time.
	private Runnable threadTimer = new Runnable() {
		public void run() {
			//System.out.println("Thread Run");
			timeGamePassed = SystemClock.uptimeMillis() - getTimeGameStart();
			timeAppPassed = SystemClock.uptimeMillis() - getTimeAppStart();
			if (isDoingExercise) {
				callApplication();
				removeCallBack();
				return;
			}
			if (timeGamePassed > TIME_TO_DO_EXERCISE) {
				setTimeGameStart(SystemClock.uptimeMillis());
				isDoingExercise = true;
				System.out.println("TIME TO DO EXERCISE");
				callApplication();
				removeCallBack();
				return;
			}
			customHandler.postDelayed(this, 0);
		}
	};

	// Call main application
	public void callApplication() {
		if (i == null) {
			i = new Intent(getApplicationContext(), MainActivity.class);
		}
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}

	/*
	 * Remove call back from thread thread.stop() is deprecate, instead use
	 * thread.interrupt()
	 */
	public void removeCallBack() {
		customHandler.removeCallbacks(threadTimer);
		Thread.currentThread().interrupt();
		System.out.println("Removed call back from thread and interrupt thread");
	}

	/*
	 * Initialize the service which set some default properties Do not
	 * initialize service after have started app
	 */
	public static void Init() {
		if (isAppStarted) {
			System.out.println("Do not inaltialize App");
			return;
		}
		setTimeAppStart(SystemClock.uptimeMillis());
		setTimeGameStart(SystemClock.uptimeMillis());
		isAppStarted = true;
		isPlayGame = true;
		isActive = true;
		System.out.println("Service Initialized");
	}
	
	public static void finishExercise(){
		setTimeGameStart(SystemClock.uptimeMillis());
		isDoingExercise = false;
	}

	/*
	 * Stop service invoke to system isActive and isAppStarted set back to false
	 */
	public static void stopInvokingService() {
		isActive = false;
		isAppStarted = false;
	}

	public static long getTimeGameStart() {
		return timeGameStart;
	}

	public static void setTimeGameStart(long timeGameStart) {
		InvokingService.timeGameStart = timeGameStart;
	}

	public static long getTimeAppStart() {
		return timeAppStart;
	}

	public static void setTimeAppStart(long timeAppStart) {
		InvokingService.timeAppStart = timeAppStart;
	}
}
