package com.kmshack.BusanBus.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.kmshack.BusanBus.App;
import com.kmshack.BusanBus.activity.SearchMainActivity;

public class BusanBusPrefrence {

	private static BusanBusPrefrence sInstance;

	private SharedPreferences mPref;
	private SharedPreferences.Editor mEditor;

	private BusanBusPrefrence(Context context) {
		mPref = PreferenceManager.getDefaultSharedPreferences(context);
		mEditor = mPref.edit();
	}

	public synchronized static BusanBusPrefrence getInstance(Context context) {

		if (sInstance != null) {
			return sInstance;
		}

		sInstance = new BusanBusPrefrence(context);
		return sInstance;
	}

	public boolean setReceiverSource(String source) {
		mEditor.putString("receiver_source", source);
		return mEditor.commit();
	}

	public String getReceiverSource() {
		return mPref.getString("receiver_source", null);
	}

	public boolean setIsFavoriteStart(boolean is) {
		mEditor.putBoolean("favorite_start", is);
		return mEditor.commit();
	}

	public boolean getIsFavoriteStart() {
		return mPref.getBoolean("favorite_start", false);
	}

	public boolean setIsArriveSort(boolean is) {
		mEditor.putBoolean("arrive_sort", is);
		return mEditor.commit();
	}

	public boolean getIsArriveSort() {
		return mPref.getBoolean("arrive_sort", true);
	}

	public boolean setIsFavoriteLocation(boolean is) {
		mEditor.putBoolean("favorite_start_location", is);
		return mEditor.commit();
	}

	public boolean getIsFavoriteLocation() {
		return mPref.getBoolean("favorite_start_location", true);
	}

	public boolean setFavoriteLocation(int value) {
		mEditor.putInt("favorite_location", value);
		return mEditor.commit();
	}

	public int getFavoriteLocation() {
		return mPref.getInt("favorite_location", SearchMainActivity.FAVORITE_TAB_NOSUN);
	}

	public boolean setTextSize(int value) {
		App.FONT_SIZE = value;
		mEditor.putInt("text_size", App.FONT_SIZE);
		return mEditor.commit();
	}

	public int getTextSize() {
		App.FONT_SIZE = mPref.getInt("text_size", 0);
		return App.FONT_SIZE;
	}

	public boolean getShowArriveMap() {
		return mPref.getBoolean("arrive_show_map", true);
	}

	public boolean setShowArriveMap(boolean value) {
		mEditor.putBoolean("arrive_show_map", value);
		return mEditor.commit();
	}

	public boolean setLocationAgree() {
		mEditor.putBoolean("agree_location", true);
		return mEditor.commit();
	}

	public boolean getIsLocationAgree() {
		return mPref.getBoolean("agree_location", false);
	}
}
