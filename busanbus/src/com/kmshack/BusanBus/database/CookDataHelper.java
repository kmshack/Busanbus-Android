package com.kmshack.BusanBus.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kmshack.BusanBus.database.Constants.Cook;

public class CookDataHelper extends SQLiteOpenHelper {

	static final String TAG = "DB";

	public CookDataHelper(Context c) {
		super(c, Cook.DB_NAME, null, Cook.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Cook.TABLE_NAME + " ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + Cook.C1
				+ " text, " + Cook.C2 + " text, " + Cook.C3 + " text, "
				+ Cook.C4 + " text, " + Cook.C5 + " text, " + Cook.C6
				+ " text not null);");

		db.execSQL("CREATE TABLE " + Cook.TABLE_NAME2 + " ("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + Cook.C7
				+ " text, " + Cook.C8 + " text);");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}
}
