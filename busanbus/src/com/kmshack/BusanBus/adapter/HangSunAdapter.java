package com.kmshack.BusanBus.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.kmshack.BusanBus.R;

public class HangSunAdapter extends SimpleCursorAdapter{

	public HangSunAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.setViewBinder(new CountViewBinder());
	}

	class CountViewBinder implements ViewBinder {
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			try{
				if(view instanceof TextView) {
				   if(((TextView)view).getId() == R.id.text1) {
						String num = cursor.getString(columnIndex);
						((TextView)view).setText(cursor.getPosition()+1 +". " + num);
						return true;
				   }
				}
			}catch (Exception e) {}
			return false;
		}
	}
}
