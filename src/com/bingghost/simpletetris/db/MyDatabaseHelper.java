package com.bingghost.simpletetris.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
	
	public MyDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public static final String CREATE_SCORE = "create table score ("
			+ "id integer primary key autoincrement, " 
			+ "name text, "
			+ "score integer)";

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_SCORE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists score");
		onCreate(db);
	}

}
