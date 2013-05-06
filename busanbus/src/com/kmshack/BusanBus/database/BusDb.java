package com.kmshack.BusanBus.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

/**
 * 버스 DB
 * @author kmshack
 *
 */
public class BusDb {

	private static final String DATABASE_PATH = Environment
			.getExternalStorageDirectory().getPath()
			+ "/Android/data/com.kmshack.BusanBus/databases/BusData.kms";

	private static BusDb sInstance;
	private SQLiteDatabase mDb;

	private BusDb(Context context) {
	}

	public synchronized static BusDb getInstance(Context context) {

		if (sInstance != null && sInstance.mDb != null) {
			return sInstance;
		}

		sInstance = new BusDb(context);
		if (sInstance.open(context) == false) {
			sInstance = null;
		}

		return sInstance;
	}

	private boolean open(Context context) {

		try {
			openDb();
			if (mDb == null)
				openDb();
		} catch (Exception e) {
			openDb();
		}

		return (mDb == null) ? false : true;
	}

	private void openDb() {
		mDb = SQLiteDatabase.openDatabase(DATABASE_PATH, null,
				SQLiteDatabase.CREATE_IF_NECESSARY);
	}

	public Cursor selectNosunForQuery(String query) {
		String slq = "";

		// 모든 노선
		if (query.length() <= 0 || query.equals("")) {
			slq = "select _id, NOSUNNUM, START, END from BUSLINEINFO order by NOSUNNUM COLLATE LOCALIZED ASC";
		}

		// 특정 노선
		else {
			slq = "select _id, NOSUNNUM, START, END from BUSLINEINFO where NOSUNNUM  like '%"
					+ query + "%' order by NOSUNNUM COLLATE LOCALIZED ASC";
		}
		return mDb.rawQuery(slq, null);

	}

	public Cursor selectBusStopForQuery(String query) {

		if (query.length() <= 0 || query.equals(""))
			return null;

		String slq = "";

		// 정류소번호 검색
		if (query.subSequence(0, 1).toString().equals("0")
				|| query.subSequence(0, 1).toString().equals("1")
				|| query.subSequence(0, 1).toString().equals("5")) {
			slq = "select _id, BUSSTOPNAME, UNIQUEID from BUSLINE where UNIQUEID  like '"
					+ query + "%' group by UNIQUEID";
		}

		// 정류소명 검색
		else {
			slq = "select _id, BUSSTOPNAME, UNIQUEID from BUSLINE where BUSSTOPNAME  like '"
					+ query + "%' group by UNIQUEID";
		}

		return mDb.rawQuery(slq, null);

	}

	/**
	 * 특정 노선 정보 가져 오기
	 * 
	 * @param nosun
	 * @return
	 */
	public Cursor selectBuslineInfo(String nosun) {

		String slq = "select * from BUSLINEINFO where NOSUNNUM='" + nosun + "'";

		return mDb.rawQuery(slq, null);

	}

	/**
	 * 특정 노선 정보 가져 오기
	 * 
	 * @param nosun
	 * @return
	 */
	public Cursor selectBusline(String nosun) {

		String slq = "select _id from BUSLINE where BUSLINENUM='" + nosun + "'";

		return mDb.rawQuery(slq, null);

	}

	/**
	 * 특정 노선 정보 가져 오기
	 * 
	 * @param nosun
	 * @return
	 */
	public Cursor selectBuslineForUniqueid(String uniqueid) {

		String slq = "select * from BUSLINE where UNIQUEID ='" + uniqueid + "'";

		return mDb.rawQuery(slq, null);

	}

	/**
	 * 특정 노선 상행선 가져 오기
	 * 
	 * @param nosun
	 * @return
	 */
	public Cursor selectNosunUp(String nosun) {

		Cursor cursor = selectBusline(nosun);
		int count = cursor.getCount();
		cursor.close();

		String slq = "select _id, UNIQUEID, ORD, BUSSTOPNAME, SIGUNNAME, GUNAME, DONGNAME from BUSLINE where BUSLINENUM ='"
				+ nosun + "' and ORD <=" + ((count / 2) + 5);

		return mDb.rawQuery(slq, null);

	}

	/**
	 * 특정 노선 하행선 가져 오기
	 * 
	 * @param nosun
	 * @return
	 */
	public Cursor selectNosunDown(String nosun) {

		Cursor cursor = selectBusline(nosun);
		int count = cursor.getCount();
		cursor.close();

		String slq = "select _id, UNIQUEID, ORD, BUSSTOPNAME, SIGUNNAME, GUNAME, DONGNAME from BUSLINE where BUSLINENUM ='"
				+ nosun + "' and ORD >=" + ((count / 2) - 5);

		return mDb.rawQuery(slq, null);

	}

	/**
	 * 정류소번호->정류소 이름
	 * 
	 * @param nosun
	 * @return
	 */
	public String selectStopIdToName(String stopid) {

		String slq = "select " + "BUSSTOPNAME " + // 종점 7
				"from BUSLINE where UNIQUEID ='" + stopid + "';";

		Cursor cursor = mDb.rawQuery(slq, null);

		String returnStr = " ";

		if (cursor.getCount() > 0) {
			cursor.moveToPosition(0);
			returnStr = cursor.getString(0);
		}
		cursor.close();

		return returnStr;
	}

	/**
	 * 특정 노선 하행선 가져 오기
	 * 
	 * @param nosun
	 * @return
	 */
	public Cursor selectReatime(String stopid, String nosun, String ord) {

		String slq = "select "
				+ "BUSLINEINFO.BUSLINEID, "
				+ // 버스번호 아이디 0
				"BUSLINE.REALTIME, "
				+ // 실시간 도착정보 1
				"BUSLINE.BUSLINENUM, "
				+ // 노선번호 2
				"BUSLINEINFO.START, "
				+ // 시작 3
				"BUSLINEINFO.MIDD, "
				+ // 중간 4
				"BUSLINEINFO.END, "
				+ // 종점 5
				"BUSLINE.X, "
				+ // 종점 6
				"BUSLINE.Y "
				+ // 종점 7
				"from BUSLINE, BUSLINEINFO where BUSLINEINFO.NOSUNNUM=BUSLINE.BUSLINENUM and BUSLINE.UNIQUEID ='"
				+ stopid + "' and BUSLINE.BUSLINENUM ='" + nosun
				+ "' and BUSLINE.ORD =" + ord;

		return mDb.rawQuery(slq, null);

	}

	public Cursor selectReatime(String busstop) {

		String slq = "select "
				+ "BUSLINEINFO.BUSLINEID, "
				+ // 버스번호 아이디 0
				"BUSLINE.REALTIME, "
				+ // 실시간 도착정보 1
				"BUSLINE.BUSLINENUM, "
				+ // 노선번호 2
				"BUSLINEINFO.START, "
				+ // 시작 3
				"BUSLINEINFO.MIDD, "
				+ // 중간 4
				"BUSLINEINFO.END "
				+ // 종점 5
				"from BUSLINE, BUSLINEINFO where BUSLINEINFO.NOSUNNUM=BUSLINE.BUSLINENUM and BUSLINE.UNIQUEID ='"
				+ busstop + "'";

		return mDb.rawQuery(slq, null);

	}

}
