package com.kmshack.BusanBus.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CursorAdapter;

import com.kmshack.BusanBus.R;
import com.kmshack.BusanBus.database.Constants.UserData;
import com.kmshack.BusanBus.utils.ViewHolder;
import com.kmshack.BusanBus.view.SizeableTextView;

public class MainNosunFavoriteAdapter extends CursorAdapter {

	private LayoutInflater mInflater;

	private int mIdxNm;
	private int mIdxId;
	private int mIdxNosun;
	private int mIdxEnd;

	public MainNosunFavoriteAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		mInflater = LayoutInflater.from(context);
		getColumnIndex(c);
	}

	public MainNosunFavoriteAdapter(Context context, Cursor c) {
		super(context, c);
		mInflater = LayoutInflater.from(context);
		getColumnIndex(c);
	}

	@Override
	public void bindView(View convertView, Context context, Cursor cursor) {
		SizeableTextView title = ViewHolder.get(convertView, R.id.title);
		SizeableTextView subtitle = ViewHolder.get(convertView, R.id.subtitle);

		String nosun = cursor.getString(mIdxNm);

		title.setText(nosun);

		String id = cursor.getString(mIdxId);
		String no = cursor.getString(mIdxNosun);
		String end = cursor.getString(mIdxEnd);
		
		subtitle.setText(no + "¹ø³ë¼± (" + id + ")" + " - " + end);

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.item_favoritelist, null);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		return view;
	}
	
	

	@Override
	public void changeCursor(Cursor arg0) {
		getColumnIndex(arg0);
		super.changeCursor(arg0);
	}

	private void getColumnIndex(Cursor cursor) {
		if(cursor==null || cursor.getCount() <= 0)
			return;
		
		mIdxNm = cursor.getColumnIndexOrThrow(UserData.STOPNAME);
		mIdxId = cursor.getColumnIndexOrThrow(UserData.STOPID);
		mIdxNosun = cursor.getColumnIndexOrThrow(UserData.NOSUN);
		mIdxEnd = cursor.getColumnIndexOrThrow(UserData.UPDOWN);
	}

}
