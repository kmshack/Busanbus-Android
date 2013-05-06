package com.kmshack.BusanBus.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kmshack.BusanBus.Config;
import com.kmshack.BusanBus.R;
import com.kmshack.BusanBus.database.BusDb;
import com.kmshack.BusanBus.database.BusanBusPrefrence;
import com.kmshack.BusanBus.database.UserDb;

/**
 * 메인 검색 화면
 * @author kmshack
 *
 */
public class SearchMainActivity extends BaseActivity {

	private static final int TAB_FAVORITE = 1;
	private static final int TAB_NOSUN = 2;
	private static final int TAB_BUSSTOP = 3;
	private static final int TAB_SETTING = 4;

	public static final int FAVORITE_TAB_NOSUN = 5;
	public static final int FAVORITE_TAB_BUSSTOP = 6;

	private static final int DEFAULT_TAB = TAB_NOSUN;

	private BusDb mBusDb;
	private UserDb mUserDb;
	private BusanBusPrefrence mBusanBusPrefrence;
	private InputMethodManager mInputMethodManager;

	private LinearLayout mNosunSearchView;
	private LinearLayout mBusstopSearchView;

	private SimpleCursorAdapter mNosunAdapter;
	private SimpleCursorAdapter mBusStopAdapter;
	private SimpleCursorAdapter mFavoriteAdapter;

	private ListView mNosunListView;
	private ListView mBusStopListView;
	private ListView mFavoriteListView;

	private EditText mNosunEditText;
	private EditText mBusStopEditText;

	private LinearLayout mTabFavorite;
	private LinearLayout mTabNosun;
	private LinearLayout mTabBusstop;
	private LinearLayout mTabSetting;

	private LinearLayout mLayoutFavorite;
	private LinearLayout mLayoutNosun;
	private LinearLayout mLayoutBusstop;
	private LinearLayout mLayoutSetting;

	private TextView mTextFavorite;
	private TextView mTextNosun;
	private TextView mTextBusstop;
	private TextView mTextSetting;

	private TextView mTextSettingNotice;
	private TextView mTextSettingUpdate;

	private TextView mFavoriteImageNosun;
	private TextView mFavoriteImageBusstop;

	private LinearLayout mLayoutSettingTextSize;
	private LinearLayout mLayoutSettingArrive;
	private LinearLayout mLayoutSettingFavorite;
	private LinearLayout mLayoutSettingFavoriteLocation;

	private TextView mTextViewSettingTextSize;
	private ImageView mCheckSettingArrive;
	private ImageView mCheckSettingFavorite;
	private ImageView mCheckSettingFavoriteLocation;

	private int mFavoriteMode = DEFAULT_TAB;

	private Handler mHandler;
	private int mTabMode;

	private void setFavoriteTabChange(int tab) {

		mFavoriteMode = tab;

		if (mBusanBusPrefrence.getIsFavoriteLocation())
			mBusanBusPrefrence.setFavoriteLocation(tab);

		switch (tab) {
		case FAVORITE_TAB_NOSUN:
			mFavoriteImageNosun
					.setBackgroundResource(R.drawable.favorite_left_press);
			mFavoriteImageNosun.setTextColor(Color.parseColor("#F7F7F0"));
			mFavoriteImageBusstop
					.setBackgroundResource(R.drawable.btn_favorite_right);
			mFavoriteImageBusstop.setTextColor(Color.parseColor("#7F7F72"));

			break;

		case FAVORITE_TAB_BUSSTOP:
			mFavoriteImageNosun
					.setBackgroundResource(R.drawable.btn_favorite_left);
			mFavoriteImageNosun.setTextColor(Color.parseColor("#7F7F72"));
			mFavoriteImageBusstop
					.setBackgroundResource(R.drawable.favorite_right_press);
			mFavoriteImageBusstop.setTextColor(Color.parseColor("#F7F7F0"));
			break;
		}
	}

