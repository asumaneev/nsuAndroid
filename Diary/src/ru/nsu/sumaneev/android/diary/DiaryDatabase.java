package ru.nsu.sumaneev.android.diary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DiaryDatabase {

	//	database's constants
	private static final String DB_NAME = "diaryDB";
	private static final int DB_VERSION = 1;
	private static final String DB_TABLE = "notes";
	
	
	//	columns' names
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_NOTE = "note_body";
	
	private static final String DB_CREATE_QUERY = (
			"CREATE TABLE "  + DB_TABLE + " ( "
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT"
			+ ", " + COLUMN_TITLE + " TEXT NOT NULL "
			+ ", " + COLUMN_NOTE + " TEXT"
			+ " );"
			);
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			
			db.execSQL(DB_CREATE_QUERY);
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private Context context;
	
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;

	private boolean isClosed = true;
	
	public DiaryDatabase(Context context) {
		
		this.context = context;
		
	}
	
	public void open() {
		
		dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
		db = dbHelper.getWritableDatabase();
		
		isClosed = false;
		
	}
	
	public void close() {
		dbHelper.close();
		
		isClosed = true;
	}
	
	public boolean isClosed() {
		return isClosed;
	}
	
	public long addNote(String title, String noteBody) {
		
		ContentValues cv = new ContentValues();
		
		cv.put(COLUMN_TITLE, title);
		cv.put(COLUMN_NOTE, noteBody);
		
		return db.insert(DB_TABLE, null, cv);
		
	}

	public boolean deleteNote(long rowId) {

        return db.delete(DB_TABLE, COLUMN_ID + " = " + rowId, null) > 0;
    }
	
	public boolean updateNote(long rowId, String title, String noteBody) {
		
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_TITLE, title);
		cv.put(COLUMN_NOTE, noteBody);

        return db.update(DB_TABLE, cv, COLUMN_ID + " = " + rowId, null) > 0;
	}
	
	public boolean updateNote(String oldTitle, String title, String noteBody) {
		
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_TITLE, title);
		cv.put(COLUMN_NOTE, noteBody);

        return db.update(DB_TABLE, cv, COLUMN_TITLE + " = " + "'" + oldTitle + "'", null) > 0;
	}
	
	public Cursor fetchAllNotes() {
		
		return db.query(DB_TABLE, new String[] {COLUMN_ID, COLUMN_TITLE}, null, null, null, null,  COLUMN_ID + " ASC");
	}

	public Cursor fetchNote(String pattern) {
		
		if (pattern.isEmpty()) {
			return fetchAllNotes();
		}
		
		Cursor cursor = db.query(
				DB_TABLE, 
				new String[] {COLUMN_ID, COLUMN_TITLE}, 
				COLUMN_TITLE + " LIKE " + "'" + pattern + "%'",
				null, 
				null, 
				null,  
				COLUMN_ID + " ASC");
		
		if (cursor != null) {
			cursor.moveToFirst();
        }
		
		return cursor;
	}
	
	public Cursor fetchNote(long rowId) {
		
		Cursor cursor = db.query(
				DB_TABLE, 
				new String[] {COLUMN_TITLE, COLUMN_NOTE}, 
				COLUMN_ID + " = " + rowId, 
				null, 
				null, 
				null, 
				COLUMN_ID + " ASC"
				);
		
		if (cursor != null) {
			cursor.moveToFirst();
        }
        return cursor;

	}
	
}
