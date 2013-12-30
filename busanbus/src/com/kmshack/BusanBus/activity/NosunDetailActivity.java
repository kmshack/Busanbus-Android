package com.kmshack.BusanBus.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kmshack.BusanBus.R;
import com.kmshack.BusanBus.adapter.HangSunAdapter;
import com.kmshack.BusanBus.database.BusDb;
import com.kmshack.BusanBus.task.BaseAsyncTask.PostListener;
import com.kmshack.BusanBus.task.HtmlAsync;

public class NosunDetailActivity extends BaseActivity {

	private static final int TAB_INFO = 1;
	private static final int TAB_REALTIME = 2;
	private static final int TAB_UP = 3;
	private static final int TAB_DOWN = 4;

	private static final int DEFAULT_TAB = TAB_INFO;
	private GoogleMap mGoogleMap;

	private BusDb mBusDb;

	private SimpleCursorAdapter mUpAdapter;
	private SimpleCursorAdapter mDownAdapter;

	public Cursor mCursor;

	private ListView mUpListView;
	private ListView mDownListView;

	private LinearLayout mTabInfo;
	private LinearLayout mTabLocation;
	private LinearLayout mTabUp;
	private LinearLayout mTabDown;

	private LinearLayout mLayoutInfo;
	private LinearLayout mLayoutLocation;
	private LinearLayout mLayoutUp;
	private LinearLayout mLayoutDown;

	private ImageView mImageInfo;
	private ImageView mImageLocation;
	private ImageView mImageUp;
	private ImageView mImageDown;

	private TextView mTextInfo;
	private TextView mTextLocation;
	private TextView mTextUp;
	private TextView mTextDown;

	private TextView mTextNosunDetail;
	private TextView mBtnNosunMap;

	private String mNosun;
	private String mRealTimeNoSun;
	private String mParamStopId;
	private String mLastStopName;
	private WebView mWebview;
	private ProgressBar mProgress;

	private RelativeLayout mBtnWebRefresh;

	private void setTabChange(int tab) {

		switch (tab) {

		case TAB_INFO:
			mLayoutInfo.setVisibility(View.VISIBLE);
			mLayoutLocation.setVisibility(View.GONE);
			mLayoutUp.setVisibility(View.GONE);
			mLayoutDown.setVisibility(View.GONE);

			mTabInfo.setBackgroundColor(Color.parseColor("#7F7F72"));
			mTabLocation.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabUp.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabDown.setBackgroundColor(Color.parseColor("#F7F7F0"));

			mTextInfo.setTextColor(Color.parseColor("#FFFFFF"));
			mTextLocation.setTextColor(Color.parseColor("#7F7F72"));
			mTextUp.setTextColor(Color.parseColor("#7F7F72"));
			mTextDown.setTextColor(Color.parseColor("#7F7F72"));

			mImageInfo.setImageResource(R.drawable.ic_main_tab_info_pressed);
			mImageLocation.setImageResource(R.drawable.ic_main_tab_time_normal);
			mImageUp.setImageResource(R.drawable.ic_main_tab_left_normal);
			mImageDown.setImageResource(R.drawable.ic_main_tab_right_normal);

			initilizeMap();

			break;

		case TAB_REALTIME:
			mLayoutInfo.setVisibility(View.GONE);
			mLayoutLocation.setVisibility(View.VISIBLE);
			mLayoutUp.setVisibility(View.GONE);
			mLayoutDown.setVisibility(View.GONE);

			mTabInfo.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabLocation.setBackgroundColor(Color.parseColor("#7F7F72"));
			mTabUp.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabDown.setBackgroundColor(Color.parseColor("#F7F7F0"));

			mTextInfo.setTextColor(Color.parseColor("#7F7F72"));
			mTextLocation.setTextColor(Color.parseColor("#FFFFFF"));
			mTextUp.setTextColor(Color.parseColor("#7F7F72"));
			mTextDown.setTextColor(Color.parseColor("#7F7F72"));

			mImageInfo.setImageResource(R.drawable.ic_main_tab_info_normal);
			mImageLocation.setImageResource(R.drawable.ic_main_tab_time_pressed);
			mImageUp.setImageResource(R.drawable.ic_main_tab_left_normal);
			mImageDown.setImageResource(R.drawable.ic_main_tab_right_normal);

			break;

		case TAB_UP:
			mLayoutInfo.setVisibility(View.GONE);
			mLayoutLocation.setVisibility(View.GONE);
			mLayoutUp.setVisibility(View.VISIBLE);
			mLayoutDown.setVisibility(View.GONE);

			mTabInfo.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabLocation.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabUp.setBackgroundColor(Color.parseColor("#7F7F72"));
			mTabDown.setBackgroundColor(Color.parseColor("#F7F7F0"));

			mTextInfo.setTextColor(Color.parseColor("#7F7F72"));
			mTextLocation.setTextColor(Color.parseColor("#7F7F72"));
			mTextUp.setTextColor(Color.parseColor("#FFFFFF"));
			mTextDown.setTextColor(Color.parseColor("#7F7F72"));

			mImageInfo.setImageResource(R.drawable.ic_main_tab_info_normal);
			mImageLocation.setImageResource(R.drawable.ic_main_tab_time_normal);
			mImageUp.setImageResource(R.drawable.ic_main_tab_left_pressed);
			mImageDown.setImageResource(R.drawable.ic_main_tab_right_normal);

			mLastStopName = mTextUp.getText().toString();

			break;

		case TAB_DOWN:
			mLayoutInfo.setVisibility(View.GONE);
			mLayoutLocation.setVisibility(View.GONE);
			mLayoutUp.setVisibility(View.GONE);
			mLayoutDown.setVisibility(View.VISIBLE);

			mTabInfo.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabLocation.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabUp.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabDown.setBackgroundColor(Color.parseColor("#7F7F72"));

			mTextInfo.setTextColor(Color.parseColor("#7F7F72"));
			mTextLocation.setTextColor(Color.parseColor("#7F7F72"));
			mTextUp.setTextColor(Color.parseColor("#7F7F72"));
			mTextDown.setTextColor(Color.parseColor("#FFFFFF"));

			mImageInfo.setImageResource(R.drawable.ic_main_tab_info_normal);
			mImageLocation.setImageResource(R.drawable.ic_main_tab_time_normal);
			mImageUp.setImageResource(R.drawable.ic_main_tab_left_normal);
			mImageDown.setImageResource(R.drawable.ic_main_tab_right_pressed);

			mLastStopName = mTextDown.getText().toString();

			break;
		}

	}

