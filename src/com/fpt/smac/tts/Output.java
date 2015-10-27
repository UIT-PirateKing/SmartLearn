package com.fpt.smac.tts;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;

public class Output extends Activity {
	public static final int INDEX_OF_EXPLAIN = 7;
	public static final int INDEX_OF_CONTENT = 2;
	public static final int INDEX_OF_CORRECT_ANSWERS = 3;
	private static final int NUMBER_OF_ANSWERS = 4;
	public static final int INDEX_OF_LAST_ANSWER = 7;
	public static final int INDEX_OF_SECOND_ANSWERS = 4;
	public Vector<Vector<String>> result = new Vector<Vector<String>>();
	public MyDatabase database;
	public String question;
	public String answer1;
	public String answer2;
	public String answer3;
	public String answer4;
	public String explain;
	public boolean isOutOfColection = true;
	public boolean repeatQuention = false;
	public String answertitle = "";
	public String EndAnswer;
	public int NumberOfCorrectAnswer = 0;
	public MediaPlayer correctVoice, wrongVoice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	public Output(Vector<Vector<String>> result, Context context)
	{
		this.result = result;
		wrongVoice = MediaPlayer.create(context, R.raw.wronganswer);
		correctVoice = MediaPlayer.create(context, R.raw.correctanswer);
	}

	public boolean checkRightAnswer(String answer) {
		isOutOfColection = true;
		//int countInColection = 0;
		String []sAnswerArray = answer.split("\n");
		
		for(String s : sAnswerArray)
		{
			String sAnswer = endAnswer(s);			
			String rightAnswer = result.firstElement().elementAt(3).toString();
			if(!sAnswer.equalsIgnoreCase(answertitle) && "abcd".contains(sAnswer))
			{
				isOutOfColection = false;
				return false;
			}
			if (rightAnswer.equalsIgnoreCase(sAnswer) || sAnswer.equalsIgnoreCase(answertitle)) {
				//correctVoice.start();
				NumberOfCorrectAnswer++;
				return true;
			}
			else
			{
				for(int i = INDEX_OF_SECOND_ANSWERS ; i < INDEX_OF_LAST_ANSWER ; i++)
				{	
					String tempAnswer = result.firstElement().elementAt(i).toString();
					if (tempAnswer.equalsIgnoreCase(sAnswer)) 
					{
						isOutOfColection = false;
					}
				} 
			}
		}
		return false;

	}

	public String endAnswer(String answer) {
		repeatQuention = false;
		if(answer.contains("đọc lại"))
		{
			repeatQuention = true;
		}
			
		if(answer.contains("đáp án"))
		{
			answer = answer.substring(6);
			answer = answer.trim();
			return answer;
		}
		return " ";
	}

	public String Display() {
		Random random = new Random();
		int[] randomAnswerDisplay = new int[NUMBER_OF_ANSWERS];
		randomAnswerDisplay[0] = random.nextInt(NUMBER_OF_ANSWERS) + INDEX_OF_CORRECT_ANSWERS;
		switch (randomAnswerDisplay[0]) {
		case 6:
			randomAnswerDisplay[1] = 5;
			randomAnswerDisplay[2] = 4;
			randomAnswerDisplay[3] = INDEX_OF_CORRECT_ANSWERS;
			answertitle = "D";
			break;

		case 5:
			randomAnswerDisplay[1] = INDEX_OF_CORRECT_ANSWERS;
			randomAnswerDisplay[2] = 4;
			randomAnswerDisplay[3] = 6;
			answertitle = "B";
			break;
		case 4:
			randomAnswerDisplay[1] = 6;
			randomAnswerDisplay[2] = INDEX_OF_CORRECT_ANSWERS;
			randomAnswerDisplay[3] = 5;
			answertitle = "C";
			break;
		case INDEX_OF_CORRECT_ANSWERS:
			randomAnswerDisplay[1] = 4;
			randomAnswerDisplay[2] = 5;
			randomAnswerDisplay[3] = 6;
			answertitle = "A";
			break;
		}
		question = (result.firstElement().elementAt(INDEX_OF_CONTENT)) + " . ";
		answer1 = "Đáp án A: " + (result.firstElement().elementAt(randomAnswerDisplay[0])) + " . ";
		answer2 = "Đáp án B: " + (result.firstElement().elementAt(randomAnswerDisplay[1])) + " . ";
		answer3 = "Đáp án C: " + (result.firstElement().elementAt(randomAnswerDisplay[2])) + " . ";
		answer4 = "Đáp án Đ: " + (result.firstElement().elementAt(randomAnswerDisplay[3])) + " . ";
		explain = (result.firstElement().elementAt(INDEX_OF_EXPLAIN)) + " . ";

		return question + answer1 + answer2 + answer3 + answer4;
	}

	public void deleteRecord()
	{
		if(!result.isEmpty())
			result.remove(0);
	}
	
}