	private void setTabChange(int tab) {

		mInputMethodManager.hideSoftInputFromWindow(
				mNosunEditText.getWindowToken(), 0);
		mTabMode = tab;

		switch (tab) {

		case TAB_FAVORITE:
			mLayoutFavorite.setVisibility(View.VISIBLE);
			mLayoutNosun.setVisibility(View.GONE);
			mLayoutBusstop.setVisibility(View.GONE);
			mLayoutSetting.setVisibility(View.GONE);

			mTabFavorite.setBackgroundColor(Color.parseColor("#7F7F72"));
			mTabNosun.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabBusstop.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabSetting.setBackgroundColor(Color.parseColor("#F7F7F0"));

			mTextFavorite.setTextColor(Color.parseColor("#FFFFFF"));
			mTextNosun.setTextColor(Color.parseColor("#7F7F72"));
			mTextBusstop.setTextColor(Color.parseColor("#7F7F72"));
			mTextSetting.setTextColor(Color.parseColor("#7F7F72"));

			if (mBusanBusPrefrence.getIsFavoriteLocation())
				setFavoriteTabChange(mBusanBusPrefrence.getFavoriteLocation());
			else
				setFavoriteTabChange(FAVORITE_TAB_NOSUN);

			switch (mFavoriteMode) {
			case FAVORITE_TAB_NOSUN:
				favoriteBusStop();
				favoriteNosun();
				break;

			case FAVORITE_TAB_BUSSTOP:
				favoriteNosun();
				favoriteBusStop();
				break;

			}
			break;

		case TAB_NOSUN:
			mLayoutFavorite.setVisibility(View.GONE);
			mLayoutNosun.setVisibility(View.VISIBLE);
			mLayoutBusstop.setVisibility(View.GONE);
			mLayoutSetting.setVisibility(View.GONE);
			mNosunEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

			mTabFavorite.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabNosun.setBackgroundColor(Color.parseColor("#7F7F72"));
			mTabBusstop.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabSetting.setBackgroundColor(Color.parseColor("#F7F7F0"));

			mTextFavorite.setTextColor(Color.parseColor("#7F7F72"));
			mTextNosun.setTextColor(Color.parseColor("#FFFFFF"));
			mTextBusstop.setTextColor(Color.parseColor("#7F7F72"));
			mTextSetting.setTextColor(Color.parseColor("#7F7F72"));

			break;

		case TAB_BUSSTOP:
			mLayoutFavorite.setVisibility(View.GONE);
			mLayoutNosun.setVisibility(View.GONE);
			mLayoutBusstop.setVisibility(View.VISIBLE);
			mLayoutSetting.setVisibility(View.GONE);
			mBusStopEditText.setInputType(InputType.TYPE_CLASS_TEXT);

			mTabFavorite.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabNosun.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabBusstop.setBackgroundColor(Color.parseColor("#7F7F72"));
			mTabSetting.setBackgroundColor(Color.parseColor("#F7F7F0"));

			mTextFavorite.setTextColor(Color.parseColor("#7F7F72"));
			mTextNosun.setTextColor(Color.parseColor("#7F7F72"));
			mTextBusstop.setTextColor(Color.parseColor("#FFFFFF"));
			mTextSetting.setTextColor(Color.parseColor("#7F7F72"));
			
			break;

		case TAB_SETTING:
			mLayoutFavorite.setVisibility(View.GONE);
			mLayoutNosun.setVisibility(View.GONE);
			mLayoutBusstop.setVisibility(View.GONE);
			mLayoutSetting.setVisibility(View.VISIBLE);

			mTabFavorite.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabNosun.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabBusstop.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabSetting.setBackgroundColor(Color.parseColor("#7F7F72"));

			mTextFavorite.setTextColor(Color.parseColor("#7F7F72"));
			mTextNosun.setTextColor(Color.parseColor("#7F7F72"));
			mTextBusstop.setTextColor(Color.parseColor("#7F7F72"));
			mTextSetting.setTextColor(Color.parseColor("#FFFFFF"));

			break;
		}

	}