	View.OnClickListener mTabClickListener = new OnClickListener() {

		public void onClick(View v) {

			switch (v.getId()) {

			case R.id.nosun_tap_favorite:
				setTabChange(TAB_INFO);
				break;
			case R.id.nosun_tap_nosun:
				setTabChange(TAB_REALTIME);
				doBusLocation();
				break;
			case R.id.nosun_tap_busstop:
				setTabChange(TAB_UP);
				doNosunUp();
				break;
			case R.id.nosun_tap_setting:
				setTabChange(TAB_DOWN);
				doNosunDown();
				break;

			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nosun_common);

		mBusDb = BusDb.getInstance(getApplicationContext());

		Intent intent = getIntent();
		mNosun = intent.getStringExtra("NoSun");
		mRealTimeNoSun = intent.getStringExtra("RealTimeNoSun");

		if (intent.hasExtra("Id")) {
			mParamStopId = intent.getStringExtra("Id");
		} else {
			mParamStopId = "";
		}

		tracker.trackPageView("/NosunDetail");

		setTitle(mNosun + "번 노선");

		mImageInfo = (ImageView) findViewById(R.id.nosun_tap_favorite_image);
		mImageLocation = (ImageView) findViewById(R.id.nosun_tap_nosun_image);
		mImageUp = (ImageView) findViewById(R.id.nosun_tap_busstop_image);
		mImageDown = (ImageView) findViewById(R.id.nosun_tap_setting_image);

		mTextInfo = (TextView) findViewById(R.id.nosun_tap_favorite_text);
		mTextLocation = (TextView) findViewById(R.id.nosun_tap_nosun_text);
		mTextUp = (TextView) findViewById(R.id.nosun_tap_busstop_text);
		mTextDown = (TextView) findViewById(R.id.nosun_tap_setting_text);

		mUpListView = (ListView) findViewById(R.id.lv_up);
		mDownListView = (ListView) findViewById(R.id.lv_down);

		mTabInfo = (LinearLayout) findViewById(R.id.nosun_tap_favorite);
		mTabLocation = (LinearLayout) findViewById(R.id.nosun_tap_nosun);
		mTabUp = (LinearLayout) findViewById(R.id.nosun_tap_busstop);
		mTabDown = (LinearLayout) findViewById(R.id.nosun_tap_setting);

		mLayoutInfo = (LinearLayout) findViewById(R.id.layout_nosun_favorite);
		mLayoutLocation = (LinearLayout) findViewById(R.id.layout_nosun_nosun_search);
		mLayoutUp = (LinearLayout) findViewById(R.id.layout_nosun_busstop_search);
		mLayoutDown = (LinearLayout) findViewById(R.id.layout_nosun_setting);

		mBtnWebRefresh = (RelativeLayout) findViewById(R.id.btn_reflash);
		mBtnWebRefresh.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				doBusLocation();
			}
		});

		mTabInfo.setOnClickListener(mTabClickListener);
		mTabLocation.setOnClickListener(mTabClickListener);
		mTabUp.setOnClickListener(mTabClickListener);
		mTabDown.setOnClickListener(mTabClickListener);

		mTextNosunDetail = (TextView) findViewById(R.id.text_nosun_detail);
		mBtnNosunMap = (TextView) findViewById(R.id.btn_nosun_map);

		mUpListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				Cursor cursor = mUpAdapter.getCursor();
				cursor.moveToPosition(position);

				Intent intent = new Intent(getApplicationContext(), BusArriveActivity.class);
				intent.putExtra("NOSUN", mNosun);
				intent.putExtra("UNIQUEID", cursor.getString(1));
				intent.putExtra("ORD", cursor.getString(2));
				intent.putExtra("BUSSTOPNAME", cursor.getString(3));
				intent.putExtra("UPDOWN", mLastStopName);
				startActivity(intent);

			}
		});

		mDownListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				Cursor cursor = mDownAdapter.getCursor();
				cursor.moveToPosition(position);

				Intent intent = new Intent(getApplicationContext(), BusArriveActivity.class);
				intent.putExtra("NOSUN", mNosun);
				intent.putExtra("UNIQUEID", cursor.getString(1));
				intent.putExtra("ORD", cursor.getString(2));
				intent.putExtra("BUSSTOPNAME", cursor.getString(3));
				intent.putExtra("UPDOWN", mLastStopName);
				startActivity(intent);

			}
		});

		setTabChange(DEFAULT_TAB);

		String url = "http://121.174.75.12/03/0311.html.asp?m=2&linenm=" + mRealTimeNoSun;

		HtmlAsync task = new HtmlAsync();

		task.setOnTapUpListener(new PostListener() {

			public void onPost(String result) {
				dismissDialog();

				if (result.indexOf("결과없음") > 0) {
					mTextNosunDetail.setText("버스정보 없음.");
				} else if (result.indexOf("버스번호") > 0) {
					String tmp1 = result.substring(result.indexOf("버스번호") - 4, result.indexOf("막차시간") + 10);
					String tmp2 = tmp1.replace("<br/>", "").replace("<br />", "");
					mTextNosunDetail.setText(tmp2);
				}

			}
		});
		showDialog();
		task.execute(url);

		mBtnNosunMap.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent nosunmap = new Intent(getApplicationContext(), NosunMapActivity.class);
				nosunmap.putExtra("NOSUN", mNosun);
				nosunmap.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(nosunmap);

			}
		});

		String down = intent.getStringExtra("Down");
		String up = intent.getStringExtra("Up");

		if (TextUtils.isEmpty(down)) {
			down = "상행";
			up = "하행";
		} else {
			down = down + "행";
			up = up + "행";
		}

		mTextUp.setText(down);
		mTextDown.setText(up);

	}

	private void doNosunUp() {

		if (mUpAdapter != null)
			return;

		Cursor cursor = mBusDb.selectNosunUp(mNosun);
		mUpAdapter = new HangSunAdapter(NosunDetailActivity.this, R.layout.item_hangsunlist, cursor, new String[] { "BUSSTOPNAME", "UNIQUEID" }, new int[] {
				R.id.text1, R.id.text2 });
		mUpListView.setAdapter(mUpAdapter);

	}

	private void doNosunDown() {

		if (mDownAdapter != null)
			return;

		Cursor cursor = mBusDb.selectNosunDown(mNosun);
		mDownAdapter = new HangSunAdapter(NosunDetailActivity.this, R.layout.item_hangsunlist, cursor, new String[] { "BUSSTOPNAME", "UNIQUEID" }, new int[] {
				R.id.text1, R.id.text2 });
		mDownListView.setAdapter(mDownAdapter);

	}

	private void doBusLocation() {

		if (mProgress == null) {
			mProgress = (ProgressBar) findViewById(R.id.bus_location_webview_reload_progress);
		}

		if (mWebview == null) {
			String url = "http://121.174.75.12/bims/Menu03/code04_02.aspx?bno=" + mRealTimeNoSun;

			Log.i("BusanBus", "Load URL = " + url);

			mWebview = (WebView) findViewById(R.id.bus_location_webview);
			mWebview.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
			mWebview.getSettings().setJavaScriptEnabled(true);
			mWebview.setWebChromeClient(new WebChromeClient() {

				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					if (newProgress >= 100) {
						mProgress.setVisibility(View.GONE);
					} else {
						mProgress.setVisibility(View.VISIBLE);
					}

				}
			});
			mWebview.loadUrl(url);
		} else {
			mWebview.reload();
		}

	}

	@Override
	protected void onDestroy() {
		cursorClose();
		super.onDestroy();
	}

	private void cursorClose() {
		if (mCursor != null && !mCursor.isClosed())
			mCursor.close();
	}

	private void loadQueryForMap() {

		if (mGoogleMap == null)
			return;

		cursorClose();

		mCursor = mBusDb.selectBusline(mNosun);

		if (mCursor.moveToFirst()) {

			int count = mCursor.getCount();

			int center = count / 2;
			int doubleCenter = count / 4;

			LatLng centerLatlng = null;
			LatLng beforeLatlng = null;
			LatLng currentLatlng = null;

			int lineColorForUpper = Color.parseColor("#550000FF");
			int lineColorForDownner = Color.parseColor("#5500FF00");

			boolean isGoId = !TextUtils.isEmpty(mParamStopId);

			while (mCursor.moveToNext()) {

				int position = mCursor.getPosition();
				double latitude = mCursor.getDouble(8);
				double longitude = mCursor.getDouble(7);

				String id = mCursor.getString(9);
				String stopid = mCursor.getString(10);
				String name = mCursor.getString(3);

				currentLatlng = new LatLng(latitude, longitude);

				MarkerOptions marker = new MarkerOptions().position(currentLatlng).title(position + ". " + name).snippet(id);
				if (center < position) {
					marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
					if (beforeLatlng != null) {
						mGoogleMap.addPolyline(new PolylineOptions().add(beforeLatlng, currentLatlng).geodesic(true).width(7).color(lineColorForUpper));
					}

				} else {
					marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
					if (beforeLatlng != null) {
						mGoogleMap.addPolyline(new PolylineOptions().add(beforeLatlng, currentLatlng).geodesic(true).width(7).color(lineColorForDownner));
					}
				}

				beforeLatlng = currentLatlng;

				if (isGoId) {
					if (stopid.equals(mParamStopId)) {
						mGoogleMap.addMarker(marker).showInfoWindow();
						centerLatlng = currentLatlng;
					} else {

						if (position == doubleCenter) {
							if (centerLatlng == null)
								centerLatlng = currentLatlng;
						}

						mGoogleMap.addMarker(marker);
					}
				} else {

					if (position == doubleCenter) {
						if (centerLatlng == null)
							centerLatlng = currentLatlng;
					}

					if (position == 1)
						mGoogleMap.addMarker(marker).showInfoWindow();
					else
						mGoogleMap.addMarker(marker);
				}

			}

			CameraPosition INIT = new CameraPosition.Builder().target(centerLatlng).zoom(11F).bearing(0F) // orientation
					.tilt(0F) // viewing angle
					.build();
			mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(INIT));

			mGoogleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

				public void onInfoWindowClick(Marker marker) {
					Intent intent = new Intent(getApplicationContext(), BusstopDetailActivity.class);
					intent.putExtra("BusStop", marker.getSnippet());
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			});
		}
	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		if (mGoogleMap == null) {
			mGoogleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

			if (mGoogleMap == null) {
			} else {
				mGoogleMap.setMyLocationEnabled(true);
				mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
				mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
				mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

				CameraPosition INIT = new CameraPosition.Builder().target(new LatLng(35.1796F, 129.076F)).zoom(8F).bearing(0F) // orientation
						.tilt(0F) // viewing angle
						.build();
				mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(INIT));

				loadQueryForMap();

			}
		}
	}

}
