package com.bingghost.simpletetris.db;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBScore {
	Context mContext = null;
	MyDatabaseHelper dbHelper = null;
	
	
	public DBScore(Context context) {
		mContext = context;
		
		InitDB();
	}
	
	private void InitDB() {
		dbHelper = new MyDatabaseHelper(mContext, DBConfig.DB_NAME, null, DBConfig.DB_VERSION);
		if (dbHelper == null) {
			return;
		}
		
		dbHelper.getWritableDatabase();
	}
	
	public boolean addScore(String name,int score) {
		if (dbHelper == null) {
			return false;
		}
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("score", score);
		db.insert("score", null, values);
		values.clear();
		return true;
	}
	
	public boolean delScore(int id) {
		if (dbHelper == null) {
			return false;
		}
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete("score", "id = ?", new String[] { "" +  id});
		return true;
	}
	
	public boolean queryInfo(List<ScoreInfo> listScore) {
		if (dbHelper == null) {
			return false;
		}
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from score order by score desc limit 0,10",null);
		if(cursor.moveToFirst()) {
			do {
		    	int index_id = cursor.getColumnIndex("id");
		    	int id = cursor.getInt(index_id);
		    	int score = cursor.getInt(cursor.getColumnIndex("score"));
		        String name = cursor.getString(cursor.getColumnIndex("name"));
		        
		        // LogUtil.v("id:" + id + " name:" + name + " score:" + score);
		        
		        ScoreInfo scoreInfo = new ScoreInfo(name, score, id, 0);
		        listScore.add(scoreInfo);
			} while (cursor.moveToNext());
		}
		
		return true;
	}
}
