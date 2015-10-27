package com.fpt.smac.tts;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
//import android.net.nsd.NsdManager.RegistrationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

//@SuppressLint("InlinedApi")
public class MainActivity extends Activity implements RecognitionListener {

	private static final int TIME_ANSWER = 10;
	TTSP ttsp;
	private Button mBt;
	private Button btnExit;
	private static EditText mEdit;
	static Timer timer = new Timer();

	protected static final int RESULT_SPEECH = 1;
	private SpeechRecognizer speech = null;
	private Intent intent;
	private Output output;
	private String UserAnwser = "";
	private MyDatabase database = new MyDatabase(this);
	public int stateMediaPlayer;
	public final int stateMP_Error = 0;
	public final int stateMP_NotStarter = 1;
	private Intent iService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		database.OpenConnecttion();
		database.LoadDataFromFile();
		output = new Output(database.GetDataBaseOnGrade(1),this);
		
		setContentView(R.layout.activity_main);
		// Init Service
		iService = new Intent(this, InvokingService.class);
		InvokingService.Init();
		
		mEdit = (EditText) findViewById(R.id.edit);
		mEdit.setText("Done1");

		ttsp = new TTSP();
		ttsp.mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
							mEdit.setText("OK");
							ListenFromUser();
							System.out.println("Duration" + mp.getDuration());
							mp.reset();
							countTimeAnswer();
			}
		});
		
		mBt = (Button) findViewById(R.id.bt);

		mBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ListenFromUser();
				speechQuestion();
			}	
		});
		
		btnExit = (Button) findViewById(R.id.btnExit);
		btnExit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				InvokingService.stopInvokingService();
				finish();
			}
		});
	}

	static public int seconds = 0;
	public TimerTask task;
	final Handler handler = new Handler();
	public void countTimeAnswer() {
		 task = new TimerTask() {
		    @Override
		    public void run() {

		        //Perform background work here

		        handler.post(new Runnable() {
		            public void run() {
		            	System.out.println(seconds);
						seconds++;
						if(seconds >= TIME_ANSWER){
							changeQuestion();
							output.wrongVoice.start();
//							seconds = 0;
							//cancel();
						}
		            }
		        });
		    }
		};
	
			//public void run() {
//				System.out.println(seconds);
//				seconds++;
//
//				if(seconds >= TIME_ANSWER){
//					changeQuestion();
//					seconds = 0;
//					task.
//				}
			//}
		timer.schedule(task, 0, 1000);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		stopService(iService);
		System.out.println("Service Stopped");
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (InvokingService.isActive) {
			startService(iService);
			//System.out.println("Service Started");
		}
		super.onPause();
	}

	@Override
	public void onBeginningOfSpeech() {
		// txtText.setText("");
	}

	@Override
	public void onBufferReceived(byte[] buffer) {

	}

	@Override
	public void onEndOfSpeech() {
	}

	@Override
	public void onError(int errorCode) {
		String errorMessage = getErrorText(errorCode);
		// txtText.setText(errorMessage);
		mEdit.setText(errorMessage);
		if(!TTSP.isPlaying)
			ListenFromUser();
		
	}

	@Override
	public void onEvent(int arg0, Bundle arg1) {
	}

	@Override
	public void onPartialResults(Bundle results) {

	}

	@Override
	public void onReadyForSpeech(Bundle arg0) {
	}

	@Override
	public void onResults(Bundle results) {
		ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		mEdit.setText("Result: " + ReciveResult(matches));
		UserAnwser += ReciveResult(matches);
		
		if(!output.checkRightAnswer(UserAnwser) && output.isOutOfColection)
		{
			
			mEdit.setText("Wating Answer");
			if(output.repeatQuention)
			{
				stopServices();
				speechQuestion();
			}
			else
				ListenFromUser();
		}
		else
		{
			if (output.checkRightAnswer(UserAnwser)) {
				output.correctVoice.start();
				mEdit.setText("Dung");
			} else {
				output.wrongVoice.start();
				mEdit.setText("Sai");
				//ListenFromUser();
			}
			changeQuestion();
//			ttsp.stopSpeakVi();
//			output.deleteRecord();
//			stopServices();
//			speechQuestion();
		}
		UserAnwser = "";
	}

	@Override
	public void onRmsChanged(float rmsdB) {

	}

	public static String getErrorText(int errorCode) {
		String message;

		switch (errorCode) {
		case SpeechRecognizer.ERROR_AUDIO:
			message = "Audio recording error";
			break;
		case SpeechRecognizer.ERROR_CLIENT:
			message = "Client side error";
			break;
		case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
			message = "Insufficient permissions";
			break;
		case SpeechRecognizer.ERROR_NETWORK:
			message = "Network error";
			break;
		case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
			message = "Network timeout";
			break;
		case SpeechRecognizer.ERROR_NO_MATCH:
			message = "No match";
			break;
		case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
			message = "RecognitionService busy";
			break;
		case SpeechRecognizer.ERROR_SERVER:
			message = "error from server";
			break;
		case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
			message = "No speech input";
			break;
		default:
			message = "Didn't understand, please try again.";
			break;
		}
		return message;
	}

	public String ReciveResult(ArrayList<String> matches) {
		String text = "";
		// for (String result : matches)
		// text += result + "\n";
		text = matches.get(0);
		return text;
	}

	public void ListenFromUser() {
		stopServices();
		speech = SpeechRecognizer.createSpeechRecognizer(this);
		speech.setRecognitionListener(this);
		intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.ACTION_WEB_SEARCH);
		intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, Long.valueOf(600000000));
		intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
				Long.valueOf(600000000));
		intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, Long.valueOf(600000000));
		intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

		// startActivityForResult(intent, 100);
		speech.startListening(intent);
	}

	public void stopServices() {
		if (intent != null) {
			intent = null;
			speech.stopListening();
			speech.destroy();
		}
	}

	private void speechQuestion()
	{
		//ttsp.stopSpeakVi();
		new Thread(new Runnable() {
			@Override
			public void run() {
				String s = output.Display();
				if (!s.equals("")) {
					ttsp.speakTTS(s);
				}		
			}
		}).start();
	}
	
	private void changeQuestion(){
		ttsp.stopSpeakVi();
		output.deleteRecord();
		stopServices();
		
		if(output.result.isEmpty())
		{
			if(output.NumberOfCorrectAnswer >= 1) //hard code number of correct answer
			{
				InvokingService.finishExercise();
				resetTimer();
				finish();
				return;
			}
		}
		else
		{
			//stopServices();
			speechQuestion();
		}
		//speechQuestion();
		//Reset timer after change question
		resetTimer();
	}
	
	private void resetTimer(){
		seconds = 0;
		task.cancel();
	}
}
