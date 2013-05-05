package com.kmshack.BusanBus;

import android.app.Application;

import com.kmshack.BusanBus.database.BusanBusPrefrence;

/**
 * 
 * @author kmshack
 *
 */
public class App extends Application {

	static public int FONT_SIZE; 
	
	@Override
	public void onCreate() {
		
		FONT_SIZE = BusanBusPrefrence.getInstance(getApplicationContext()).getTextSize();
		
		super.onCreate();
	}
	
}
