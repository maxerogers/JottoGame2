package com.example.jottogame2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper{
	private static String DB_NAME = "jottogame.db";
	private static String DB_PATH = "";
	private static String TAG = "DatabaseHandler";
	private Context myContext;
	private SQLiteDatabase myDataBase;
	// Labels table name
	private static final String TABLE_WORDS = "words";
	// labels column names
	private static final String KEY_ID = "id";
	private static final String KEY_VALUE = "value";
	private static final int NUM_WORDS = 5757;
	public DatabaseHandler(Context context)  
	{ 
	    super(context, DB_NAME, null, 1);// 1? its Database Version 
	    DB_PATH = "/data/data/" + context.getPackageName() + "/databases/"; 
	    this.myContext = context; 
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {}
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {}  
	
	public void createDataBase() throws IOException 
	{ 
	    //If database not exists copy it from the assets 
	 
	    boolean mDataBaseExist = checkDataBase(); 
	    if(!mDataBaseExist) 
	    { 
	        this.getReadableDatabase(); 
	        this.close(); 
	        try  
	        { 
	            //Copy the database from assests 
	            copyDataBase(); 
	            Log.e(TAG, "createDatabase database created"); 
	        }  
	        catch (IOException mIOException)  
	        { 
	            throw new Error("ErrorCopyingDataBase"); 
	        } 
	    } 
	} 
	//Check that the database exists here: /data/data/your package/databases/Da Name 
	private boolean checkDataBase() 
	{ 
	    File dbFile = new File(DB_PATH + DB_NAME); 
	    //Log.v("dbFile", dbFile + "   "+ dbFile.exists()); 
	        return dbFile.exists(); 
	    } 
	 
	    //Copy the database from assets 
	    private void copyDataBase() throws IOException 
	    { 
	        InputStream mInput = myContext.getAssets().open(DB_NAME); 
	        String outFileName = DB_PATH + DB_NAME; 
	        OutputStream mOutput = new FileOutputStream(outFileName); 
	        byte[] mBuffer = new byte[1024]; 
	        int mLength; 
	        while ((mLength = mInput.read(mBuffer))>0) 
	        { 
	            mOutput.write(mBuffer, 0, mLength); 
	        } 
	        mOutput.flush(); 
	        mOutput.close(); 
	        mInput.close(); 
	    } 
	 
	    //Open the database, so we can query it 
	public boolean openDataBase() throws SQLException 
	{ 
	    String mPath = DB_PATH + DB_NAME; 
	    //Log.v("mPath", mPath); 
	    myDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY); 
	    //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS); 
	        return myDataBase != null; 
	    } 
	 
	    @Override 
	    public synchronized void close()  
	    { 
	        if(myDataBase != null) 
	            myDataBase.close(); 
	        super.close(); 
	    }
	    
	    public String getWord(int x)
		{
			String result = "Jotto";
		String query = "SELECT * FROM "+TABLE_WORDS+" WHERE id ="+x;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if(cursor.moveToFirst()){
			result = cursor.getString(1);
		}
		cursor.close();
		db.close();
		return result;
	}
	
	public String getRandomWord(){
		int rand = (int) (Math.random()*NUM_WORDS);
		return getWord(rand);
	}
	public boolean isValidGuess(String input)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT count(*) FROM "+TABLE_WORDS+" WHERE value = '"+input+"\'";
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		if(cursor.getInt(0) > 0)
			return true;
		return false;
	}
	public ArrayList<String> getAllWords(){
		ArrayList<String> words = new ArrayList<String>();
		String selectQuery = "SELECT * FROM "+TABLE_WORDS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		//looping through all rows and adding it to list
		if(cursor.moveToFirst()){
			do{
				words.add(cursor.getString(1));
			} while (cursor.moveToNext());
		}
		//close connections
		cursor.close();
		db.close();
		
		//return words
		return words;
	}
}
