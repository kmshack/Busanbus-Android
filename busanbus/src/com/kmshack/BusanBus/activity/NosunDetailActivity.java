package com.kmshack.BusanBus.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.kmshack.BusanBus.R;
import com.kmshack.BusanBus.adapter.HangSunAdapter;
import com.kmshack.BusanBus.database.BusDb;
import com.kmshack.BusanBus.task.BaseAsyncTask.PostListener;
import com.kmshack.BusanBus.task.HtmlAsync;

/**
 * 특정 노선 상세 
 * @author kmshack
 *
 */
public class NosunDetailActivity extends BaseActivity {

	private static final int TAB_FAVORITE = 1;
	private static final int TAB_NOSUN = 2;
	private static final int TAB_BUSSTOP = 3;
	private static final int TAB_SETTING = 4;

	private static final int DEFAULT_TAB = TAB_FAVORITE;

	private BusDb mBusDb;

	private SimpleCursorAdapter mUpAdapter;
	private SimpleCursorAdapter mDownAdapter;

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

	private TextView mTextInfo;
	private TextView mTextLocation;
	private TextView mTextUp;
	private TextView mTextDown;

	private TextView mTextNosunDetail;
	private TextView mBtnNosunMap;

	private String mNosun;
	private String mLastStopName;
	private WebView mWebView;
	private ProgressBar mLoading;

	private void setTabChange(int tab) {

		switch (tab) {

		case TAB_FAVORITE:
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

			break;

		case TAB_NOSUN:
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

			break;

		case TAB_BUSSTOP:
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

			mLastStopName = mTextUp.getText().toString();

			break;

		case TAB_SETTING:
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

			mLastStopName = mTextDown.getText().toString();

			break;
		}

	}

	View.OnClickListener mTabClickListener = new OnClickListener() {

		public void onClick(View v) {

			switch (v.getId()) {

			case R.id.nosun_tap_favorite:
				setTabChange(TAB_FAVORITE);
				break;
			case R.id.nosun_tap_nosun:
				setTabChange(TAB_NOSUN);
				doBusLocation();
				break;
			case R.id.nosun_tap_busstop:
				setTabChange(TAB_BUSSTOP);
				doNosunUp();
				break;
			case R.id.nosun_tap_setting:
				setTabChange(TAB_SETTING);
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

		setTitleLeft(mNosun + "번 노선");

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

		mTabInfo.setOnClickListener(mTabClickListener);
		mTabLocation.setOnClickListener(mTabClickListener);
		mTabUp.setOnClickListener(mTabClickListener);
		mTabDown.setOnClickListener(mTabClickListener);

		mTextNosunDetail = (TextView) findViewById(R.id.text_nosun_detail);
		mBtnNosunMap = (TextView) findViewById(R.id.btn_nosun_map);

		mUpListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				Cursor cursor = mUpAdapter.getCursor();
				cursor.moveToPosition(position);

				Intent intent = new Intent(getApplicationContext(),
						BusArriveActivity.class);
				intent.putExtra("NOSUN", mNosun);
				intent.putExtra("UNIQUEID", cursor.getString(1));
				intent.putExtra("ORD", cursor.getString(2));
				intent.putExtra("BUSSTOPNAME", cursor.getString(3));
				intent.putExtra("UPDOWN", mLastStopName);
				startActivity(intent);

			}
		});

		mDownListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				Cursor cursor = mDownAdapter.getCursor();
				cursor.moveToPosition(position);

				Intent intent = new Intent(getApplicationContext(),
						BusArriveActivity.class);
				intent.putExtra("NOSUN", mNosun);
				intent.putExtra("UNIQUEID", cursor.getString(1));
				intent.putExtra("ORD", cursor.getString(2));
				intent.putExtra("BUSSTOPNAME", cursor.getString(3));
				intent.putExtra("UPDOWN", mLastStopName);
				startActivity(intent);

			}
		});

		setTabChange(DEFAULT_TAB);
		String convertNosun = mNosun.replace("(심야)", "-F")
				.replace("(오전)", "-F").replace("(A)", "").replace("(B)", "")
				.replace("A", "").replace("B", "");

		String url = "http://121.174.75.12/03/0311.html.asp?m=2&linenm="
				+ convertNosun;

		HtmlAsync task = new HtmlAsync();

		task.setOnTapUpListener(new PostListener() {

			public void onPost(String result) {
				dismissDialog();

				if (result.indexOf("결과없음") > 0) {
					mTextNosunDetail.setText("버스정보 없음.");
				} else if (result.indexOf("버스번호") > 0) {
					String tmp1 = result.substring(result.indexOf("버스번호") - 4,
							result.indexOf("막차시간") + 10);
					String tmp2 = tmp1.replace("<br/>", "").replace("<br />",
							"");
					mTextNosunDetail.setText(tmp2);
				}

			}
		});
		showDialog();
		task.execute(url);

		mBtnNosunMap.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Cursor cursor = mBusDb.selectBusline(mNosun);
				int count = cursor.getCount();
				cursor.close();

				Intent nosunmap = new Intent(getApplicationContext(),
						NosunMapActivity.class);
				nosunmap.putExtra("NOSUN", mNosun);
				nosunmap.putExtra("COUNT", count / 4);
				nosunmap.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(nosunmap);

			}
		});

		mTextUp.setText(intent.getStringExtra("Down") + "행");
		mTextDown.setText(intent.getStringExtra("Up") + "행");

	}

	private void doNosunUp() {

		if (mUpAdapter != null)
			return;

		Cursor cursor = mBusDb.selectNosunUp(mNosun);
		mUpAdapter = new HangSunAdapter(NosunDetailActivity.this,
				R.layout.item_hangsunlist, cursor, new String[] {
						"BUSSTOPNAME", "UNIQUEID" }, new int[] { R.id.text1,
						R.id.text2 });
		mUpListView.setAdapter(mUpAdapter);

	}

	private void doNosunDown() {

		if (mDownAdapter != null)
			return;

		Cursor cursor = mBusDb.selectNosunDown(mNosun);
		mDownAdapter = new HangSunAdapter(NosunDetailActivity.this,
				R.layout.item_hangsunlist, cursor, new String[] {
						"BUSSTOPNAME", "UNIQUEID" }, new int[] { R.id.text1,
						R.id.text2 });
		mDownListView.setAdapter(mDownAdapter);

	}

	private void doBusLocation() {
		mWebView = (WebView) findViewById(R.id.bus_location_webview);
		String convertNosun = mNosun.replace("(심야)", "-F")
				.replace("(오전)", "-F").replace("(A)", "").replace("(B)", "")
				.replace("A", "").replace("B", "");

		mWebView.loadUrl("http://121.174.75.12/bims/Menu03/code04_02.aspx?bno="
				+ convertNosun);
		mWebView.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
		mLoading = (ProgressBar) findViewById(R.id.bus_location_webview_reload_progress);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress >= 100) {
					mLoading.setVisibility(View.GONE);
				} else {
					mLoading.setVisibility(View.VISIBLE);
				}

			}
		});

	}
}
