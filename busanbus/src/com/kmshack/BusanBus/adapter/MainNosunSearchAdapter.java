package com.kmshack.BusanBus.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.kmshack.BusanBus.R;
import com.kmshack.BusanBus.utils.ViewHolder;
import com.kmshack.BusanBus.view.SizeableTextView;

public class MainNosunSearchAdapter extends CursorAdapter {

	private LayoutInflater mInflater;
	private Context mContext;

	private int mIdxNo;
	private int mIdxStart;
	private int mIdxEnd;

	public MainNosunSearchAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		getColumnIndex(c);
	}

	public MainNosunSearchAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		getColumnIndex(c);
	}

	@Override
	public void bindView(View convertView, Context context, Cursor cursor) {
		// ¡ê
		SizeableTextView title = ViewHolder.get(convertView, R.id.title);
		SizeableTextView subtitle = ViewHolder.get(convertView, R.id.subtitle);

		String nosun = cursor.getString(mIdxNo);

		title.setText(nosun);

		String start = cursor.getString(mIdxStart);
		String end = cursor.getString(mIdxEnd);

		if (!TextUtils.isEmpty(start)) {
			subtitle.setText(start + " ¡ê " + end);
			subtitle.setVisibility(View.VISIBLE);
		} else {
			subtitle.setVisibility(View.GONE);
		}

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.item_busnosearchlist, null);

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
		
		mIdxNo = cursor.getColumnIndexOrThrow("NOSUNNUM");
		mIdxStart = cursor.getColumnIndexOrThrow("START");
		mIdxEnd = cursor.getColumnIndexOrThrow("END");
	}

}
