package com.kmshack.BusanBus.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

import kr.hyosang.coordinate.CoordPoint;
import kr.hyosang.coordinate.TransCoord;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.kmshack.BusanBus.KakaoLink;
import com.kmshack.BusanBus.MapOverlay;
import com.kmshack.BusanBus.R;
import com.kmshack.BusanBus.database.BusDb;
import com.kmshack.BusanBus.database.BusanBusPrefrence;
import com.kmshack.BusanBus.database.UserDb;
import com.kmshack.BusanBus.task.BaseAsyncTask.PostListener;
import com.kmshack.BusanBus.task.HtmlsAsync;

/**
 * 특정 정류소에 대한 모든 버스의 도착정보
 * 
 * @author kmshack
 * 
 */
public class BusstopDetailActivity extends BaseMapActivity {

	private BusanBusPrefrence mBusanBusPrefrence;

	private ArrayList<MyItem> mItems;
	private ListView mListView;
	private TextView mTxtNosun;
	private TextView mBtnMap, mBtnLocation, mBtnShortCut, mBtnFavorite,
			mShareKakao, mSorting;
	private ImageView mBtnRefresh;

	private String mStopId, mStopName = "";
	private String mInfoStopName = "", mInfoUniquId = "", mInfoGuName = "",
			mInfoDongName = "", mInfoX = "", mInfoY = "";

	private int mCount;

	public SimpleCursorAdapter mAdapter;
	private MyListAdapter mMyListAdapter;
	private MyItem mMyItem;

	private MapView mMapView;

	private BusDb mBusDb;
	private UserDb mUserDb;

	private boolean mIsFavorite;

