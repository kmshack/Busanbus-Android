package com.kmshack.BusanBus.database;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kmshack.BusanBus.database.Constants.UserData;

public class UserDb {
	

	private static UserDb sInstance;
	private Context mContext;
	private SQLiteDatabase mDb;
	
	private UserDb(Context context){
		mContext = context;
	}
	
	public synchronized static UserDb getInstance(Context context){
		
		if(sInstance != null && sInstance.mDb != null){
			return sInstance;
		}
		
		sInstance = new UserDb(context);
		if( sInstance.open(context) == false){
			sInstance = null;
		}
		
		return sInstance;
	}
	
	private boolean open(Context context){

		CookDataHelper dbHelper;
		dbHelper = new CookDataHelper(context);
		
		// 간헐적으로 발생하는 비정상 종료 문제 때문에 아래와 같이 조치한다.
		try{
			mDb = dbHelper.getWritableDatabase();
			if(mDb == null)
				mDb = dbHelper.getWritableDatabase();
			
		}catch(Exception e){
			
			File dbDir = new File(mContext.getApplicationInfo().dataDir+"/databases");
			dbDir.mkdirs();
			
			mDb = dbHelper.getWritableDatabase();
		}
		
		return (mDb == null) ? false : true;
	}
	
	/**
	 * 모든 즐겨찾기된 노선 가져오기
	 * @return
	 */
	public Cursor selectFavoriteNosun(){
		String slq = "select * from Favorite" + " ORDER BY " + UserData.ORDERING + " ASC;";
		return mDb.rawQuery(slq, null);
	}
	
	/**
	 * 특정 즐겨찾기된 노선 삭제
	 * @param id
	 * @return
	 */
	public boolean deleteFavoriteNosun(int id){
		String slq = "delete from Favorite where _id = " + id;
		
		try{
			mDb.execSQL(slq);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 모든 버스정류소 가져오기
	 * @return
	 */
	public Cursor selectFavoriteBusStop(){
		String slq = "select * from Favorite2" + " ORDER BY " + UserData.ORDERING + " ASC;";
		return mDb.rawQuery(slq, null);
	}
	
	/**
	 * 특정 즐겨찾기된 버스정류소 삭제
	 * @param id
	 * @return
	 */
	public boolean deleteFavoriteBusStop(int id){
		String slq = "delete from Favorite2 where _id = " + id;
		
		try{
			mDb.execSQL(slq);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 즐겨찾기
	 * @param id
	 * @return
	 */
	public boolean insertFavorite(String nosun, String stopId, String stopName, 
									String upDown, String realtime, String ord){
		
		String slq = "insert into Favorite(NOSUN, STOPID, STOPNAME, UPDOWN, REALTIME ,ORD) values('" + 
										nosun + "', '" + 
										stopId + "', '" + 
										stopName + "', '" + 
										upDown + "', '" + 
										realtime + "', '" + 
										ord + "');";
				
		try{
			mDb.execSQL(slq);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 특정 즐겨찾기된 버스정류소 삭제
	 * @param id
	 * @return
	 */
	public boolean deleteFavorite(String nosun, String stopId){
		
		String slq = "delete from Favorite where NOSUN = '" + nosun + "' and STOPID = '" + stopId +"'";
				
		try{
			mDb.execSQL(slq);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	
	/**
	 * 특정 즐겨찾기된 것 있는지 여부
	 * @param id
	 * @return
	 */
	public boolean isRegisterFavorite(String nosun, String stopId){
		String slq = "select * from Favorite where " +
						"NOSUN = '" + nosun +"' and " +
						"STOPID = '" + stopId +"'";
		
		Cursor cursor = mDb.rawQuery(slq, null);
		int count = cursor.getCount();
		cursor.close();
		
		if(count > 0){
			return true;
		}
	
		return false;
		
	}
	
	
	/**
	 * 추가
	 * @param id
	 * @return
	 */
	public boolean insertFavorite2(String stopName, String stopId){
		
		Log.d("aa", "insertFavorite2 - stopName:" + stopName + ", stopId: "+stopId);
		
		String slq = "insert into Favorite2(NOSUNNAME, NOSUNNO) values('" + 
						stopName + "', '" + 
						stopId + "');";
				
		try{
			mDb.execSQL(slq);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 삭제
	 * @param id
	 * @return
	 */
	public boolean deleteFavorite2(String stopId){
		
		String slq = "delete from Favorite2 where NOSUNNO = '" + stopId +"'";
				
		try{
			mDb.execSQL(slq);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	
	/**
	 * 특정 즐겨찾기된 것 있는지 여부
	 * @param id
	 * @return
	 */
	public boolean isRegisterFavorite2(String stopId){
		String slq = "select * from Favorite2 where " +
						"NOSUNNO = '" + stopId +"'";
		
		Cursor cursor = mDb.rawQuery(slq, null);
		int count = cursor.getCount();
		cursor.close();
		
		if(count > 0){
			return true;
		}
	
		return false;
		
	}
	
	
	
	
	
	
	
	
	
	

	/** ORDER_NUM의 최대값을 구한다.
	 * @return 최대값
	 */
	public int getMaxOrderNum(String table){
		int maxOrderNum = 0;
		
		String slq = "SELECT MAX("+ UserData.ORDERING +")"
					+ " FROM " + table + ";";
		
		Cursor cursor = mDb.rawQuery(slq, null);
		if(cursor.moveToFirst()){
			maxOrderNum = cursor.getInt(0);
		}
		cursor.close();
		
		return maxOrderNum;
	}
	
	/**
	 * order_num 변경
	 */
	public int updateOrderNum(String table, int _id, int orderNum){
		ContentValues values = new ContentValues();
		values.put(UserData.ORDERING, orderNum);
		
		return mDb.update(table, values, UserData._ID + " = ? ", new String[]{String.valueOf(_id)});
	}
	
	public void updateBySql(String table, String value, String selection){
		
		String sql = "UPDATE "+ table
					+" SET "+ value;
		if(selection != null)
			sql += " WHERE " + selection;
		
		mDb.execSQL(sql);		
	}
	
	
	
	
	
	
	
}
