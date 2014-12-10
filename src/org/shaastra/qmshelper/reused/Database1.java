package org.shaastra.qmshelper.reused;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database1 {
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_USERID = "user_id";
	public static final String KEY_DISC = "disc";
	public static final String KEY_GALLERY = "gallery";
	public static final String KEY_BOWL = "bowl";
	public static final String KEY_FAN = "fan";
	public static final String KEY_COST = "cost";
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
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					KEY_USERID + " TEXT NOT NULL, " +
					KEY_DISC + " TEXT NOT NULL, "+
					KEY_GALLERY + " TEXT NOT NULL, "+
					KEY_BOWL + " TEXT NOT NULL, "+
					KEY_FAN + " TEXT NOT NULL, "+
					KEY_COST + " TEXT NOT NULL, "+
					KEY_SENT + " INTEGER);"
					);
			ContentValues cv = new ContentValues();
			cv.put(KEY_ROWID,0);
			cv.put(KEY_USERID, "USERID");
			cv.put(KEY_DISC, "DISCOUNT");
			cv.put(KEY_GALLERY, "GALLERY");
			cv.put(KEY_BOWL, "BOWL");
			cv.put(KEY_FAN, "FAN");
			cv.put(KEY_COST, "COST");
			cv.put(KEY_SENT, 2);
			db.insert(DATABASE_TABLE, null, cv);
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
	}
	public Database1(Context c){
		myContext = c;
	}
	public Database1 open(){
		myData = new Data(myContext);
		myDatabase = myData.getWritableDatabase();
		return this;
	}
	public long createEntry(String userid, String disc, String gallery, String bowl, String fan, String cost,int sent){
		ContentValues cv = new ContentValues();
		cv.put(KEY_USERID, userid);
		cv.put(KEY_DISC, disc);
		cv.put(KEY_GALLERY, gallery);
		cv.put(KEY_BOWL, bowl);
		cv.put(KEY_FAN, fan);
		cv.put(KEY_COST, cost);
		cv.put(KEY_SENT, sent);
		return myDatabase.insert(DATABASE_TABLE, null, cv);
	}
	public long deleteEntry(String userid){
		return myDatabase.delete(DATABASE_TABLE,KEY_USERID+"='"+userid+"'", null);
	}
	public String[][] getData(){
		int count=0;
		int i=0;
		String[] columns = new String[]{KEY_ROWID,KEY_USERID,KEY_DISC,KEY_GALLERY,KEY_BOWL, KEY_FAN,KEY_COST,KEY_SENT};
		Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
		int row = c.getColumnIndex(KEY_ROWID);
		int user = c.getColumnIndex(KEY_USERID);
		int disc = c.getColumnIndex(KEY_DISC);
		int gallery = c.getColumnIndex(KEY_GALLERY);
		int bowl = c.getColumnIndex(KEY_BOWL);
		int fan = c.getColumnIndex(KEY_FAN);
		int cost = c.getColumnIndex(KEY_COST);
		int sent = c.getColumnIndex(KEY_SENT);
		
		
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			count++;
		}
		String[][] data = new String[8][count];
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
				data[0][i] = c.getString(row);
				data[1][i] = c.getString(user);
				data[2][i] = c.getString(disc);
				data[3][i] = c.getString(gallery);
				data[4][i] = c.getString(bowl);
				data[5][i] = c.getString(fan);
				data[6][i] = c.getString(cost);
				if(c.getInt(sent)==1)
					data[7][i] = "sent";
				else if(c.getInt(sent)==0)
					data[7][i] = "not sent";
				else if(c.getInt(sent)==2)
					data[7][i] = "STATUS";
				i++;
		}
		return data;
	}
	public String[][] getDataToSend(){
		int count=0;
		int i=0;
		String[] columns = new String[]{KEY_ROWID,KEY_USERID,KEY_DISC,KEY_GALLERY,KEY_BOWL, KEY_FAN,KEY_COST,KEY_SENT};
		Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
		int row = c.getColumnIndex(KEY_ROWID);
		int user = c.getColumnIndex(KEY_USERID);
		int disc = c.getColumnIndex(KEY_DISC);
		int gallery = c.getColumnIndex(KEY_GALLERY);
		int bowl = c.getColumnIndex(KEY_BOWL);
		int fan = c.getColumnIndex(KEY_FAN);
		int cost = c.getColumnIndex(KEY_COST);
		int sent = c.getColumnIndex(KEY_SENT);
		
		
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			if(c.getInt(sent)==0)
				count++;
		}
		String[][] data = new String[8][count];
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			if(c.getInt(sent)==0){
				data[0][i] = c.getString(row);
				data[1][i] = c.getString(user);
				data[2][i] = c.getString(disc);
				data[3][i] = c.getString(gallery);
				data[4][i] = c.getString(bowl);
				data[5][i] = c.getString(fan);
				data[6][i] = c.getString(cost);
				if(c.getInt(sent)==1)
					data[7][i] = "Sent";
				else if(c.getInt(sent)==0)
					data[7][i] = "Not sent";
				else if(c.getInt(sent)==2)
					data[7][i] = "STATUS";
				i++;
			}
		}
		return data;
	}
	public int getCount(){
		int count=0;
		String[] columns = new String[]{KEY_ROWID,KEY_USERID,KEY_DISC,KEY_GALLERY,KEY_BOWL, KEY_FAN,KEY_COST,KEY_SENT};
		Cursor c = myDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
		int sent = c.getColumnIndex(KEY_SENT);
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			if(c.getInt(sent)==0)
				count++;
		}
		return count;
	}
	public void updateEntry(String userid, int put) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put(KEY_SENT, put);
		myDatabase.update(DATABASE_TABLE, cv, KEY_USERID+"='"+userid+"'", null);
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
}
