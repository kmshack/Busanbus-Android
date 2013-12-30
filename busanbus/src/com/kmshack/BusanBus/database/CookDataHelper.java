package com.kmshack.BusanBus.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kmshack.BusanBus.database.Constants.UserData;

public class CookDataHelper extends SQLiteOpenHelper {

	static final String TAG = "DB";

	public CookDataHelper(Context c) {
		super(c, UserData.DB_NAME, null, UserData.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + UserData.FAVORITE + " (" + "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + UserData.NOSUN + " text, "
				+ UserData.STOPID + " text, " + UserData.STOPNAME + " text, " + UserData.UPDOWN + " text, " + UserData.REALTIME + " text, " + UserData.ORD
				+ " text not null, " + UserData.ORDERING + " INT DEFAULT 0);");

		db.execSQL("CREATE TABLE " + UserData.FAVORITE2 + " (" + "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + UserData.NOSUNNAME + " text, "
				+ UserData.NOSUNNO + " text, " + UserData.ORDERING + " INT DEFAULT 0);");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (UserData.DB_VERSION > oldVersion) {

			try {
				String tableCheckSqlF1 = "SELECT name FROM sqlite_master WHERE type='table' AND name = '" + UserData.FAVORITE + "'";
				Cursor cursorF1 = db.rawQuery(tableCheckSqlF1, null);
				boolean isTableF1 = cursorF1.getCount() > 0;
				cursorF1.close();

				// 테이블이 있다.
				if (isTableF1) {
					String alterSql1 = "ALTER TABLE " + UserData.FAVORITE + " ADD COLUMN " + UserData.ORDERING + " INT DEFAULT 0";
					db.execSQL(alterSql1);
				}

				String tableCheckSqlF2 = "SELECT name FROM sqlite_master WHERE type='table' AND name = '" + UserData.FAVORITE2 + "'";
				Cursor cursorF2 = db.rawQuery(tableCheckSqlF2, null);
				boolean isTableF2 = cursorF2.getCount() > 0;
				cursorF2.close();

				// 테이블이 있다.
				if (isTableF2) {
					String alterSql2 = "ALTER TABLE " + UserData.FAVORITE2 + " ADD COLUMN " + UserData.ORDERING + " INT DEFAULT 0";
					db.execSQL(alterSql2);
				}
			} catch (Exception e) {
			}
		}
	}
}
