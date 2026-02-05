package com.kmshack.BusanBus.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kmshack.BusanBus.KakaoLink;
import com.kmshack.BusanBus.R;
import com.kmshack.BusanBus.database.BusDb;
import com.kmshack.BusanBus.database.BusanBusPrefrence;
import com.kmshack.BusanBus.database.UserDb;
import com.kmshack.BusanBus.task.BaseAsyncTask.PostListener;
import com.kmshack.BusanBus.task.HtmlsAsync;
import com.kmshack.BusanBus.utils.ViewHolder;

public class BusstopDetailActivity extends BaseActivity {

	private BusanBusPrefrence mBusanBusPrefrence;

	private GoogleMap mGoogleMap;
	private FrameLayout mGoogleMapView;

	private ArrayList<ArriveItem> Items;
	private ListView mListView;
	private TextView mTxtLocation;
	private TextView mBtnTopMap, mBtnMap, mBtnShortCut, mBtnFavorite, mBtnShareKakao, mBtnOrdering;
	private View mBtnReload;

	private String busstop, busstop_name = "";
	private String info_BUSSTOPNAME = "", info_UNIQUEID = "", info_STOPID = "", info_GUNAME = "", info_DONGNAME = "", info_X = "", info_Y = "";

	private int mCount;

	private ArriveAdapter mArriveAdapter;
	private ArriveItem mArriveItem;

	private BusDb mBusDb;
	private UserDb mUserDb;

	private boolean isFavorite;