	private View mFooterView;
	private View mHeaderView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.busstopdetail);

		Intent intent = getIntent();
		mStopId = intent.getStringExtra("BusStop");

		if (mStopId == null || mStopId.equals("0")) {
			Toast.makeText(getApplicationContext(), "지원하지 않는 정류소입니다.",
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		mBusDb = BusDb.getInstance(getApplicationContext());
		mUserDb = UserDb.getInstance(getApplicationContext());
		mBusanBusPrefrence = BusanBusPrefrence
				.getInstance(getApplicationContext());

		LayoutInflater mInflater = (LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mListView = (ListView) findViewById(R.id.busstopdetail_nosunlist);

		mMapView = (MapView) findViewById(R.id.mapview);
		mTxtNosun = (TextView) findViewById(R.id.busstopdetail_nosuntext);
		mBtnMap = (TextView) findViewById(R.id.busarrive_show_map);

		mBtnRefresh = (ImageView) findViewById(R.id.busstopdetail_reflash_top);

		mHeaderView = mInflater.inflate(R.layout.view_busstopdetail_header,
				mListView, false);

		mSorting = (TextView) mHeaderView.findViewById(R.id.sorting);
		mSorting.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				mBusanBusPrefrence.setIsArriveSort(!mBusanBusPrefrence
						.getIsArriveSort());
				loadArriveSorting();
				loadArrive();
			}
		});

		loadArriveSorting();

		// Footer View
		mFooterView = mInflater.inflate(R.layout.view_busstopdetail_footer,
				mListView, false);

		mBtnFavorite = (TextView) mFooterView
				.findViewById(R.id.busstopdetail_favor);

		mBtnShortCut = (TextView) mFooterView
				.findViewById(R.id.busstopdetail_shortcut);
		mShareKakao = (TextView) mFooterView
				.findViewById(R.id.busstopdetail_kakao_share);
		mBtnLocation = (TextView) mFooterView
				.findViewById(R.id.busstopdetail_locationbt);

		mListView.addFooterView(mFooterView);
		mListView.addHeaderView(mHeaderView);

		mItems = new ArrayList<MyItem>();

		loadInformationForStopId();
		loadArrive();

		mBtnMap.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mMapView.getVisibility() == View.GONE) {
					mBtnMap.setText("▲ 지도 감추기");
					mMapView.setVisibility(View.VISIBLE);
				} else if (mMapView.getVisibility() == View.VISIBLE) {
					mBtnMap.setText("▼ 지도 표시");
					mMapView.setVisibility(View.GONE);
				}
			}
		});

		mBtnShortCut.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				shortcutDialog();
			}
		});

		// 즐겨찾기 추가
		mBtnFavorite.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				toggleFavorite();
			}
		});

		mShareKakao.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				shareKakao();
			}
		});

		// 새로고침 버튼 클릭
		mBtnRefresh.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				loadArrive();
			}
		});

		// 현재 정류소 위치 확인 버튼 클릭
		mBtnLocation.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent mapv = new Intent(BusstopDetailActivity.this,
						BusMapActivity.class);
				mapv.putExtra("X", mInfoX.toString());
				mapv.putExtra("Y", mInfoY.toString());
				mapv.putExtra("TITLE", mStopId + "번 노선 - " + mInfoStopName
						+ "(" + mInfoUniquId + ")");
				mapv.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(mapv);
			}

		});

		mIsFavorite = mUserDb.isRegisterFavorite2(mStopId);
		if (mIsFavorite) {
			mBtnFavorite.setText("즐겨찾기 삭제");
		} else {
			mBtnFavorite.setText("즐겨찾기 추가");
		}

		mListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				int realPosition = position - mListView.getHeaderViewsCount();

				String tmp = mMyListAdapter.getItem(realPosition);
				String nosun = tmp.substring(0, tmp.indexOf("번 노선 ")).replace(
						" ", "");
				String up = null;
				String down = null;

				Cursor cursor = mBusDb.selectBuslineInfo(nosun);
				if (cursor.moveToFirst()) {
					up = cursor.getString(cursor.getColumnIndexOrThrow("START"));
					down = cursor.getString(cursor.getColumnIndexOrThrow("END"));
				}

				cursor.close();

				if (up == null || down == null)
					return;

				Intent intent = new Intent(getApplicationContext(),
						NosunDetailActivity.class);
				intent.putExtra("NoSun", nosun);
				intent.putExtra("Up", up);
				intent.putExtra("Down", down);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
		});

	}

	private void loadArriveSorting() {
		String isUse = mBusanBusPrefrence.getIsArriveSort() == true ? "활성"
				: "비활성";

		mSorting.setText("도착정보 빠른순으로 정렬: " + isUse);
	}

	public void loadInformationForStopId() {

		Cursor cursor = mBusDb.selectBuslineForUniqueid(mStopId);

		if (cursor.getCount() > 0) {

			if (cursor.moveToNext()) {
				mInfoStopName = "정류소명: " + cursor.getString(3);
				mStopName = cursor.getString(3);
				mInfoUniquId = "정류소번호: " + cursor.getString(9);
				mInfoGuName = cursor.getString(5);
				mInfoDongName = cursor.getString(6);
				mInfoX = cursor.getString(7).replace(" ", "");
				mInfoY = cursor.getString(8).replace(" ", "");

				setTitleLeft(cursor.getString(3) + "(" + cursor.getString(9)
						+ ")");

				CoordPoint tm = new CoordPoint(Double.parseDouble(mInfoX),
						Double.parseDouble(mInfoY));
				CoordPoint wgs = TransCoord.getTransCoord(tm,
						TransCoord.COORD_TYPE_WGS84,
						TransCoord.COORD_TYPE_WGS84);

				GeoPoint mapPoint = new GeoPoint((int) ((wgs.y) * 1E6),
						(int) ((wgs.x) * 1E6));

				mMapView.setBuiltInZoomControls(false);
				mMapView.getController().setCenter(mapPoint);
				mMapView.getController().setZoom(19);

				mMapView.getOverlays().add(new MapOverlay(mapPoint, this));

			}

			mTxtNosun
					.setText((cursor.getString(4) + " " + mInfoGuName + " " + mInfoDongName)
							.replace("null", ""));

		}

		cursor.close();
	}

	public void toggleFavorite() {

		if (mIsFavorite) {
			if (mUserDb.deleteFavorite2(mStopId)) {
				Toast.makeText(getApplicationContext(), "즐겨찾기를 삭제 하였습니다.",
						Toast.LENGTH_SHORT).show();
				mIsFavorite = false;
			}
		} else {
			favoriteDialog();
		}

		if (mIsFavorite) {
			mBtnFavorite.setText("즐겨찾기 삭제");
		} else {
			mBtnFavorite.setText("즐겨찾기 추가");
		}
	}

	private void favoriteDialog() {
		final EditText renameText = new EditText(this);
		renameText.setText(mStopName);
		renameText.setSelection(mStopName.length());

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("즐겨찾기 이름 입력");
		dialog.setMessage("정류소 즐겨찾기 이름을 입력 해주세요.")
				.setView(renameText)
				.setCancelable(false)
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						if (mUserDb.insertFavorite2(renameText.getText()
								.toString(), mStopId)) {
							Toast.makeText(getApplicationContext(),
									"즐겨찾기를 추가 하였습니다.", Toast.LENGTH_SHORT)
									.show();
							mIsFavorite = true;
						}

						if (mIsFavorite) {
							mBtnFavorite.setText("즐겨찾기 삭제");
						} else {
							mBtnFavorite.setText("즐겨찾기 추가");
						}

					}
				})
				.setNegativeButton("취소", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = dialog.create();
		alert.show();
	}

	private void shareKakao() {
		String strMessage = mStopName + "(" + mStopId + ")";
		String strURL = "busanbus://stop/detail?" + "busstop=" + mStopId;

		String strAppId = getPackageName();
		String strAppVer = "2.0";
		String strAppName = "부산버스";
		String strInstallUrl = "market://details?id=com.kmshack.BusanBus";

		try {
			ArrayList<Map<String, String>> arrMetaInfo = new ArrayList<Map<String, String>>();

			Map<String, String> metaInfoAndroid = new Hashtable<String, String>(
					1);
			metaInfoAndroid.put("os", "android");
			metaInfoAndroid.put("devicetype", "phone");
			metaInfoAndroid.put("installurl", strInstallUrl);
			metaInfoAndroid.put("executeurl", strURL);
			arrMetaInfo.add(metaInfoAndroid);

			KakaoLink link = new KakaoLink(this, strURL, strAppId, strAppVer,
					strMessage, strAppName, arrMetaInfo, "UTF-8");

			if (link.isAvailable()) {
				startActivity(link.getIntent());
			} else {
				Toast.makeText(getApplicationContext(), "카카오톡 설치 후 이용 가능합니다.",
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void loadArrive() {

		final Cursor cursor = mBusDb.selectReatime(mStopId);

		if (cursor.getCount() <= 0)
			return;

		mItems.clear();
		mMyListAdapter = null;

		mCount = 0;

		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			final String url = "http://121.174.75.12/01/011.html.asp?bstop_id="
					+ cursor.getString(1) + "&line_id=" + cursor.getString(0);
			final HtmlsAsync task = new HtmlsAsync();
			task.setTitle(cursor.getString(2) + "번 노선 / " + cursor.getString(3)
					+ "↔" + cursor.getString(5));
			task.setOnTapUpListener(new PostListener() {
				public void onPost(String html) {
					mCount++;

					String bstime;
					if (html != null) {
						if (html.indexOf("차량이") != -1) {
							String infotmp1 = html.substring(
									html.indexOf("분후") - 2, html.indexOf("분후"));
							String infotmp2 = html.substring(
									html.indexOf("번째") - 2, html.indexOf("번째"));
							bstime = infotmp1.replace(" ", "0").toString()
									+ "분후, "
									+ infotmp2.replace(" ", "").toString()
									+ "번째 전 정류소";

							if (html.indexOf(">2.") != -1) {
								String infotmp3 = html.substring(
										html.lastIndexOf("분후") - 2,
										html.lastIndexOf("분후"));
								String infotmp4 = html.substring(
										html.lastIndexOf("번째") - 2,
										html.lastIndexOf("번째"));

								bstime += "\n"
										+ infotmp3.replace(" ", "0").toString()
										+ "분후, "
										+ infotmp4.replace(" ", "").toString()
										+ "번째 전 정류소";
							}
						} else {
							bstime = "도착정보가 없습니다.";
						}
					} else {
						bstime = "도착정보가 없습니다.";
					}

					mMyItem = new MyItem(task.getTitle(), bstime.toString());
					mItems.add(mMyItem);

					if (mCount == cursor.getCount()) {
						mMyListAdapter = new MyListAdapter(
								getApplicationContext(),
								R.layout.item_realtimelist, mItems);
						mListView.setAdapter(mMyListAdapter);

						cursor.close();

						dismissDialog();
					}
				}
			});
			showDialog();
			task.execute(url);

		}

	}

	class MyItem implements Comparable {
		String item1, item2;

		MyItem(String i1, String i2) {
			item1 = i1;
			item2 = i2;
		}

		public int compareTo(Object another) {

			if (mBusanBusPrefrence.getIsArriveSort())
				return item2.compareTo(((MyItem) another).item2);
			else {
				return item1.compareTo(((MyItem) another).item1);
			}
		}
	}

	class MyListAdapter extends BaseAdapter {
		LayoutInflater Inflater;
		ArrayList<MyItem> arSrc;
		int layout;

		@SuppressWarnings("unchecked")
		public MyListAdapter(Context context, int alayout,
				ArrayList<MyItem> aarSrc) {
			Inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

			TextView txt1 = (TextView) convertView.findViewById(R.id.text1);
			txt1.setText(arSrc.get(position).item1);

			TextView txt2 = (TextView) convertView.findViewById(R.id.text2);
			txt2.setText(arSrc.get(position).item2);

			return convertView;
		}

	}

	private void shortcutDialog() {
		final EditText renameText = new EditText(this);
		renameText.setText(mStopName.toString());
		renameText.setSelection(mStopName.length());

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setIcon(R.drawable.ic_dialog_info);
		dialog.setMessage("바탕화면에 바로가기 이름을 입력 해주세요.")
				.setView(renameText)
				.setCancelable(false)
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent intent = new Intent();
						intent.setComponent(new ComponentName(
								"com.kmshack.BusanBus",
								"com.kmshack.BusanBus.activity.BusstopDetailActivity"));

						intent.putExtra("BusStop", mStopId);

						Intent result = new Intent();
						result.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

						result.putExtra(Intent.EXTRA_SHORTCUT_NAME, renameText
								.getText().toString());

						Bitmap src = BitmapFactory.decodeResource(
								getResources(), R.drawable.link);
						result.putExtra(Intent.EXTRA_SHORTCUT_ICON, src);

						result.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
						sendBroadcast(result);

						Toast.makeText(BusstopDetailActivity.this,
								"바탕화면 바로가기 생성 완료.", Toast.LENGTH_SHORT).show();

					}
				})
				.setNegativeButton("취소", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		AlertDialog alert = dialog.create();
		alert.setTitle("바로가기 이름 입력");
		alert.setIcon(R.drawable.ic_dialog_info);
		alert.setIcon(R.drawable.link);
		alert.show();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
