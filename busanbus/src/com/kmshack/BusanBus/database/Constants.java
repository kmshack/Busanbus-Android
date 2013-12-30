package com.kmshack.BusanBus.database;

import android.provider.BaseColumns;

public final class Constants implements BaseColumns{
	
	private Constants(){
		
	}

	public static final class UserData implements BaseColumns {

		private UserData() {
			
		}
		
		// DB name & version	
		public static final String DB_NAME = "BusFavorite.kms";
		public static final int DB_VERSION = 3;
		
		// Table name
		public static final String FAVORITE ="Favorite";
		
		public static final String FAVORITE2 ="Favorite2";
		
		public static final String NOSUN ="NOSUN";
		public static final String STOPID ="STOPID";
		public static final String STOPNAME ="STOPNAME";
		public static final String UPDOWN ="UPDOWN";
		public static final String REALTIME ="REALTIME";
		public static final String ORD ="ORD";
		
		//DB_VERSION 3부터 적용
		public static final String ORDERING ="ORDERING";
		
		public static final String NOSUNNAME ="NOSUNNAME";
		public static final String NOSUNNO ="NOSUNNO";
	}

}

