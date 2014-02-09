package com.example.jottogame2;

import java.io.IOException;
import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.SQLException;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private DatabaseHandler db;
	private String TAG = "MAIN";
	private String answer;
	private int[] abc;
	private int defaultButtonColor;
	private ArrayList<String> guesses = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		loadData();
		newGameListener();
		abc = new int[26];
		for(int i=0;i<abc.length;i++){	abc[i] = 1; }
		addAZButtons();
		newGame();
		addGuessListener();
	}

	private void addAZButtons() {
		LinearLayout llaf = (LinearLayout) findViewById(R.id.AFLayout);
		OnClickListener buttonListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				Button btn = (Button) v;
				char c = btn.getText().toString().charAt(0);
				Log.i(TAG,""+c);
				int x = c - 'A';
				if(abc[x] > 1){
					btn.setBackgroundResource(android.R.drawable.btn_default);
					abc[x] = 0;
				}else if(abc[x] < 1){
					btn.setBackgroundColor(Color.RED);
					abc[x] = 1;
				}else{
					btn.setBackgroundColor(Color.GREEN);
					abc[x] = 2;
				}
				recolorLetter();
			}
		};
		for(char c= 'A'; c <= 'F'; c++)
		{
			Button button = new Button(this, null, android.R.attr.buttonStyleSmall);
			button.setText(""+c);
			llaf.addView(button);
			button.setOnClickListener(buttonListener);
			button.setId(c);
		}
		LinearLayout llgf = (LinearLayout) findViewById(R.id.GLLayout);
		for(char c= 'G'; c <= 'L'; c++)
		{
			Button button = new Button(this, null, android.R.attr.buttonStyleSmall);
			button.setText(""+c);
			llgf.addView(button);
			button.setOnClickListener(buttonListener);
			button.setId(c);
		}
		LinearLayout llmr = (LinearLayout) findViewById(R.id.MRLayout);
		for(char c= 'M'; c <= 'R'; c++)
		{
			Button button = new Button(this, null, android.R.attr.buttonStyleSmall);
			button.setText(""+c);
			llmr.addView(button);
			button.setOnClickListener(buttonListener);
			button.setId(c);
		}
		LinearLayout llsx = (LinearLayout) findViewById(R.id.SXLayout);
		for(char c= 'S'; c <= 'X'; c++)
		{
			Button button = new Button(this, null, android.R.attr.buttonStyleSmall);
			button.setText(""+c);
			llsx.addView(button);
			button.setOnClickListener(buttonListener);
			button.setId(c);
		}
		LinearLayout llyz = (LinearLayout) findViewById(R.id.YZLayout);
		for(char c= 'Y'; c <= 'Z'; c++)
		{
			Button button = new Button(this, null, android.R.attr.buttonStyleSmall);
			button.setText(""+c);
			llyz.addView(button);
			button.setOnClickListener(buttonListener);
			button.setId(c);
		}
	}

	private void addGuessListener() {
		Button btnG = (Button) findViewById(R.id.Guess);
		btnG.setBackgroundResource(android.R.drawable.btn_default);
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
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(et01.getWindowToken(), 0);
						if(input.equals(answer))
						{
							
						}
						recolorLetter();
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
		btnNG.setBackgroundResource(android.R.drawable.btn_default);
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
		Log.i(TAG, answer);
		TextView guessList = (TextView) findViewById(R.id.GuessList);
		guessList.setText("");
		for(char c = 'A';c<='Z';c++)
		{
			Button btn = (Button) findViewById(c);
			btn.setBackgroundResource(android.R.drawable.btn_default);
		}
	}
	
	private void recolorLetter() {
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
