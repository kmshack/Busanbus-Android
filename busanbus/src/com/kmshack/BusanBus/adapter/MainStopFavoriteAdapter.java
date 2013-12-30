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

public class MainStopFavoriteAdapter extends CursorAdapter {

	private LayoutInflater mInflater;
	private Context mContext;

	private int mIdxNm;
	private int mIdxNum;

	public MainStopFavoriteAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		getColumnIndex(c);
	}

	public MainStopFavoriteAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		getColumnIndex(c);
	}

	@Override
	public void bindView(View convertView, Context context, Cursor cursor) {
		SizeableTextView title = ViewHolder.get(convertView, R.id.title);
		SizeableTextView subtitle = ViewHolder.get(convertView, R.id.subtitle);

		String nosunNm = cursor.getString(mIdxNm);
		title.setText(nosunNm);

		String nosunNo = cursor.getString(mIdxNum);
		subtitle.setText(nosunNo);

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

		mIdxNm = cursor.getColumnIndexOrThrow(UserData.NOSUNNAME);
		mIdxNum = cursor.getColumnIndexOrThrow(UserData.NOSUNNO);
	}

}
