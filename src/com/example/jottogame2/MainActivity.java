package com.example.jottogame2;

import java.io.IOException;
import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.database.SQLException;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private DatabaseHandler db;
	private String TAG = "MAIN";
	private String answer;
	private int[] abc;
	private ArrayList<String> guesses = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		loadData();
		newGame();
		newGameListener();
		abc = new int[26];
		for(int i=0;i<abc.length;i++){	abc[i] = 2; }
		addGuessListener();
	}
	
	private void addGuessListener() {
		Button btnG = (Button) findViewById(R.id.Guess);
		btnG.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				EditText et01 = (EditText) findViewById(R.id.EditText01);
				String input = et01.getText().toString();
				//make sure it is a meaningful and valid guess
				if (input != null && !input.isEmpty() && input.length() == 5){
					//Log.i(TAG, "Check if valid guess");
					if(db.isValidGuess(input))
					{
						guesses.add(input);
						//Log.i(TAG, guesses.get(guesses.size()-1));
						TextView guessList = (TextView) findViewById(R.id.GuessList);
						String temp = guessList.getText().toString();
						guessList.setText(input+": ("+inCommon(answer, input).length()+") \n"+temp);
					}else{
						//Log.i(TAG, "NOT a valid guess");
						Toast.makeText(getApplicationContext(), input+" is not a valid guess", Toast.LENGTH_SHORT).show();
					}
				}
			}

			private String inCommon(String wordA, String wordB) {
				String common = "";
				for(int i=0;i<wordA.length();i++){  
				    for(int j=0;j<wordB.length();j++){  
				        if(wordA.charAt(i)==wordB.charAt(j)){  
				            common += wordA.charAt(i);  
				            break;
				        }  
				    }  
				} 
				return common;
			}
			
		});
	}
	
	public void loadData(){
		db = new DatabaseHandler(this);
		try {
			db.createDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			db.openDataBase();
		}catch( SQLException e){
			Log.e(TAG, e.toString());
		}
		//Log.i(TAG,db.getRandomWord());
	}
	
	private void newGameListener(){
		Button btnNG = (Button) findViewById(R.id.Giveup);
		btnNG.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				newGame();
			}
		});
	}
	
	private void newGame() {
		answer = db.getRandomWord();
		guesses = new ArrayList<String>();
		Log.v(TAG, answer);
		TextView guessList = (TextView) findViewById(R.id.GuessList);
		guessList.setText("");
	}
	
	private void recolorLetter(int index) {
		TextView guessList = (TextView) findViewById(R.id.GuessList);
		String temp = guessList.getText().toString();
		
		SpannableStringBuilder str = new SpannableStringBuilder(temp);
		for(int j=0;j<abc.length;j++)
		{
			int fcs;
			char c = (char)((int)('a') + j);
			if(abc[j] > 1){
				fcs = Color.rgb(0, 0, 0);
			}else if(abc[j] < 1){
				fcs = Color.rgb(0, 255, 0);
			}else{
				fcs = Color.rgb(255, 0, 0);
			}
			for(int i=0;i<temp.length();i++)
			{
				if(temp.charAt(i) == c){
					str.setSpan(new ForegroundColorSpan(fcs), i, i+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				}
			}
		}
		guessList.setText(str);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
