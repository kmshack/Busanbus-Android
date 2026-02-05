package com.kmshack.BusanBus.activity;

import java.io.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kmshack.BusanBus.Config;
import com.kmshack.BusanBus.R;
import com.kmshack.BusanBus.adapter.MainNosunFavoriteAdapter;
import com.kmshack.BusanBus.adapter.MainNosunSearchAdapter;
import com.kmshack.BusanBus.adapter.MainStopFavoriteAdapter;
import com.kmshack.BusanBus.adapter.MainStopSearchAdapter;
import com.kmshack.BusanBus.database.BusDb;
import com.kmshack.BusanBus.database.BusanBusPrefrence;
import com.kmshack.BusanBus.database.Constants.UserData;
import com.kmshack.BusanBus.database.UserDb;
import com.kmshack.BusanBus.utils.Utils;
import com.mobeta.android.dslv.DragSortListView;
import com.sherlock.navigationdrawer.compat.SherlockActionBarDrawerToggle;

public class SearchMainActivity extends BaseActivity implements android.location.LocationListener {

	private static final int TAB_FAVORITE = 1;
	private static final int TAB_NOSUN = 2;
	private static final int TAB_BUSSTOP = 3;
	private static final int TAB_LOCATION = 4;

	public static final int FAVORITE_TAB_NOSUN = 5;
	public static final int FAVORITE_TAB_BUSSTOP = 6;

	private static final int DEFAULT_TAB = TAB_NOSUN;

	private GoogleMap mGoogleMap;

	private BusDb mBusDb;
	private UserDb mUserDb;
	private BusanBusPrefrence mBusanBusPrefrence;
	private InputMethodManager mInputMethodManager;
	private LocationManager mLocationManager;

	private LinearLayout mNosunSearchView;
	private LinearLayout mBusstopSearchView;

	private MainNosunSearchAdapter mMainNosunSearchAdapter;
	private MainStopSearchAdapter mMainStopSearchAdapter;
	private MainNosunFavoriteAdapter mMainNosunFavoriteAdapter;
	private MainStopFavoriteAdapter mMainStopFavoriteAdapter;

	private DrawerLayout mDrawerLayout;
	private SherlockActionBarDrawerToggle mDrawerToggle;

	private ListView mNosunListView;
	private ListView mBusStopListView;
	private DragSortListView mFavoriteListView;

	private EditText mNosunEditText;
	private EditText mBusStopEditText;

	private LinearLayout mTabFavorite;
	private LinearLayout mTabNosun;
	private LinearLayout mTabBusstop;
	private LinearLayout mTabLocation;

	private LinearLayout mLayoutFavorite;
	private LinearLayout mLayoutNosun;
	private LinearLayout mLayoutBusstop;
	private LinearLayout mLayoutLocation;

	private ImageView mImageFavorite;
	private ImageView mImageNosun;
	private ImageView mImageBusstop;
	private ImageView mImageLocation;

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

	private ProgressBar mFavoriteLoading;
	private TextView mFavoriteEmpty;

	private int mFavoriteMode = DEFAULT_TAB;

	private Handler mHandler;
	private int mTabMode;
	
	private Cursor mCursor;

