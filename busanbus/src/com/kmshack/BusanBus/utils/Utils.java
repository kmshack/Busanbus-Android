package com.kmshack.BusanBus.utils;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class Utils {

	public static File getDbDirectory(Context context) {

		File cacheDir;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			cacheDir = new File(context.getApplicationContext().getExternalCacheDir(), "/databases");
		} else {
			cacheDir = new File(context.getCacheDir(), "/databases");
		}
		return cacheDir;
	}

	public static File getDbFile(Context context) {

		File cacheDir;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			cacheDir = new File(context.getApplicationContext().getExternalCacheDir(), "/databases/BusData.kms");
		} else {
			cacheDir = new File(context.getCacheDir(), "/databases/BusData.kms");
		}
		return cacheDir;
	}

}
