package org.shaastra.qmshelper.reused;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EventDatabase {
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_EVENTID = "event_id";
	public static final String KEY_EVENT = "event_name";
	public static final String KEY_TEAM = "is_team";
	
	private static final String DATABASE_NAME = "ListOfEvents";
	private static final String DATABASE_TABLE1 = "eventTable";
	private static final String DATABASE_TABLE2 = "eventTableOld";
	private static final int DATABASE_VERSION = 3;
	
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
			db.execSQL("CREATE TABLE " + DATABASE_TABLE1 + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					KEY_EVENTID+" TEXT NOT NULL, "+
					KEY_EVENT + " TEXT NOT NULL, "+
					KEY_TEAM + " TEXT NOT NULL);"
					);
			db.execSQL("CREATE TABLE " + DATABASE_TABLE2 + " (" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					KEY_EVENTID+" TEXT NOT NULL, "+
					KEY_EVENT + " TEXT NOT NULL, "+
					KEY_TEAM + " TEXT NOT NULL);"
					);
			ContentValues cv1 = new ContentValues();
			cv1.put(KEY_ROWID,0);
			cv1.put(KEY_EVENTID, "1");
			cv1.put(KEY_EVENT, "TEST EVENT 1");
			cv1.put(KEY_TEAM, "0");
			db.insert(DATABASE_TABLE1, null, cv1);
			
			ContentValues cv2 = new ContentValues();
			cv2.put(KEY_ROWID,1);
			cv2.put(KEY_EVENTID, "2");
			cv2.put(KEY_EVENT, "TEST EVENT 2");
			cv2.put(KEY_TEAM, "1");
			db.insert(DATABASE_TABLE1, null, cv2);
			
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE1);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE2);
			onCreate(db);
		}
	}
	public long update(){
		myDatabase.delete(DATABASE_TABLE2, null, null);
		myDatabase.execSQL("INSERT INTO "+DATABASE_TABLE2+" SELECT * FROM "+DATABASE_TABLE1);
		return myDatabase.delete(DATABASE_TABLE1, null, null);
	}
	public EventDatabase(Context c){
		myContext = c;
	}
	public EventDatabase open(){
		myData = new Data(myContext);
		myDatabase = myData.getWritableDatabase();
		return this;
	}
	public long createEntry(String event, String eventid, String isTeam){
		ContentValues cv = new ContentValues();
		cv.put(KEY_EVENT, event);
		cv.put(KEY_EVENTID, eventid);
		cv.put(KEY_TEAM, isTeam);
		return myDatabase.insert(DATABASE_TABLE1, null, cv);
	}
	public String[][] getData(){
		int count=0;
		int i=0;
		String[] columns = new String[]{KEY_ROWID,KEY_EVENTID,KEY_EVENT,KEY_TEAM};
		Cursor c = myDatabase.query(DATABASE_TABLE1, columns, null, null, null, null, null);
		int eventid = c.getColumnIndex(KEY_EVENTID);
		int  event= c.getColumnIndex(KEY_EVENT);
		int team = c.getColumnIndex(KEY_TEAM);
		
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			count++;
		}
		String[][] data = new String[3][count];
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
				data[0][i] = c.getString(eventid);
				data[1][i] = c.getString(event);
				data[2][i] = c.getString(team);
				i++;
		}
		return data;
	}
	public void close(){
		myData.close();
	}
}
