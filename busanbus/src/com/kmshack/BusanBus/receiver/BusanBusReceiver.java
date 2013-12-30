package com.kmshack.BusanBus.receiver;

import android.content.Context;
import android.content.Intent;

import com.google.android.apps.analytics.AnalyticsReceiver;
import com.kmshack.BusanBus.database.BusanBusPrefrence;

public class BusanBusReceiver extends AnalyticsReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		BusanBusPrefrence mBusanBusPrefrence = BusanBusPrefrence.getInstance(context);

		String values = intent.getExtras().getString("referrer");
		String[] keys = values.split("&");
		for (int i = 0; i < keys.length; i++) {
			String[] item = keys[i].split("=");

			if (item[0] != null && item[0].equals("utm_source")) {
				if (item[1] != null)
					mBusanBusPrefrence.setReceiverSource(item[1]);
			}
		}

		super.onReceive(context, intent);

	}
}