	private DragSortListView.DropListener mOnDrop = new DragSortListView.DropListener() {
		public void drop(int from, int to) {

			if (from != to) {

				if (mFavoriteMode == FAVORITE_TAB_BUSSTOP) {

					if (mMainStopFavoriteAdapter == null)
						return;

					Cursor cursor = mMainStopFavoriteAdapter.getCursor();

					int targetOrderNum;
					int startTo;

					// ū -> ��
					if (from < to) {
						startTo = to + 1;
						cursor.moveToPosition(to);
						targetOrderNum = cursor.getInt(cursor.getColumnIndex(UserData.ORDERING)) + 1;
					}

					// �� -> ū
					else {
						if (to > 0) {
							startTo = to;
							cursor.moveToPosition(to - 1);
							targetOrderNum = cursor.getInt(cursor.getColumnIndex(UserData.ORDERING)) + 1;
						} else {
							startTo = 0;
							cursor.moveToPosition(0);
							targetOrderNum = cursor.getInt(cursor.getColumnIndex(UserData.ORDERING));
						}
					}

					cursor.moveToPosition(from);
					int fromId = cursor.getInt(cursor.getColumnIndex(UserData._ID));
					mUserDb.updateOrderNum(UserData.FAVORITE2, fromId, targetOrderNum);

					if (cursor.getCount() > startTo) {

						String value = UserData.ORDERING + "=" + UserData.ORDERING + "+1";
						StringBuilder sb = new StringBuilder();
						cursor.moveToPosition(startTo);
						do {
							int id = cursor.getInt(cursor.getColumnIndex(UserData._ID));
							if (id != fromId)
								sb.append("," + id);
						} while (cursor.moveToNext());

						if (sb.length() > 0) {
							String selection = UserData._ID + " in (" + sb.substring(1) + ")";
							mUserDb.updateBySql(UserData.FAVORITE2, value, selection);
						}
					}

					favoriteBusStop();

				} else {

					if (mMainNosunFavoriteAdapter == null)
						return;

					Cursor cursor = mMainNosunFavoriteAdapter.getCursor();

					int targetOrderNum;
					int startTo;

					// ū -> ��
					if (from < to) {
						startTo = to + 1;
						cursor.moveToPosition(to);
						targetOrderNum = cursor.getInt(cursor.getColumnIndex(UserData.ORDERING)) + 1;
					}

					// �� -> ū
					else {
						if (to > 0) {
							startTo = to;
							cursor.moveToPosition(to - 1);
							targetOrderNum = cursor.getInt(cursor.getColumnIndex(UserData.ORDERING)) + 1;
						} else {
							startTo = 0;
							cursor.moveToPosition(0);
							targetOrderNum = cursor.getInt(cursor.getColumnIndex(UserData.ORDERING));
						}
					}

					cursor.moveToPosition(from);
					int fromId = cursor.getInt(cursor.getColumnIndex(UserData._ID));
					mUserDb.updateOrderNum(UserData.FAVORITE, fromId, targetOrderNum);

					if (cursor.getCount() > startTo) {

						String value = UserData.ORDERING + "=" + UserData.ORDERING + "+1";
						StringBuilder sb = new StringBuilder();
						cursor.moveToPosition(startTo);
						do {
							int id = cursor.getInt(cursor.getColumnIndex(UserData._ID));
							if (id != fromId)
								sb.append("," + id);
						} while (cursor.moveToNext());

						if (sb.length() > 0) {
							String selection = UserData._ID + " in (" + sb.substring(1) + ")";
							mUserDb.updateBySql(UserData.FAVORITE, value, selection);
						}
					}

					favoriteNosun();

				}

			}

		}
	};

	private DragSortListView.RemoveListener mOnRemove = new DragSortListView.RemoveListener() {
		public void remove(final int position) {

			if (mFavoriteMode == FAVORITE_TAB_BUSSTOP) {
				favoriteBusStop();

			} else {
				favoriteNosun();
			}

			DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			};
			DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {

					if (mFavoriteMode == FAVORITE_TAB_BUSSTOP) {

						if (mMainStopFavoriteAdapter == null)
							return;

						Cursor cursor = mMainStopFavoriteAdapter.getCursor();

						if (cursor.moveToPosition(position)) {
							mUserDb.deleteFavoriteBusStop(cursor.getInt(cursor.getColumnIndexOrThrow(UserData._ID)));
							favoriteBusStop();
						}

					} else {

						if (mMainNosunFavoriteAdapter == null)
							return;

						Cursor cursor = mMainNosunFavoriteAdapter.getCursor();
						if (cursor.moveToPosition(position)) {
							mUserDb.deleteFavoriteNosun(cursor.getInt(cursor.getColumnIndexOrThrow(UserData._ID)));
							favoriteNosun();
						}

					}

				}
			};