	private View.OnClickListener mSettingClick = new OnClickListener() {

		public void onClick(View v) {

			switch (v.getId()) {

			case R.id.setting_notice:
				Intent webview = new Intent(getApplicationContext(),
						WebActivity.class);
				webview.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(webview);
				break;
			case R.id.setting_update:
				Intent intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(Config.UPDATE_URL));
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			case R.id.setting_textsize_sun:
				Intent fontSizeIntent = new Intent(getApplicationContext(),
						TextSizeSettingActivity.class);
				fontSizeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(fontSizeIntent);
				break;
			case R.id.setting_arrive_sun:

				mBusanBusPrefrence.setIsArriveSort(!mBusanBusPrefrence
						.getIsArriveSort());

				if (mBusanBusPrefrence.getIsArriveSort())
					mCheckSettingArrive
							.setImageResource(R.drawable.btn_setting_checkbox_check);
				else
					mCheckSettingArrive
							.setImageResource(R.drawable.btn_setting_checkbox_uncheck);

				break;

			case R.id.setting_favorite_start:

				mBusanBusPrefrence.setIsFavoriteStart(!mBusanBusPrefrence
						.getIsFavoriteStart());

				if (mBusanBusPrefrence.getIsFavoriteStart())
					mCheckSettingFavorite
							.setImageResource(R.drawable.btn_setting_checkbox_check);
				else
					mCheckSettingFavorite
							.setImageResource(R.drawable.btn_setting_checkbox_uncheck);

				break;

			case R.id.setting_favorite_location:

				mBusanBusPrefrence.setIsFavoriteLocation(!mBusanBusPrefrence
						.getIsFavoriteLocation());

				if (mBusanBusPrefrence.getIsFavoriteLocation())
					mCheckSettingFavoriteLocation
							.setImageResource(R.drawable.btn_setting_checkbox_check);
				else
					mCheckSettingFavoriteLocation
							.setImageResource(R.drawable.btn_setting_checkbox_uncheck);

				break;

			}

		}
	};

	private View.OnClickListener mTabClickListener = new OnClickListener() {

		public void onClick(View v) {

			switch (v.getId()) {

			case R.id.main_tap_favorite:
				setTabChange(TAB_FAVORITE);
				break;
			case R.id.main_tap_nosun:
				setTabChange(TAB_NOSUN);
				break;
			case R.id.main_tap_busstop:
				setTabChange(TAB_BUSSTOP);
				break;
			case R.id.main_tap_setting:
				setTabChange(TAB_SETTING);
				break;

			case R.id.favorite_tab_nosun:
				setFavoriteTabChange(FAVORITE_TAB_NOSUN);
				favoriteNosun();
				break;

			case R.id.favorite_tab_busstop:
				setFavoriteTabChange(FAVORITE_TAB_BUSSTOP);
				favoriteBusStop();
				break;

			}

		}
	};

	@Override
	protected void onResume() {
		super.onResume();

		if (mTabMode == TAB_FAVORITE) {

			switch (mFavoriteMode) {
			case FAVORITE_TAB_NOSUN:
				favoriteBusStop();
				favoriteNosun();
				break;

			case FAVORITE_TAB_BUSSTOP:
				favoriteNosun();
				favoriteBusStop();
				break;

			}
		} else if (mTabMode == TAB_SETTING) {
			loadSettingTextSizeSelect();
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_common);
		disableBack();
		setTitleLeft("부산버스");

		mBusDb = BusDb.getInstance(getApplicationContext());
		mUserDb = UserDb.getInstance(getApplicationContext());
		mBusanBusPrefrence = BusanBusPrefrence
				.getInstance(getApplicationContext());
		mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				switch (msg.what) {

				case TAB_NOSUN:
					searchNosun((String) msg.obj);
					break;

				case TAB_BUSSTOP:
					searchBusStop((String) msg.obj);
					break;
				}

			}
		};

		mFavoriteImageNosun = (TextView) findViewById(R.id.favorite_tab_nosun);
		mFavoriteImageBusstop = (TextView) findViewById(R.id.favorite_tab_busstop);

		mTextFavorite = (TextView) findViewById(R.id.main_tap_favorite_text);
		mTextNosun = (TextView) findViewById(R.id.main_tap_nosun_text);
		mTextBusstop = (TextView) findViewById(R.id.main_tap_busstop_text);
		mTextSetting = (TextView) findViewById(R.id.main_tap_setting_text);

		mNosunListView = (ListView) findViewById(R.id.lv_nosun);
		mBusStopListView = (ListView) findViewById(R.id.lv_busstop);
		mFavoriteListView = (ListView) findViewById(R.id.lv_favorite);

		mNosunListView.setEmptyView((TextView) findViewById(R.id.nosun_empty));
		mBusStopListView
				.setEmptyView((TextView) findViewById(R.id.busstop_empty));
		mFavoriteListView
				.setEmptyView((TextView) findViewById(R.id.favorite_empty));

		mTextSettingNotice = (TextView) findViewById(R.id.setting_notice);
		mTextSettingUpdate = (TextView) findViewById(R.id.setting_update);

		mLayoutSettingTextSize = (LinearLayout) findViewById(R.id.setting_textsize_sun);
		mLayoutSettingArrive = (LinearLayout) findViewById(R.id.setting_arrive_sun);
		mLayoutSettingFavorite = (LinearLayout) findViewById(R.id.setting_favorite_start);
		mLayoutSettingFavoriteLocation = (LinearLayout) findViewById(R.id.setting_favorite_location);

		mTextViewSettingTextSize = (TextView) findViewById(R.id.setting_textsize_value);
		mCheckSettingArrive = (ImageView) findViewById(R.id.setting_check_arrive_sun);
		mCheckSettingFavorite = (ImageView) findViewById(R.id.setting_check_favorite_start);
		mCheckSettingFavoriteLocation = (ImageView) findViewById(R.id.setting_check_favorite_location);

		mTextSettingNotice.setOnClickListener(mSettingClick);
		mTextSettingUpdate.setOnClickListener(mSettingClick);

		mLayoutSettingTextSize.setOnClickListener(mSettingClick);
		mLayoutSettingArrive.setOnClickListener(mSettingClick);
		mLayoutSettingFavorite.setOnClickListener(mSettingClick);
		mLayoutSettingFavoriteLocation.setOnClickListener(mSettingClick);

		if (mBusanBusPrefrence.getIsArriveSort())
			mCheckSettingArrive
					.setImageResource(R.drawable.btn_setting_checkbox_check);
		else
			mCheckSettingArrive
					.setImageResource(R.drawable.btn_setting_checkbox_uncheck);

		if (mBusanBusPrefrence.getIsFavoriteStart())
			mCheckSettingFavorite
					.setImageResource(R.drawable.btn_setting_checkbox_check);
		else
			mCheckSettingFavorite
					.setImageResource(R.drawable.btn_setting_checkbox_uncheck);

		if (mBusanBusPrefrence.getIsFavoriteLocation())
			mCheckSettingFavoriteLocation
					.setImageResource(R.drawable.btn_setting_checkbox_check);
		else
			mCheckSettingFavoriteLocation
					.setImageResource(R.drawable.btn_setting_checkbox_uncheck);

		mNosunSearchView = (LinearLayout) LayoutInflater.from(
				getApplicationContext()).inflate(R.layout.search_bar_nosun,
				null);
		mNosunEditText = (EditText) mNosunSearchView
				.findViewById(R.id.et_search_nosun);
		mNosunEditText.setHint("노선번호 검색");
		mNosunEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		mNosunEditText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mHandler.removeMessages(TAB_NOSUN);
				Message msg = Message.obtain(mHandler, TAB_NOSUN, s.toString());
				mHandler.sendMessageDelayed(msg, 200);
			}

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

		});

		((LinearLayout) findViewById(R.id.search_nosun))
				.addView(mNosunSearchView);

		mNosunListView.setOnScrollListener(new OnScrollListener() {
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
			}

			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				mInputMethodManager.hideSoftInputFromWindow(
						mNosunEditText.getWindowToken(), 0);
			}
		});

		mNosunListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				Cursor cursor = mNosunAdapter.getCursor();
				cursor.moveToPosition(position);

				Intent intent = new Intent(getApplicationContext(),
						NosunDetailActivity.class);
				intent.putExtra("NoSun", cursor.getString(1));
				intent.putExtra("Up", cursor.getString(2));
				intent.putExtra("Down", cursor.getString(3));
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

			}
		});

		mBusstopSearchView = (LinearLayout) LayoutInflater.from(
				getApplicationContext()).inflate(R.layout.search_bar_busstop,
				null);
		mBusStopEditText = (EditText) mBusstopSearchView
				.findViewById(R.id.et_search_busstop);
		mBusStopEditText.setHint("정류소명/번호 검색");
		mBusStopEditText.setInputType(InputType.TYPE_CLASS_TEXT);
		mBusStopEditText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mHandler.removeMessages(TAB_BUSSTOP);
				Message msg = Message.obtain(mHandler, TAB_BUSSTOP,
						s.toString());
				mHandler.sendMessageDelayed(msg, 200);
			}

			public void afterTextChanged(Editable s) {
				// searchBusStop();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

		});

		((LinearLayout) findViewById(R.id.search_busstop))
				.addView(mBusstopSearchView);

		mBusStopListView.setOnScrollListener(new OnScrollListener() {
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
			}

			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				mInputMethodManager.hideSoftInputFromWindow(
						mBusStopEditText.getWindowToken(), 0);
			}
		});

		mBusStopListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				Cursor cursor = mBusStopAdapter.getCursor();
				cursor.moveToPosition(position);

				Intent intent = new Intent(getApplicationContext(),
						BusstopDetailActivity.class);
				intent.putExtra("BusStop", cursor.getString(2).toString());
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

			}
		});

		mFavoriteListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {

				Cursor cursor = mFavoriteAdapter.getCursor();

				if (cursor.getCount() > 0) {

					cursor.moveToPosition(position);

					Intent intent = null;

					switch (mFavoriteMode) {
					case FAVORITE_TAB_NOSUN:
						intent = new Intent(getApplicationContext(),
								BusArriveActivity.class);
						intent.putExtra("NOSUN", cursor.getString(1));
						intent.putExtra("UNIQUEID", cursor.getString(2));
						intent.putExtra("BUSSTOPNAME", cursor.getString(3));
						intent.putExtra("UPDOWN", cursor.getString(4));
						intent.putExtra("ORD", cursor.getString(6));

						break;

					case FAVORITE_TAB_BUSSTOP:
						intent = new Intent(getApplicationContext(),
								BusstopDetailActivity.class);
						intent.putExtra("BusStop", cursor.getString(2));

						break;
					}

					if (intent != null) {
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				}
			}
		});

		mTabFavorite = (LinearLayout) findViewById(R.id.main_tap_favorite);
		mTabNosun = (LinearLayout) findViewById(R.id.main_tap_nosun);
		mTabBusstop = (LinearLayout) findViewById(R.id.main_tap_busstop);
		mTabSetting = (LinearLayout) findViewById(R.id.main_tap_setting);

		mLayoutFavorite = (LinearLayout) findViewById(R.id.layout_main_favorite);
		mLayoutNosun = (LinearLayout) findViewById(R.id.layout_main_nosun_search);
		mLayoutBusstop = (LinearLayout) findViewById(R.id.layout_main_busstop_search);
		mLayoutSetting = (LinearLayout) findViewById(R.id.layout_main_setting);

		mTabFavorite.setOnClickListener(mTabClickListener);
		mTabNosun.setOnClickListener(mTabClickListener);
		mTabBusstop.setOnClickListener(mTabClickListener);
		mTabSetting.setOnClickListener(mTabClickListener);
		mFavoriteImageNosun.setOnClickListener(mTabClickListener);
		mFavoriteImageBusstop.setOnClickListener(mTabClickListener);

		searchNosun("");
		searchBusStop("부산");

		if (mBusanBusPrefrence.getIsFavoriteStart()) {
			setTabChange(TAB_FAVORITE);
		} else {
			setTabChange(DEFAULT_TAB);
		}

		loadSettingTextSizeSelect();
	}

	private void loadSettingTextSizeSelect() {

		switch (mBusanBusPrefrence.getTextSize()) {
		case 0:
			mTextViewSettingTextSize.setText("보통");
			break;
		case 2:
			mTextViewSettingTextSize.setText("크게");
			break;
		case 4:
			mTextViewSettingTextSize.setText("아주크게");
			break;

		}

	}

	private void searchNosun(String query) {
		Cursor cursor = mBusDb.selectNosunForQuery(query);

		if (mNosunAdapter == null) {
			mNosunAdapter = new SimpleCursorAdapter(SearchMainActivity.this,
					R.layout.item_busnosearchlist, cursor, new String[] {
							"NOSUNNUM", "START", "END" }, new int[] {
							R.id.text1, R.id.text2, R.id.text3 });
			mNosunListView.setAdapter(mNosunAdapter);
		} else {
			mNosunAdapter.changeCursor(cursor);
		}
	}

	private void searchBusStop(String query) {
		Cursor cursor = mBusDb.selectBusStopForQuery(query);

		if (mBusStopAdapter == null) {
			mBusStopAdapter = new SimpleCursorAdapter(SearchMainActivity.this,
					R.layout.item_searchlist, cursor, new String[] {
							"BUSSTOPNAME", "UNIQUEID" }, new int[] {
							R.id.text1, R.id.text2 });
			mBusStopListView.setAdapter(mBusStopAdapter);
		} else {
			mBusStopAdapter.changeCursor(cursor);
		}

	}

	private void favoriteNosun() {
		Cursor cursor = mUserDb.selectFavoriteNosun();
		mFavoriteAdapter = new SimpleCursorAdapter(SearchMainActivity.this,
				R.layout.item_favoritelist, cursor, new String[] { "STOPNAME",
						"STOPID", "NOSUN", "UPDOWN" }, new int[] { R.id.text1,
						R.id.text2, R.id.text3, R.id.text4 });

		mFavoriteListView.setAdapter(mFavoriteAdapter);

		mFavoriteImageNosun.setText("노선(" + cursor.getCount() + ")");

	}

	private void favoriteBusStop() {
		Cursor cursor = mUserDb.selectFavoriteBusStop();
		mFavoriteAdapter = new SimpleCursorAdapter(SearchMainActivity.this,
				R.layout.item_searchlist, cursor, new String[] { "NOSUNNAME",
						"NOSUNNO" }, new int[] { R.id.text1, R.id.text2 });

		mFavoriteListView.setAdapter(mFavoriteAdapter);

		mFavoriteImageBusstop.setText("정류소(" + cursor.getCount() + ")");
	}

	// Back Key 관련
	private Handler mFinishHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				mFlag = false;
			}
		}
	};

	private boolean mFlag = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!mFlag) {
				Toast.makeText(getApplicationContext(),
						"'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
				mFlag = true;
				mFinishHandler.sendEmptyMessageDelayed(0, 2000);
				return false;
			} else {
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}