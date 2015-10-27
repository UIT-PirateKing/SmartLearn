package com.fpt.smac.tts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @author uitpirateking
 *	Name: MyDatabase
 *	Purpose: Handle anything involve database such as: connect database, insert, update, delete or get data form 
 *			database
 */
public class MyDatabase {
	private static final String DATABASE_NAME = "UITPIRATEKING";
	private static final String TABLE_NAME = "Question";
	private static final String QuestionID = "ID";
	private static final String Priority = "Priority";
	private static final String Grade = "Grade";
	private static final String Content = "Content";
	private static final String Answer1 = "Answer1"; //correct answer
	private static final String Answer2 = "Answer2";
	private static final String Answer3 = "Answer3";
	private static final String Answer4 = "Answer4";
	private static final String Explain = "Explain";
	
	
	private static SQLiteDatabase database;
	private static Context context;
	private OpenHelper openHelper;
	
	/**
	 * Constructor
	 * @param c
	 */
	public MyDatabase(Context c)
	{
		MyDatabase.context = c;
	}
	
	/**
	 * Purpose: connect to database
	 * @return ds
	 * @throws SQLException
	 */
	public MyDatabase OpenConnecttion() throws SQLException
	{
		openHelper = new OpenHelper(context);
		database = openHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * close connection
	 */
	public void close()
	{
		openHelper.close();
	}

	/**
	 * load SQL statement from text file
	 */
	public void LoadDataFromFile()
	{
		BufferedReader br  = null;
		try 
		{			 
			AssetManager am = context.getAssets();
			InputStream is = am.open("Data.txt"); //text file in assets folder
			
			br = new BufferedReader(new InputStreamReader(is,"UTF-16")); //handle unicode string
			
			String temp = "";
			
			while((temp = br.readLine()) != null)
			{
				try
				{
					database.execSQL(temp); //execute that statement in database
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
				
			}
			br.close();
 
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		/*database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		database.execSQL("CREATE TABLE " + TABLE_NAME + " ("
				+ QuestionID + " INT PRIMARY KEY, "
				+ Grade  + " INT, "
				+ Content  + " TEXT, "
				+ Answer1  + " TEXT, " 
				+ Answer2  + " TEXT, "
				+ Answer3  + " TEXT, "
				+ Answer4  + " TEXT, "
				+ Priority + " INT);");*/
	}
	
	/**
	 * Get 30 questions from database
	 * @param grade: grade of question that will check out from database
	 * @return: 30 questions with the lowest priority to ensure new question always is choice first
	 */
	public Vector<Vector<String>> GetDataBaseOnGrade(int grade)
	{
		Vector<Vector<String>> result = new Vector<Vector<String>>();
		int count = 0;
		Cursor cursor ;
		
		//columns have necessary data
		String[] columns = {QuestionID,Priority, Content,Answer1,Answer2,Answer3,Answer4, Explain};
		//get data from database with condition is grade equal given grade
		cursor = database.query(TABLE_NAME, columns, "Grade=?", new String[]{""+grade}, null, null, Priority);
		
		if(cursor != null && cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			//check out 30 question from database
			while(count < 2 && !cursor.isLast())
			{
				Vector<String> element = new Vector<String>();
				//get data from each cursor and add them into vector result
				for(int i = 0; i< 8; i++)
				{
					if(cursor.getString(i) != null)
						element.add(cursor.getString(i));
				}
				result.add(element);
				
				cursor.moveToNext();
				count ++;
			}
		}
		
		return result;
	}
	
	/**
	 * Update Priority of a question
	 * @param ID: ID of question want to udpate
	 * @param newPriority
	 */
	public void updatePriority(int ID, int newPriority)
	{
		 ContentValues data = new ContentValues();
		 data.put(Priority,newPriority);
		 database.update(TABLE_NAME, data, QuestionID +" = " + ID, null);
	}
	
	/**
	 * Purpose: add new question into database
	 * @param Content: content of question
	 * @param Answer1: correct answer
	 * @param Answer2
	 * @param Answer3
	 * @param Answer4
	 */
	public void insert(int grade, String content,String answer1,String answer2,String answer3,String answer4,String explain)
	{
		int questionID = getNumberOfRecordInDB() + 2;
		ContentValues data = new ContentValues();
		data.put(QuestionID, questionID);
		data.put(Grade, grade);
		data.put(Priority,0);
		data.put(Content, content);
		data.put(Answer1, answer1);
		data.put(Answer2, answer2);
		data.put(Answer3, answer3);
		data.put(Answer4, answer4);
		data.put(Explain, explain);
		database.insert(TABLE_NAME, null, data);
	}
	
	/**
	 * Purpose: get number of record in database
	 * @return
	 */
	public int getNumberOfRecordInDB()
	{
		Cursor cursor ;
		
		//columns have necessary data
		String[] columns = {QuestionID};
		//get data from database with condition is grade equal given grade
		cursor = database.query(TABLE_NAME, columns, null, null, null, null, QuestionID);
		
		cursor.moveToLast();
		//cursor.moveToPosition(database.rawQuery("Select * from "+TABLE_NAME, null).getCount());
		return cursor.getInt(0);
		//return database.rawQuery("Select * from "+TABLE_NAME, null).getCount();
	}
	
//=================================================================================//
//=================================================================================//
//=================================================================================//
//=================================================================================//
	
	/**
	 * 
	 * @author uitpirateking
	 *	Class OpenhHelper
	 *	Purpose: Handler for Create table and Upgrade table in database
	 */
	private static class OpenHelper extends SQLiteOpenHelper {
		public OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, 1);
		}
	
		@Override
		public void onCreate(SQLiteDatabase arg0) {
			//arg0.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			arg0.execSQL("CREATE TABLE " + TABLE_NAME + " ("
					+ QuestionID + " INT PRIMARY KEY, "
					+ Grade  + " INT, "
					+ Priority + " INT, "
					+ Content  + " TEXT, "
					+ Answer1  + " TEXT, " 
					+ Answer2  + " TEXT, "
					+ Answer3  + " TEXT, "
					+ Answer4  + " TEXT, "
					+ Explain  + " TEXT);");
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			arg0.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(arg0);
		}
	}
}