	private View mFooterView;
	private View mHeaderView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.busstopdetail);

		Intent intent = getIntent();
		busstop = intent.getStringExtra("BusStop");

		if (busstop == null || busstop.equals("0")) {
			Toast.makeText(getApplicationContext(), "�������� �ʴ� �������Դϴ�.", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		mBusDb = BusDb.getInstance(getApplicationContext());
		mUserDb = UserDb.getInstance(getApplicationContext());
		mBusanBusPrefrence = BusanBusPrefrence.getInstance(getApplicationContext());

		LayoutInflater mInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		tracker.trackPageView("/BusstopDetail");
		mListView = (ListView) findViewById(R.id.busstopdetail_nosunlist);

		mTxtLocation = (TextView) findViewById(R.id.busstopdetail_nosuntext);
		mBtnTopMap = (TextView) findViewById(R.id.busarrive_show_map);

		mBtnReload = findViewById(R.id.busstopdetail_reflash_top);

		// Header View
		mHeaderView = mInflater.inflate(R.layout.view_busstopdetail_header, mListView, false);

		mBtnOrdering = (TextView) mHeaderView.findViewById(R.id.sorting);
		mBtnOrdering.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				mBusanBusPrefrence.setIsArriveSort(!mBusanBusPrefrence.getIsArriveSort());
				loadSort();
				reflash();
			}
		});

		loadSort();

		// Footer View
		mFooterView = mInflater.inflate(R.layout.view_busstopdetail_footer, mListView, false);

		mBtnFavorite = (TextView) mFooterView.findViewById(R.id.busstopdetail_favor);

		mBtnShortCut = (TextView) mFooterView.findViewById(R.id.busstopdetail_shortcut);
		mBtnShareKakao = (TextView) mFooterView.findViewById(R.id.busstopdetail_kakao_share);
		mBtnMap = (TextView) mFooterView.findViewById(R.id.busstopdetail_locationbt);

		mListView.addFooterView(mFooterView);
		mListView.addHeaderView(mHeaderView);

		mGoogleMapView = (FrameLayout) findViewById(R.id.layout_map);

		try {
			initilizeMap();
		} catch (Exception e) {
		}

		Items = new ArrayList<ArriveItem>();

		infoopen();

		reflash();

		mBtnTopMap.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showTopMap(mGoogleMapView.getVisibility() == View.GONE);
			}
		});

		mBtnShortCut.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				shortcut_dlg();
			}
		});

		// ���ã�� �߰�
		mBtnFavorite.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				favor();
			}
		});

		mBtnShareKakao.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				shareKakao();
			}
		});

		// ���ΰ�ħ ��ư Ŭ��
		mBtnReload.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				reflash();
			}
		});

		// ���� ������ ��ġ Ȯ�� ��ư Ŭ��
		mBtnMap.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent mapv = new Intent(BusstopDetailActivity.this, BusMapActivity.class);
				mapv.putExtra("X", info_X.toString());
				mapv.putExtra("Y", info_Y.toString());

				mapv.putExtra("NOSUN", busstop);
				mapv.putExtra("NAME", info_BUSSTOPNAME);
				mapv.putExtra("UNIQUEID", info_UNIQUEID);

				mapv.putExtra("TITLE", busstop + "�� �뼱 - " + info_BUSSTOPNAME + "(" + info_UNIQUEID + ")");
				mapv.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mapv);
			}

		});

		isFavorite = mUserDb.isRegisterFavorite2(busstop);
		if (isFavorite) {
			mBtnFavorite.setText("���ã�� ����");
		} else {
			mBtnFavorite.setText("���ã�� �߰�");
		}

		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				int realPosition = position - mListView.getHeaderViewsCount();

				ArriveItem arriveItem = mArriveAdapter.getArriveItem(realPosition);
				String nosun = arriveItem.nosun;
				int ord = arriveItem.ord;
				String up = null;
				String down = null;
				String realtime = null;

				Cursor cursor = mBusDb.selectBuslineInfo(nosun);
				if (cursor.moveToFirst()) {
					up = cursor.getString(cursor.getColumnIndexOrThrow("START"));
					down = cursor.getString(cursor.getColumnIndexOrThrow("END"));
					realtime = cursor.getString(cursor.getColumnIndexOrThrow("WEBREALTIME"));
				}

				cursor.close();

				if (up == null || down == null)
					return;

				Intent intent = new Intent(getApplicationContext(), NosunDetailActivity.class);
				intent.putExtra("NoSun", nosun);
				intent.putExtra("Up", up);
				intent.putExtra("Down", down);
				intent.putExtra("RealTimeNoSun", realtime);
				intent.putExtra("Id", info_STOPID);
				intent.putExtra("Ord", ord);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		showTopMap(mBusanBusPrefrence.getShowArriveMap());

	}

	private void showTopMap(boolean enable) {
		if (mBtnTopMap == null || mGoogleMapView == null)
			return;

		if (enable) {
			mBtnTopMap.setText("��������");
			mGoogleMapView.setVisibility(View.VISIBLE);
			mBusanBusPrefrence.setShowArriveMap(true);
		} else {
			mBtnTopMap.setText("����ǥ��");
			mGoogleMapView.setVisibility(View.GONE);
			mBusanBusPrefrence.setShowArriveMap(false);
		}
	}

	private void loadSort() {
		String isUse = mBusanBusPrefrence.getIsArriveSort() == true ? "Ȱ��" : "��Ȱ��";

		mBtnOrdering.setText("�������� ���������� ����: " + isUse);
	}

	public void infoopen() {

		Cursor cursor = mBusDb.selectBuslineForUniqueid(busstop);

		if (cursor.getCount() > 0) {

			if (cursor.moveToNext()) {

				String name = cursor.getString(3);
				String id = cursor.getString(9);

				info_GUNAME = cursor.getString(5);
				info_DONGNAME = cursor.getString(6);
				info_BUSSTOPNAME = "�����Ҹ�: " + name;
				busstop_name = name;
				info_UNIQUEID = "�����ҹ�ȣ: " + id;
				info_STOPID = cursor.getString(10);

				double latitude = cursor.getDouble(8);
				double longitude = cursor.getDouble(7);

				info_X = String.valueOf(longitude).replace(" ", "");
				info_Y = String.valueOf(latitude).replace(" ", "");

				setTitle(cursor.getString(3) + "(" + cursor.getString(9) + ")");

				LatLng latlng = new LatLng(latitude, longitude);

				if (mGoogleMap != null) {
					// create marker
					MarkerOptions marker = new MarkerOptions().position(latlng);
					marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
					// adding marker
					mGoogleMap.addMarker(marker);

					CameraPosition INIT = new CameraPosition.Builder().target(latlng).zoom(17F).bearing(0F) // orientation
							.tilt(0F) // viewing angle
							.build();
					mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(INIT));
				}
			}

			mTxtLocation.setText((cursor.getString(4) + " " + info_GUNAME + " " + info_DONGNAME).replace("null", ""));

		}

		cursor.close();
	}

	public void favor() {
		tracker.trackEvent("IconClicks", // Category
				"Favorite", // Action
				"���ã��", // Label
				0); // Value

		if (isFavorite) {
			if (mUserDb.deleteFavorite2(busstop)) {
				Toast.makeText(getApplicationContext(), "���ã�⸦ ���� �Ͽ����ϴ�.", Toast.LENGTH_SHORT).show();
				isFavorite = false;
			}
		} else {
			favoriteDialog();
		}

		if (isFavorite) {
			mBtnFavorite.setText("���ã�� ����");
		} else {
			mBtnFavorite.setText("���ã�� �߰�");
		}
	}

	private void favoriteDialog() {
		final EditText renameText = new EditText(this);
		renameText.setText(busstop_name);
		renameText.setSelection(busstop_name.length());

		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setTitle("���ã�� �̸� �Է�");
		alt_bld.setMessage("������ ���ã�� �̸��� �Է� ���ּ���.").setView(renameText).setCancelable(false).setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				if (mUserDb.insertFavorite2(renameText.getText().toString(), busstop)) {
					Toast.makeText(getApplicationContext(), "���ã�⸦ �߰� �Ͽ����ϴ�.", Toast.LENGTH_SHORT).show();
					isFavorite = true;
				}

				if (isFavorite) {
					mBtnFavorite.setText("���ã�� ����");
				} else {
					mBtnFavorite.setText("���ã�� �߰�");
				}

			}
		}).setNegativeButton("���", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = alt_bld.create();
		alert.show();
	}

	private void shareKakao() {
		String strMessage = busstop_name + "(" + busstop + ")";
		String strURL = "busanbus://stop/detail?" + "busstop=" + busstop;

		String strAppId = getPackageName();
		String strAppVer = "2.0";
		String strAppName = "�λ����";
		String strInstallUrl = "market://details?id=com.kmshack.BusanBus";

		try {
			ArrayList<Map<String, String>> arrMetaInfo = new ArrayList<Map<String, String>>();

			Map<String, String> metaInfoAndroid = new Hashtable<String, String>(1);
			metaInfoAndroid.put("os", "android");
			metaInfoAndroid.put("devicetype", "phone");
			metaInfoAndroid.put("installurl", strInstallUrl);
			metaInfoAndroid.put("executeurl", strURL);
			arrMetaInfo.add(metaInfoAndroid);

			KakaoLink link = new KakaoLink(this, strURL, strAppId, strAppVer, strMessage, strAppName, arrMetaInfo, "UTF-8");

			if (link.isAvailable()) {
				startActivity(link.getIntent());
			} else {
				Toast.makeText(getApplicationContext(), "īī���� ��ġ �� �̿� �����մϴ�.", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		tracker.trackEvent("IconClicks", // Category
				"Kakao", // Action
				"īī���� ��ũ", // Label
				0); // Value

	}

	public void reflash() {

		tracker.trackEvent("IconClicks", // Category
				"ReLoad", // Action
				"���ΰ�ħ", // Label
				0); // Value

		final Cursor cursor = mBusDb.selectReatime(busstop);

		if (cursor.getCount() <= 0)
			return;

		Items.clear();
		mArriveAdapter = null;

		mCount = 0;

		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			final String start = cursor.getString(3);
			final String end = cursor.getString(5);
			final String lineId = cursor.getString(0);
			final String realTime = cursor.getString(1);

			final String nosun = cursor.getString(2);
			final int ord = cursor.getInt(6);

			final String url = "http://121.174.75.12/01/011.html.asp?bstop_id=" + realTime + "&line_id=" + lineId;
			final HtmlsAsync task = new HtmlsAsync();

			if (TextUtils.isEmpty(start) || TextUtils.isEmpty(end)) {
				task.setTitle(nosun + "�� �뼱 ");
			} else {
				task.setTitle(nosun + "�� �뼱 / " + start + " �� " + end);
			}

			task.setNext(mBusDb.selectNextStop(nosun, ord));

			task.setOnTapUpListener(new PostListener() {
				public void onPost(String html) {
					mCount++;

					String bstime;
					if (html != null) {
						if (html.indexOf("������") != -1) {
							String infotmp1 = html.substring(html.indexOf("����") - 2, html.indexOf("����"));
							String infotmp2 = html.substring(html.indexOf("��°") - 2, html.indexOf("��°"));
							bstime = infotmp1.replace(" ", "0").toString() + "����, " + infotmp2.replace(" ", "").toString() + "��° �� ������";

							if (html.indexOf(">2.") != -1) {
								String infotmp3 = html.substring(html.lastIndexOf("����") - 2, html.lastIndexOf("����"));
								String infotmp4 = html.substring(html.lastIndexOf("��°") - 2, html.lastIndexOf("��°"));

								bstime += "\n" + infotmp3.replace(" ", "0").toString() + "����, " + infotmp4.replace(" ", "").toString() + "��° �� ������";
							}
						} else {
							bstime = "���������� �����ϴ�.";
						}
					} else {
						bstime = "���������� �����ϴ�.";
					}

					mArriveItem = new ArriveItem(task.getTitle(), bstime.toString(), task.getNext(), nosun, ord);
					Items.add(mArriveItem);

					if (mCount == cursor.getCount()) {
						mArriveAdapter = new ArriveAdapter(getApplicationContext(), R.layout.item_realtimelist, Items);
						mListView.setAdapter(mArriveAdapter);

						cursor.close();

						dismissDialog();
					}
				}
			});
			showDialog();
			task.execute(url);

		}

	}

	class ArriveItem implements Comparable {
		String item1, item2, item3;
		String nosun;
		int ord;

		ArriveItem(String i1, String i2, String i3, String nosun, int ord) {
			item1 = i1;
			item2 = i2;
			item3 = i3;
			this.nosun = nosun;
			this.ord = ord;
		}

		public int compareTo(Object another) {

			if (mBusanBusPrefrence.getIsArriveSort())
				return item2.compareTo(((ArriveItem) another).item2);
			else {
				return item1.compareTo(((ArriveItem) another).item1);
			}
		}
	}

	class ArriveAdapter extends BaseAdapter {
		private LayoutInflater Inflater;
		private ArrayList<ArriveItem> arSrc;
		private int layout;

		@SuppressWarnings("unchecked")
		public ArriveAdapter(Context context, int alayout, ArrayList<ArriveItem> aarSrc) {
			Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arSrc = aarSrc;

			try {
				Collections.sort(arSrc);
			} catch (Exception e) {
			}

			layout = alayout;
		}

		public int getCount() {
			return arSrc.size();
		}

		public ArriveItem getArriveItem(int position) {
			return arSrc.get(position);
		}

		public String getItem(int position) {
			return arSrc.get(position).item1;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = Inflater.inflate(layout, parent, false);
			}

			TextView txt1 = ViewHolder.get(convertView, R.id.text1);
			txt1.setText(arSrc.get(position).item1);

			TextView txt2 = ViewHolder.get(convertView, R.id.text2);
			txt2.setText(arSrc.get(position).item2);

			TextView txt3 = ViewHolder.get(convertView, R.id.text3);
			txt3.setText(arSrc.get(position).item3);

			return convertView;
		}

	}

	private void shortcut_dlg() {
		final EditText renameText = new EditText(this);
		renameText.setText(busstop_name.toString());
		renameText.setSelection(busstop_name.length());

		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setMessage("����ȭ�鿡 �ٷΰ��� �̸��� �Է� ���ּ���.").setView(renameText).setCancelable(false).setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Intent intent = new Intent();
				intent.setComponent(new ComponentName("com.kmshack.BusanBus", "com.kmshack.BusanBus.activity.BusstopDetailActivity"));

				intent.putExtra("BusStop", busstop);

				Intent result = new Intent();
				result.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

				result.putExtra(Intent.EXTRA_SHORTCUT_NAME, renameText.getText().toString());

				Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.link);
				result.putExtra(Intent.EXTRA_SHORTCUT_ICON, src);

				result.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
				sendBroadcast(result);

				Toast.makeText(BusstopDetailActivity.this, "����ȭ�� �ٷΰ��� ���� �Ϸ�.", Toast.LENGTH_SHORT).show();

			}
		}).setNegativeButton("���", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = alt_bld.create();
		alert.setTitle("�ٷΰ��� �̸� �Է�");
		alert.setIcon(R.drawable.link);
		alert.show();
	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		if (mGoogleMap == null) {
			mGoogleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

			if (mGoogleMap == null) {
			} else {
				// mGoogleMap.setMyLocationEnabled(true);
				mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
				// mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
				// mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);

				CameraPosition INIT = new CameraPosition.Builder().target(new LatLng(35.1796F, 129.076F)).zoom(10F).bearing(0F) // orientation
						.tilt(0F) // viewing angle
						.build();
				mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(INIT));
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

}
