package com.fpt.smac.tts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;

public class TTSP extends Activity {
	private String TAG = "SMAC 2015";
	private String mHost = "http://118.69.135.22";
	public int stateMediaPlayer;
	public final int stateMP_Error = 0;
	public final int stateMP_NotStarter = 1;
	public MediaPlayer mediaPlayer;
	public static boolean isPlaying = false;

	public TTSP() {
		mediaPlayer = new MediaPlayer();
	}

	public void speakTTS(String msg) {
		String URL = mHost + "/synthesis/file?voiceType=\"female\"&text=\"" + URLEncoder.encode(msg) + "\"";
		downloadFile(URL, "sdcard/sound.wav");
		// speakVi( "sdcard/sound.wav");
	}

	public void speakVi(final String filePath) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				initMediaPlayer(filePath);
				mediaPlayer.start();
			}
		});
	}

	public void stopSpeakVi() {
		if (mediaPlayer.isPlaying())
			mediaPlayer.stop();
	}

	public void downloadFile(final String URL, final String filePath) {
		try {
			URL url = new URL(URL);
			Log.e(TAG, "Download URL: " + url.toString());
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setRequestProperty("accept-charset", "UTF-8");
			urlConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded; charset=utf-8");
			urlConnection.setDoOutput(true);
			urlConnection.connect();
			InputStream inputStream = urlConnection.getInputStream();
			final File file = new File(filePath);
			FileOutputStream fileOutput = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int bufferLength = 0;
			while ((bufferLength = inputStream.read(buffer)) > 0) {
				fileOutput.write(buffer, 0, bufferLength);
			}
			System.out.println("Download Complete!!!");
			speakVi(file.getAbsolutePath());
			fileOutput.close();
			urlConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initMediaPlayer(String path) {
		String PATH_TO_FILE = path;
		try {
			mediaPlayer.setDataSource(PATH_TO_FILE);
			mediaPlayer.prepare();
			System.out.println("Init Duration:" + mediaPlayer.getDuration());
			stateMediaPlayer = stateMP_NotStarter;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			stateMediaPlayer = stateMP_Error;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			stateMediaPlayer = stateMP_Error;
		} catch (IOException e) {
			e.printStackTrace();
			stateMediaPlayer = stateMP_Error;
		}
	}
}
