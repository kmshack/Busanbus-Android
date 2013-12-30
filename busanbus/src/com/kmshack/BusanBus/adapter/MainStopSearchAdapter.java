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

public class MainStopSearchAdapter extends CursorAdapter {

	private LayoutInflater mInflater;
	private Context mContext;

	private int mIdxNm;
	private int mIdxId;

	public MainStopSearchAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		getColumnIndex(c);
	}

	public MainStopSearchAdapter(Context context, Cursor c) {
		super(context, c);
		mContext = context;
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

		if (!TextUtils.isEmpty(id) && !id.equals("0")) {
			subtitle.setText(id);
			subtitle.setVisibility(View.VISIBLE);
		} else {
			subtitle.setText("알 수 없는 정류소");
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
		
		mIdxNm = cursor.getColumnIndexOrThrow("BUSSTOPNAME");
		mIdxId = cursor.getColumnIndexOrThrow("UNIQUEID");
	}

}
