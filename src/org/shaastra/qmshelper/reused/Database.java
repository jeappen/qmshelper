package org.shaastra.qmshelper.reused;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database {
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_USERID = "user_id";
	public static final String KEY_EVENT = "event_name";
	public static final String KEY_EVENTID = "event_id";
	public static final String KEY_SENT = "yes_no";
	
	private static final String DATABASE_NAME = "Registrations";
	private static final String DATABASE_TABLE = "registerTable";
	private static final int DATABASE_VERSION = 1;
	
	private Data myData;
	private final Context myContext;
	private SQLiteDatabase myDatabase;
	
	private static class Data extends SQLiteOpenHelper{

		public Data(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					KEY_USERID + " TEXT NOT NULL, " +
					KEY_EVENT + " TEXT NOT NULL, "+
					KEY_EVENTID + " TEXT NOT NULL, "+
					KEY_SENT + " INTEGER);"
					);
			ContentValues cv = new ContentValues();
			cv.put(KEY_ROWID,0);
			cv.put(KEY_USERID, "USERID");
			cv.put(KEY_EVENTID, "0");
			cv.put(KEY_EVENT, "EVENT");
			cv.put(KEY_SENT, 2);
			db.insert(DATABASE_TABLE, null, cv);
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
	}
	public Database(Context c){
		myContext = c;
	}
	public Database open(){
		myData = new Data(myContext);
		myDatabase = myData.getWritableDatabase();
		return this;
	}
	public long createEntry(String userid, String event,String eventid,int sent){
		ContentValues cv = new ContentValues();
		cv.put(KEY_EVENT, event);
		cv.put(KEY_USERID, userid);
		cv.put(KEY_EVENTID, eventid);
		cv.put(KEY_SENT, sent);
		return myDatabase.insert(DATABASE_TABLE, null, cv);
	}
	public long deleteEntry(String userid, String event){
		return myDatabase.delete(DATABASE_TABLE,KEY_USERID+"='"+userid+"' and "+KEY_EVENT+"='"+event+"'", null);
	}
	public long deleteEntryAlso(String userid, String eventid){
		return myDatabase.delete(DATABASE_TABLE,KEY_USERID+"='"+userid+"' and "+KEY_EVENTID+"='"+eventid+"'", null);
	}
	public String[][] getData(){
		int count=0;
		int i=0;
		String[] columns = new String[]{KEY_ROWID,KEY_USERID,KEY_EVENT,KEY_EVENTID,KEY_SENT};
		Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
		int userid = c.getColumnIndex(KEY_USERID);
		int event = c.getColumnIndex(KEY_EVENT);
		int row = c.getColumnIndex(KEY_ROWID);
		int sent = c.getColumnIndex(KEY_SENT);
		
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			count++;
		}
		String[][] data = new String[4][count];
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
				data[0][i] = c.getString(row);
				data[1][i] = c.getString(userid);
				data[2][i] = c.getString(event);
				if(c.getInt(sent)==1)
					data[3][i] = "sent";
				else if(c.getInt(sent)==0)
					data[3][i] = "not sent";
				else if(c.getInt(sent)==2)
					data[3][i] = "STATUS";
				i++;
		}
		return data;
	}
	public String[][] getDataToSend(){
		int count=0;
		int i=0;
		String[] columns = new String[]{KEY_ROWID,KEY_USERID,KEY_EVENT,KEY_EVENTID,KEY_SENT};
		Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
		int userid = c.getColumnIndex(KEY_USERID);
		int eventid = c.getColumnIndex(KEY_EVENTID);
		int sent = c.getColumnIndex(KEY_SENT);
		
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			if(c.getInt(sent)==0)
				count++;
		}
		String[][] data = new String[2][count];
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			if(c.getInt(sent)==0){
				data[0][i] = c.getString(eventid);
				data[1][i] = c.getString(userid);
				i++;
			}
		}
		return data;
	}
	public int getCount(){
		int count=0;
		String[] columns = new String[]{KEY_ROWID,KEY_USERID,KEY_EVENT,KEY_EVENTID,KEY_SENT};
		Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
		int sent = c.getColumnIndex(KEY_SENT);
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			if(c.getInt(sent)==0)
				count++;
		}
		return count;
	}
	public void updateEntry(String userid, String eventid, int put) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put(KEY_SENT, put);
		myDatabase.update(DATABASE_TABLE, cv, KEY_USERID+"='"+userid+"' AND "+KEY_EVENTID+"='"+eventid+"'", null);
	}
	public void close(){
		myData.close();
	}
	public void dbToCsv() throws IOException {
		String[][] data = getData();
		File file = new File("/sdcard/data.csv");
		// if file doesnt exists, then create it
		file.createNewFile();
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		int numEntries = data[0].length;
		int numbCols = data.length;
		for (int i = 0; i < numEntries; i++) {
			for (int j = 0; j < numbCols; j++) {
				bw.write(data[j][i] + ",");
			}
			bw.write("\n");
		}
		bw.close();
		fw.close();
	}
	public boolean findEntry(String userid, String eventid){
		String[] columns = new String[]{KEY_ROWID,KEY_USERID,KEY_EVENT,KEY_EVENTID,KEY_SENT};
		Cursor c = myDatabase.query(DATABASE_TABLE, columns, KEY_USERID+"='"+userid+"' AND "+KEY_EVENTID+"='"+eventid+"'", null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		if(c.getCount()!=0)
			return true;
		return false;
	}
}
