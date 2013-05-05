package com.kmshack.BusanBus.database;

import android.provider.BaseColumns;

public final class Constants implements BaseColumns {

	private Constants() {

	}

	public static final class Cook implements BaseColumns {

		// DB name & version
		public static final String DB_NAME = "BusFavorite.kms";
		public static final int DB_VERSION = 2;

		// Table name
		public static final String TABLE_NAME = "Favorite";

		public static final String TABLE_NAME2 = "Favorite2";

		public static final String C1 = "NOSUN";
		public static final String C2 = "STOPID";
		public static final String C3 = "STOPNAME";
		public static final String C4 = "UPDOWN";
		public static final String C5 = "REALTIME";
		public static final String C6 = "ORD";

		public static final String C7 = "NOSUNNAME";
		public static final String C8 = "NOSUNNO";
	}

}