			showDeleteDialog(ok, cancel);

		}
	};

	private void showDeleteDialog(DialogInterface.OnClickListener ok, DialogInterface.OnClickListener cancel) {
		new AlertDialog.Builder(this).setTitle("���ã�� ����").setMessage("���ã�⸦ ���� �Ͻðڽ��ϱ�?").setPositiveButton("Ȯ��", ok).setNegativeButton("���", cancel).show();

	}

	private void setFavoriteTabChange(int tab) {

		mFavoriteMode = tab;

		if (mBusanBusPrefrence.getIsFavoriteLocation())
			mBusanBusPrefrence.setFavoriteLocation(tab);

		switch (tab) {
		case FAVORITE_TAB_NOSUN:
			mFavoriteImageNosun.setBackgroundResource(R.drawable.favorite_left_press);
			mFavoriteImageNosun.setTextColor(Color.parseColor("#F7F7F0"));
			mFavoriteImageBusstop.setBackgroundResource(R.drawable.btn_favorite_right);
			mFavoriteImageBusstop.setTextColor(Color.parseColor("#7F7F72"));

			break;

		case FAVORITE_TAB_BUSSTOP:
			mFavoriteImageNosun.setBackgroundResource(R.drawable.btn_favorite_left);
			mFavoriteImageNosun.setTextColor(Color.parseColor("#7F7F72"));
			mFavoriteImageBusstop.setBackgroundResource(R.drawable.favorite_right_press);
			mFavoriteImageBusstop.setTextColor(Color.parseColor("#F7F7F0"));
			break;
		}
	}

	private void setTabChange(int tab) {

		mInputMethodManager.hideSoftInputFromWindow(mNosunEditText.getWindowToken(), 0);
		mTabMode = tab;

		switch (tab) {

		case TAB_FAVORITE:
			mLocationManager.removeUpdates(this);
			mLayoutFavorite.setVisibility(View.VISIBLE);
			mLayoutNosun.setVisibility(View.GONE);
			mLayoutBusstop.setVisibility(View.GONE);
			mLayoutLocation.setVisibility(View.GONE);

			mTabFavorite.setBackgroundColor(Color.parseColor("#7F7F72"));
			mTabNosun.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabBusstop.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabLocation.setBackgroundColor(Color.parseColor("#F7F7F0"));

			mTextFavorite.setTextColor(Color.parseColor("#FFFFFF"));
			mTextNosun.setTextColor(Color.parseColor("#7F7F72"));
			mTextBusstop.setTextColor(Color.parseColor("#7F7F72"));
			mTextSetting.setTextColor(Color.parseColor("#7F7F72"));

			mImageFavorite.setImageResource(R.drawable.ic_main_tab_favorite_pressed);
			mImageNosun.setImageResource(R.drawable.ic_main_tab_search_normal);
			mImageBusstop.setImageResource(R.drawable.ic_main_tab_stop_normal);
			mImageLocation.setImageResource(R.drawable.ic_main_tab_compass_normal);

			if (mBusanBusPrefrence.getIsFavoriteLocation())
				setFavoriteTabChange(mBusanBusPrefrence.getFavoriteLocation());
			else
				setFavoriteTabChange(FAVORITE_TAB_NOSUN);

			showFavoriteLoading();

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

			tracker.trackEvent("TabClicks", // Category
					"Favorite", // Action
					"���ã��", // Label
					0); // Value
			break;

		case TAB_NOSUN:
			mLocationManager.removeUpdates(this);
			mLayoutFavorite.setVisibility(View.GONE);
			mLayoutNosun.setVisibility(View.VISIBLE);
			mLayoutBusstop.setVisibility(View.GONE);
			mLayoutLocation.setVisibility(View.GONE);
			mNosunEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

			mTabFavorite.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabNosun.setBackgroundColor(Color.parseColor("#7F7F72"));
			mTabBusstop.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabLocation.setBackgroundColor(Color.parseColor("#F7F7F0"));

			mTextFavorite.setTextColor(Color.parseColor("#7F7F72"));
			mTextNosun.setTextColor(Color.parseColor("#FFFFFF"));
			mTextBusstop.setTextColor(Color.parseColor("#7F7F72"));
			mTextSetting.setTextColor(Color.parseColor("#7F7F72"));

			mImageFavorite.setImageResource(R.drawable.ic_main_tab_favorite_normal);
			mImageNosun.setImageResource(R.drawable.ic_main_tab_search_pressed);
			mImageBusstop.setImageResource(R.drawable.ic_main_tab_stop_normal);
			mImageLocation.setImageResource(R.drawable.ic_main_tab_compass_normal);

			tracker.trackEvent("TabClicks", // Category
					"LineSearch", // Action
					"�뼱��ȣ", // Label
					0); // Value

			break;

		case TAB_BUSSTOP:
			mLocationManager.removeUpdates(this);
			mLayoutFavorite.setVisibility(View.GONE);
			mLayoutNosun.setVisibility(View.GONE);
			mLayoutBusstop.setVisibility(View.VISIBLE);
			mLayoutLocation.setVisibility(View.GONE);
			mBusStopEditText.setInputType(InputType.TYPE_CLASS_TEXT);

			mTabFavorite.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabNosun.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabBusstop.setBackgroundColor(Color.parseColor("#7F7F72"));
			mTabLocation.setBackgroundColor(Color.parseColor("#F7F7F0"));

			mTextFavorite.setTextColor(Color.parseColor("#7F7F72"));
			mTextNosun.setTextColor(Color.parseColor("#7F7F72"));
			mTextBusstop.setTextColor(Color.parseColor("#FFFFFF"));
			mTextSetting.setTextColor(Color.parseColor("#7F7F72"));

			mImageFavorite.setImageResource(R.drawable.ic_main_tab_favorite_normal);
			mImageNosun.setImageResource(R.drawable.ic_main_tab_search_normal);
			mImageBusstop.setImageResource(R.drawable.ic_main_tab_stop_pressed);
			mImageLocation.setImageResource(R.drawable.ic_main_tab_compass_normal);

			tracker.trackEvent("TabClicks", // Category
					"BusStopSearch", // Action
					"������", // Label
					0); // Value

			break;

		case TAB_LOCATION:
			mLayoutFavorite.setVisibility(View.GONE);
			mLayoutNosun.setVisibility(View.GONE);
			mLayoutBusstop.setVisibility(View.GONE);
			mLayoutLocation.setVisibility(View.VISIBLE);

			mTabFavorite.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabNosun.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabBusstop.setBackgroundColor(Color.parseColor("#F7F7F0"));
			mTabLocation.setBackgroundColor(Color.parseColor("#7F7F72"));

			mTextFavorite.setTextColor(Color.parseColor("#7F7F72"));
			mTextNosun.setTextColor(Color.parseColor("#7F7F72"));
			mTextBusstop.setTextColor(Color.parseColor("#7F7F72"));
			mTextSetting.setTextColor(Color.parseColor("#FFFFFF"));

			mImageFavorite.setImageResource(R.drawable.ic_main_tab_favorite_normal);
			mImageNosun.setImageResource(R.drawable.ic_main_tab_search_normal);
			mImageBusstop.setImageResource(R.drawable.ic_main_tab_stop_normal);
			mImageLocation.setImageResource(R.drawable.ic_main_tab_compass_pressed);

			tracker.trackEvent("TabClicks", // Category
					"Information", // Action
					"����", // Label
					0); // Value

			
			if(!mBusanBusPrefrence.getIsLocationAgree()){
				
				DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						
						setTabChange(TAB_NOSUN);
						
					}
				};
				DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						mBusanBusPrefrence.setLocationAgree();
						
						initilizeMap();
						loadLocation();
					}
				};

				new AlertDialog.Builder(this).setTitle("��ġ ���� Ȱ��").setMessage("Google�� ��ġ ���񽺿��� �͸��� ��ġ ������ ������ �� �ֵ��� �մϴ�. ��ġ ���������ʹ� �����Ǵ� ���α�⿡ �������� ������ �ܼ��� Ȱ���Ͽ� ���� ��ġ���� ����� �����Ҹ� �˻��ϱ����� ���˴ϴ�.").setPositiveButton("����", ok)
						.setNegativeButton("���Ǿ���", cancel).show();
				
			}else{
				initilizeMap();
				loadLocation();
			}
			
			

			break;
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

	private void loadLocation() {

		if (mGoogleMap == null)
			return;

		GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		Criteria criteria = new Criteria();
		String provider = mLocationManager.getBestProvider(criteria, true);

		if (provider == null) { // ��ġ���� ������ �ȵǾ� ������ �����ϴ� ��Ƽ��Ƽ�� �̵��մϴ�
			DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					Location gpsLocation = mLocationManager.getLastKnownLocation("gps");

					if (gpsLocation != null) {
						onLocationChanged(gpsLocation);
					}
				}
			};
			DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
				}
			};

			new AlertDialog.Builder(this).setTitle("��ġ���� ����").setMessage("��ġ���񽺰� �������� �ʾ� ���� ���񽺸� ����Ͻ� �� �����ϴ�. ��ġ���� ������ �Ͻðڽ��ϱ�?").setPositiveButton("����", ok)
					.setNegativeButton("�ݱ�", cancel).show();

		} else { // ��ġ ���� ������ �Ǿ� ������ ������ġ�� �޾ƿɴϴ�.
			mLocationManager.requestLocationUpdates(provider, 1, 1, this);
			mGoogleMap.setMyLocationEnabled(true);
			mGoogleMap.getMyLocation();
		}
	}

	View.OnClickListener mSettingClick = new OnClickListener() {

		public void onClick(View v) {

			switch (v.getId()) {

			case R.id.setting_notice:
				Intent webview = new Intent(getApplicationContext(), WebActivity.class);
				webview.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(webview);
				break;
			case R.id.setting_update:
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Config.UPDATE_URL));
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			case R.id.setting_textsize_sun:
				Intent fontSizeIntent = new Intent(getApplicationContext(), TextSizeSettingActivity.class);
				fontSizeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(fontSizeIntent);
				break;
			case R.id.setting_arrive_sun:

				mBusanBusPrefrence.setIsArriveSort(!mBusanBusPrefrence.getIsArriveSort());

				if (mBusanBusPrefrence.getIsArriveSort())
					mCheckSettingArrive.setImageResource(R.drawable.btn_setting_checkbox_check);
				else
					mCheckSettingArrive.setImageResource(R.drawable.btn_setting_checkbox_uncheck);

				break;

			case R.id.setting_favorite_start:

				mBusanBusPrefrence.setIsFavoriteStart(!mBusanBusPrefrence.getIsFavoriteStart());

				if (mBusanBusPrefrence.getIsFavoriteStart())
					mCheckSettingFavorite.setImageResource(R.drawable.btn_setting_checkbox_check);
				else
					mCheckSettingFavorite.setImageResource(R.drawable.btn_setting_checkbox_uncheck);

				break;

			case R.id.setting_favorite_location:

				mBusanBusPrefrence.setIsFavoriteLocation(!mBusanBusPrefrence.getIsFavoriteLocation());

				if (mBusanBusPrefrence.getIsFavoriteLocation())
					mCheckSettingFavoriteLocation.setImageResource(R.drawable.btn_setting_checkbox_check);
				else
					mCheckSettingFavoriteLocation.setImageResource(R.drawable.btn_setting_checkbox_uncheck);

				break;
			case R.id.setting_bin:
				showInit();
				break;
			case R.id.setting_opensource:
				showLicense();
				break;

			}

		}
	};

	View.OnClickListener mTabClickListener = new OnClickListener() {

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
			case R.id.main_tap_location:
				setTabChange(TAB_LOCATION);
				break;

			case R.id.favorite_tab_nosun:
				setFavoriteTabChange(FAVORITE_TAB_NOSUN);
				showFavoriteLoading();
				favoriteNosun();
				break;

			case R.id.favorite_tab_busstop:
				setFavoriteTabChange(FAVORITE_TAB_BUSSTOP);
				showFavoriteLoading();
				favoriteBusStop();
				break;

			}

		}
	};

	@Override
	protected void onResume() {
		super.onResume();

		if (mTabMode == TAB_FAVORITE) {

			showFavoriteLoading();

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
		}

		loadSettingTextSizeSelect();

		if (mGoogleMap != null)
			initilizeMap();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_common);
		setTitle("�λ����");
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerListener(new DrawerListener() {
			public void onDrawerOpened(View drawerView) {
				mDrawerToggle.onDrawerOpened(drawerView);
				getActionBarHelper().onDrawerOpened();
			}

			public void onDrawerClosed(View drawerView) {
				mDrawerToggle.onDrawerClosed(drawerView);
				getActionBarHelper().onDrawerClosed();
			}

			public void onDrawerSlide(View drawerView, float slideOffset) {
				mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
			}

			public void onDrawerStateChanged(int newState) {
				mDrawerToggle.onDrawerStateChanged(newState);
			}
		});
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		mDrawerToggle = new SherlockActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer_dark, R.string.drawer_open, R.string.drawer_close);
		mDrawerToggle.syncState();

		mBusDb = BusDb.getInstance(getApplicationContext());
		mUserDb = UserDb.getInstance(getApplicationContext());
		mBusanBusPrefrence = BusanBusPrefrence.getInstance(getApplicationContext());
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

		mImageFavorite = (ImageView) findViewById(R.id.main_tap_favorite_image);
		mImageNosun = (ImageView) findViewById(R.id.main_tap_nosun_image);
		mImageBusstop = (ImageView) findViewById(R.id.main_tap_busstop_image);
		mImageLocation = (ImageView) findViewById(R.id.main_tap_setting_image);

		mFavoriteImageNosun = (TextView) findViewById(R.id.favorite_tab_nosun);
		mFavoriteImageBusstop = (TextView) findViewById(R.id.favorite_tab_busstop);

		mTextFavorite = (TextView) findViewById(R.id.main_tap_favorite_text);
		mTextNosun = (TextView) findViewById(R.id.main_tap_nosun_text);
		mTextBusstop = (TextView) findViewById(R.id.main_tap_busstop_text);
		mTextSetting = (TextView) findViewById(R.id.main_tap_setting_text);

		mNosunListView = (ListView) findViewById(R.id.lv_nosun);
		mBusStopListView = (ListView) findViewById(R.id.lv_busstop);
		mFavoriteListView = (DragSortListView) findViewById(R.id.lv_favorite);

		mNosunListView.setEmptyView((TextView) findViewById(R.id.nosun_empty));
		mBusStopListView.setEmptyView((TextView) findViewById(R.id.busstop_empty));

		mFavoriteEmpty = (TextView) findViewById(R.id.favorite_empty);
		mFavoriteLoading = (ProgressBar) findViewById(R.id.favorite_loading);

		mFavoriteListView.setDropListener(mOnDrop);
		mFavoriteListView.setRemoveListener(mOnRemove);

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

		findViewById(R.id.setting_opensource).setOnClickListener(mSettingClick);
		findViewById(R.id.setting_bin).setOnClickListener(mSettingClick);

		if (mBusanBusPrefrence.getIsArriveSort())
			mCheckSettingArrive.setImageResource(R.drawable.btn_setting_checkbox_check);
		else
			mCheckSettingArrive.setImageResource(R.drawable.btn_setting_checkbox_uncheck);

		if (mBusanBusPrefrence.getIsFavoriteStart())
			mCheckSettingFavorite.setImageResource(R.drawable.btn_setting_checkbox_check);
		else
			mCheckSettingFavorite.setImageResource(R.drawable.btn_setting_checkbox_uncheck);

		if (mBusanBusPrefrence.getIsFavoriteLocation())
			mCheckSettingFavoriteLocation.setImageResource(R.drawable.btn_setting_checkbox_check);
		else
			mCheckSettingFavoriteLocation.setImageResource(R.drawable.btn_setting_checkbox_uncheck);

		mNosunSearchView = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.search_bar_nosun, null);
		mNosunSearchView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		mNosunEditText = (EditText) mNosunSearchView.findViewById(R.id.et_search_nosun);
		mNosunEditText.setHint("�뼱��ȣ �˻�");
		mNosunEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		mNosunEditText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mHandler.removeMessages(TAB_NOSUN);
				Message msg = Message.obtain(mHandler, TAB_NOSUN, s.toString());
				mHandler.sendMessageDelayed(msg, 200);
			}

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

		});

		((LinearLayout) findViewById(R.id.search_nosun)).addView(mNosunSearchView);

		mNosunListView.setOnScrollListener(new OnScrollListener() {
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
			}

			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				mInputMethodManager.hideSoftInputFromWindow(mNosunEditText.getWindowToken(), 0);
			}
		});

		mNosunListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				Cursor cursor = mMainNosunSearchAdapter.getCursor();
				cursor.moveToPosition(position);

				String nosun = cursor.getString(1);
				String realtime = cursor.getString(cursor.getColumnIndexOrThrow("WEBREALTIME"));
				String up = cursor.getString(2);
				String down = cursor.getString(3);

				Intent intent = new Intent(getApplicationContext(), NosunDetailActivity.class);
				intent.putExtra("NoSun", nosun);
				intent.putExtra("RealTimeNoSun", realtime);
				intent.putExtra("Up", up);
				intent.putExtra("Down", down);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

			}
		});

		mBusstopSearchView = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.search_bar_busstop, null);
		mBusStopEditText = (EditText) mBusstopSearchView.findViewById(R.id.et_search_busstop);
		mBusStopEditText.setHint("�����Ҹ�/��ȣ �˻�");
		mBusStopEditText.setInputType(InputType.TYPE_CLASS_TEXT);
		mBusStopEditText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mHandler.removeMessages(TAB_BUSSTOP);
				Message msg = Message.obtain(mHandler, TAB_BUSSTOP, s.toString());
				mHandler.sendMessageDelayed(msg, 200);
			}

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

		});

		((LinearLayout) findViewById(R.id.search_busstop)).addView(mBusstopSearchView);

		mBusStopListView.setOnScrollListener(new OnScrollListener() {
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
			}

			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				mInputMethodManager.hideSoftInputFromWindow(mBusStopEditText.getWindowToken(), 0);
			}
		});

		mBusStopListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				Cursor cursor = mMainStopSearchAdapter.getCursor();
				cursor.moveToPosition(position);

				String busStop = cursor.getString(2).toString();

				Intent intent = new Intent(getApplicationContext(), BusstopDetailActivity.class);
				intent.putExtra("BusStop", busStop);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

			}
		});

		mFavoriteListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				Cursor cursor = null;
				switch (mFavoriteMode) {
				case FAVORITE_TAB_NOSUN:
					cursor = mMainNosunFavoriteAdapter.getCursor();
					break;

				default:
					cursor = mMainStopFavoriteAdapter.getCursor();
					break;
				}

				if (cursor.getCount() > 0) {
					cursor.moveToPosition(position);
					Intent intent = null;

					switch (mFavoriteMode) {
					case FAVORITE_TAB_NOSUN:
						intent = new Intent(getApplicationContext(), BusArriveActivity.class);
						intent.putExtra("NOSUN", cursor.getString(1));
						intent.putExtra("UNIQUEID", cursor.getString(2));
						intent.putExtra("BUSSTOPNAME", cursor.getString(3));
						intent.putExtra("UPDOWN", cursor.getString(4));
						intent.putExtra("ORD", cursor.getString(6));

						break;

					case FAVORITE_TAB_BUSSTOP:
						intent = new Intent(getApplicationContext(), BusstopDetailActivity.class);
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
		mTabLocation = (LinearLayout) findViewById(R.id.main_tap_location);

		mLayoutFavorite = (LinearLayout) findViewById(R.id.layout_main_favorite);
		mLayoutNosun = (LinearLayout) findViewById(R.id.layout_main_nosun_search);
		mLayoutBusstop = (LinearLayout) findViewById(R.id.layout_main_busstop_search);
		mLayoutLocation = (LinearLayout) findViewById(R.id.layout_main_location);

		mTabFavorite.setOnClickListener(mTabClickListener);
		mTabNosun.setOnClickListener(mTabClickListener);
		mTabBusstop.setOnClickListener(mTabClickListener);
		mTabLocation.setOnClickListener(mTabClickListener);
		mFavoriteImageNosun.setOnClickListener(mTabClickListener);
		mFavoriteImageBusstop.setOnClickListener(mTabClickListener);

		searchNosun("");
		searchBusStop("�λ�");

		if (mBusanBusPrefrence.getIsFavoriteStart()) {
			setTabChange(TAB_FAVORITE);
		} else {
			setTabChange(DEFAULT_TAB);
		}

		tracker.trackPageView("/SearchMain");

		loadSettingTextSizeSelect();
	}

	private void loadSettingTextSizeSelect() {

		switch (mBusanBusPrefrence.getTextSize()) {
		case 0:
			mTextViewSettingTextSize.setText("����");
			break;
		case 2:
			mTextViewSettingTextSize.setText("ũ��");
			break;
		case 4:
			mTextViewSettingTextSize.setText("����ũ��");
			break;

		}

	}

	private void searchNosun(String query) {
		Cursor cursor = mBusDb.selectNosunForQuery(query);

		if (mMainNosunSearchAdapter == null) {
			mMainNosunSearchAdapter = new MainNosunSearchAdapter(getApplicationContext(), cursor);
			mNosunListView.setAdapter(mMainNosunSearchAdapter);
		} else {
			mMainNosunSearchAdapter.changeCursor(cursor);
		}
	}

	private void searchBusStop(String query) {
		Cursor cursor = mBusDb.selectBusStopForQuery(query);

		if (mMainStopSearchAdapter == null) {
			mMainStopSearchAdapter = new MainStopSearchAdapter(getApplicationContext(), cursor);
			mBusStopListView.setAdapter(mMainStopSearchAdapter);
		} else {
			mMainStopSearchAdapter.changeCursor(cursor);
		}

	}

	private void showFavoriteLoading() {
		mFavoriteListView.setEmptyView(null);
		mFavoriteEmpty.setVisibility(View.GONE);
		mFavoriteLoading.setVisibility(View.VISIBLE);
	}

	private void hideFavoriteLoading() {
		mFavoriteLoading.setVisibility(View.GONE);
		mFavoriteListView.setEmptyView(mFavoriteEmpty);
	}

	private void favoriteNosun() {
		Cursor cursor = mUserDb.selectFavoriteNosun();
		if (mMainNosunFavoriteAdapter == null) {
			mMainNosunFavoriteAdapter = new MainNosunFavoriteAdapter(getApplicationContext(), cursor);
		} else {
			mMainNosunFavoriteAdapter.changeCursor(cursor);
		}

		mFavoriteListView.setAdapter(mMainNosunFavoriteAdapter);

		mFavoriteImageNosun.setText("�뼱(" + cursor.getCount() + ")");

		if (mFavoriteMode == FAVORITE_TAB_NOSUN) {
			hideFavoriteLoading();
		}
	}

	private void favoriteBusStop() {
		Cursor cursor = mUserDb.selectFavoriteBusStop();

		if (mMainStopFavoriteAdapter == null) {
			mMainStopFavoriteAdapter = new MainStopFavoriteAdapter(getApplicationContext(), cursor);
		} else {
			mMainStopFavoriteAdapter.changeCursor(cursor);
		}

		mFavoriteListView.setAdapter(mMainStopFavoriteAdapter);

		mFavoriteImageBusstop.setText("������(" + cursor.getCount() + ")");

		if (mFavoriteMode == FAVORITE_TAB_BUSSTOP) {
			hideFavoriteLoading();
		}
	}

	// Back Key ����
	private Handler mFinishHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				mFlag = false;
			}
		}
	};

	private boolean mFlag = false;

	private boolean mIsInitOptionsMenu = false;

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (mIsInitOptionsMenu == false) {
			mIsInitOptionsMenu = true;
		} else {
			if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
				mDrawerLayout.closeDrawer(GravityCompat.START);
			} else {
				mDrawerLayout.openDrawer(GravityCompat.START);
			}
		}

		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return false;

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!mFlag) {
				Toast.makeText(getApplicationContext(), "'�ڷ�' ��ư�� �ѹ� �� �����ø� ����˴ϴ�.", Toast.LENGTH_SHORT).show();
				mFlag = true;
				mFinishHandler.sendEmptyMessageDelayed(0, 2000);
				return false;
			} else {
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
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
				
				cursorClose();
				mGoogleMap.clear();

				mCursor = mBusDb.selectLocation(35.1796F, 129.076F);

				if (mCursor.moveToFirst()) {

					while (mCursor.moveToNext()) {

						double latitude = mCursor.getDouble(8);
						double longitude = mCursor.getDouble(7);

						String id = mCursor.getString(9);
						String name = mCursor.getString(3);

						LatLng latlng = new LatLng(latitude, longitude);

						MarkerOptions marker = new MarkerOptions().position(latlng).title(name).snippet(id);
						marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

						mGoogleMap.addMarker(marker);
					}

					LatLng currentLatlng = new LatLng(35.1796F, 129.076F);

					CameraPosition INIT = new CameraPosition.Builder().target(currentLatlng).zoom(14F).bearing(0F) // orientation
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
		}

		if (mTabMode == TAB_LOCATION && mGoogleMap != null) {
			mGoogleMap.setMyLocationEnabled(true);
			mGoogleMap.getMyLocation();
		}
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public void onLocationChanged(Location location) {
		if (mTabMode == TAB_LOCATION) {

			cursorClose();
			mGoogleMap.clear();

			mCursor = mBusDb.selectLocation(location.getLatitude(), location.getLongitude());

			if (mCursor.moveToFirst()) {

				while (mCursor.moveToNext()) {

					double latitude = mCursor.getDouble(8);
					double longitude = mCursor.getDouble(7);

					String id = mCursor.getString(9);
					String name = mCursor.getString(3);

					LatLng latlng = new LatLng(latitude, longitude);

					MarkerOptions marker = new MarkerOptions().position(latlng).title(name).snippet(id);
					marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

					mGoogleMap.addMarker(marker);
				}

				LatLng currentLatlng = new LatLng(location.getLatitude(), location.getLongitude());

				CameraPosition INIT = new CameraPosition.Builder().target(currentLatlng).zoom(16F).bearing(0F) // orientation
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

	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocationManager.removeUpdates(this);
	}

	private void showLicense() {

		DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}

		};

		new AlertDialog.Builder(this).setTitle("���¼ҽ� ���̼���").setMessage(getString(R.string.license)).setNegativeButton("Ȯ��", ok).show();
	}

	private void showInit() {
		DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

			}
		};
		DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				showDialog();
				DataDeleteTask task = new DataDeleteTask();
				task.execute();

			}

		};

		new AlertDialog.Builder(this).setTitle("������ �ʱ�ȭ").setMessage("�뼱�����͸� �ʱ�ȭ �մϴ�. ������Ʈ�� �ȵǴ� ��쿡�� ����ϼ���. ������� �Ͻðڽ��ϱ�?").setPositiveButton("Ȯ��", ok)
				.setNegativeButton("���", cancel).show();

	}

	class DataDeleteTask extends AsyncTask<Boolean, Boolean, Boolean> {

		@Override
		protected Boolean doInBackground(Boolean... arg0) {
			File file = Utils.getDbFile(getApplicationContext());
			file.delete();
			File dir = Utils.getDbDirectory(getApplicationContext());
			dir.delete();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			Log.i("BusanBus", "DB�� �����մϴ�.");
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			dismissDialog();

			if (result) {
				finish();

				Intent intent = new Intent(getApplicationContext(), BusanBusActivity.class);
				intent.putExtra("reload", true);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

			} else {
				Toast.makeText(getApplicationContext(), "�� �� ���� ������ �ʱ�ȭ�� �Ұ��� �մϴ�.", Toast.LENGTH_SHORT).show();
			}
		}

	}

}