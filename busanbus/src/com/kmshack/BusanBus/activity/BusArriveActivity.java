package com.kmshack.BusanBus.activity;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
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
import com.kmshack.BusanBus.database.UserDb;
import com.kmshack.BusanBus.task.BaseAsyncTask.PostListener;
import com.kmshack.BusanBus.task.HtmlAsync;

public class BusArriveActivity extends BaseActivity {
	private final static int PARSING_ARRIVA_INFORMATION = 1000;

	private GoogleMap mGoogleMap;

	private BusDb mBusDb;
	private UserDb mUserDb;

	private TextView text4, text6;

	private String nosun, stopid, stopname = "", ord = "", realtime = "", line_id = "", updown = "", x = "", y = "";
	private String html;

	private TextView mBtnMap, mBtnShortCut, mBtnFavorite, mBtnReload, mBtnShareKakao;

	private boolean isFavorite;

	private HtmlAsync mTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.busarrive);

		mBusDb = BusDb.getInstance(getApplicationContext());
		mUserDb = UserDb.getInstance(getApplicationContext());

		tracker.trackPageView("/BusArrive");

		Intent intent = getIntent();
		nosun = intent.getStringExtra("NOSUN");
		stopid = intent.getStringExtra("UNIQUEID");

		if (nosun == null || stopid == null) {
			Toast.makeText(getApplicationContext(), "지원하지 않는 정류소입니다.", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		try {
			initilizeMap();
		} catch (Exception e) {
			e.printStackTrace();
		}

		stopname = mBusDb.selectStopIdToName(stopid);
		ord = intent.getStringExtra("ORD");
		updown = intent.getStringExtra("UPDOWN");

		text4 = (TextView) findViewById(R.id.busarrive_text4);
		text6 = (TextView) findViewById(R.id.busarrive_text6);

		mBtnShortCut = (TextView) findViewById(R.id.busarrive_bt0);
		mBtnFavorite = (TextView) findViewById(R.id.busarrive_bt1);
		mBtnReload = (TextView) findViewById(R.id.busarrive_bt2);
		mBtnShareKakao = (TextView) findViewById(R.id.busarrive_bt3);
		mBtnMap = (TextView) findViewById(R.id.busstopdetail_locationbt);

		setTitle(nosun + "번 노선 - " + stopname + "(" + stopid + ")");

		isFavorite = mUserDb.isRegisterFavorite(nosun, stopid);
		if (isFavorite) {
			mBtnFavorite.setText("즐겨찾기 삭제");
		} else {
			mBtnFavorite.setText("즐겨찾기 추가");
		}

		realtime();

		mBtnShortCut.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				shortcut_dlg();
			}
		});

		mBtnMap.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent mapv = new Intent(BusArriveActivity.this, BusMapActivity.class);
				mapv.putExtra("X", x.toString());
				mapv.putExtra("Y", y.toString());

				mapv.putExtra("NOSUN", nosun);
				mapv.putExtra("NAME", stopname);
				mapv.putExtra("UNIQUEID", stopid);

				mapv.putExtra("TITLE", nosun + "번 노선 - " + stopname + "(" + stopid + ")");
				mapv.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mapv);
			}
		});
		mBtnShareKakao.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				shareKakao();
			}
		});

		// 새로고침
		mBtnReload.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				realtime();
			}
		});

		// 즐겨찾기 버튼 클릭
		mBtnFavorite.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				tracker.trackEvent("IconClicks", // Category
						"Favorite", // Action
						"즐겨찾기", // Label
						0); // Value

				if (isFavorite) {
					if (mUserDb.deleteFavorite(nosun, stopid)) {
						Toast.makeText(BusArriveActivity.this, "즐겨찾기를 삭제 하였습니다.", Toast.LENGTH_SHORT).show();
						isFavorite = false;
					}
				} else {
					favoriteDialog();
				}

				if (isFavorite) {
					mBtnFavorite.setText("즐겨찾기 삭제");
				} else {
					mBtnFavorite.setText("즐겨찾기 추가");
				}

			}
		});
	}

	private void favoriteDialog() {
		final EditText renameText = new EditText(this);
		renameText.setText(stopname);
		renameText.setSelection(stopname.length());

		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setTitle("즐겨찾기 이름 입력");
		alt_bld.setMessage("노선 즐겨찾기 이름을 입력 해주세요.").setView(renameText).setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				if (mUserDb.insertFavorite(nosun, stopid, renameText.getText().toString(), updown, realtime, ord)) {
					Toast.makeText(BusArriveActivity.this, "즐겨찾기를 추가 하였습니다.", Toast.LENGTH_SHORT).show();
					isFavorite = true;
				}

				if (isFavorite) {
					mBtnFavorite.setText("즐겨찾기 삭제");
				} else {
					mBtnFavorite.setText("즐겨찾기 추가");
				}

			}
		}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = alt_bld.create();
		alert.show();
	}

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PARSING_ARRIVA_INFORMATION:
				if (html.indexOf("차량이") != -1) {
					String infotmp1 = html.substring(html.indexOf("분후") - 2, html.indexOf("분후"));
					String infotmp2 = html.substring(html.indexOf("번째") - 2, html.indexOf("번째"));
					String infotmp3 = html.substring(html.indexOf("번 차량이") - 4, html.indexOf("번 차량이"));

					text4.setText(infotmp1.replace(" ", "").toString() + "분후 도착\n" + infotmp2.replace(" ", "").toString() + "번째 전 정류소\n" + infotmp3.toString()
							+ "번 차량");
				} else {
					text4.setText("도착정보 없음.");
				}

				if (html.indexOf(">2.") != -1) {
					String infotmp1 = html.substring(html.lastIndexOf("분후") - 2, html.lastIndexOf("분후"));
					String infotmp2 = html.substring(html.lastIndexOf("번째") - 2, html.lastIndexOf("번째"));
					String infotmp3 = html.substring(html.lastIndexOf("번 차량이") - 4, html.lastIndexOf("번 차량이"));

					text6.setText(infotmp1.replace(" ", "").toString() + "분후 도착\n" + infotmp2.replace(" ", "").toString() + "번째 전 정류소\n" + infotmp3.toString()
							+ "번 차량");
				} else {
					text6.setText("도착정보 없음.");
				}
				break;
			}
		}
	};

	private void shareKakao() {
		String strMessage = nosun + "번 노선 - " + stopname + "(" + stopid + ")";
		String strURL = "busanbus://line/detail?" + "nosun=" + nosun + "&uniqueid=" + stopid + "&ord=" + ord + "&busstopname=" + stopname + "&updown=" + updown;
		String strAppId = getPackageName();
		String strAppVer = "2.0";
		String strAppName = "부산버스";
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
				Toast.makeText(getApplicationContext(), "카카오톡 설치 후 이용 가능합니다.", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		tracker.trackEvent("IconClicks", // Category
				"Kakao", // Action
				"카카오톡 링크", // Label
				0); // Value

	}

	public void realtime() {

		tracker.trackEvent("IconClicks", // Category
				"ReLoad", // Action
				"새로고침", // Label
				0); // Value

		// progDailog = ProgressDialog.show(this, null, "잠시만 기다려 주세요.",true);
		showDialog();
		Cursor cursor = mBusDb.selectReatime(stopid, nosun, ord);

		if (cursor.getCount() > 0) {

			cursor.moveToFirst();
			if (cursor.getCount() != 0 && !cursor.isNull(1)) {
				cursor.moveToFirst();

				line_id = cursor.getString(0);
				realtime = cursor.getString(1);
				double latitude = cursor.getDouble(7);
				double longitude = cursor.getDouble(6);

				x = String.valueOf(longitude).replace(" ", "");
				y = String.valueOf(latitude).replace(" ", "");

				if (mGoogleMap != null) {
					LatLng latlng = new LatLng(latitude, longitude);

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
				String url = "http://121.174.75.12/01/011.html.asp?bstop_id=" + realtime.toString() + "&line_id=" + line_id.toString();

				mTask = new HtmlAsync();
				mTask.setOnTapUpListener(new PostListener() {
					public void onPost(String result) {
						dismissDialog();

						html = result;
						if (html != null) {
							handler.sendMessage(Message.obtain(handler, PARSING_ARRIVA_INFORMATION));
						} else {
							text4.setText("도착정보 없음.");
							text6.setText("도착정보 없음.");
						}

						text4.invalidate();
						text6.invalidate();

					}
				});

				mTask.execute(url);

			}
		} else {
			text4.setText("도착정보 없음.");
			text6.setText("도착정보 없음.");
		}

		cursor.close();

	}

	@Override
	protected void onDestroy() {
		if (mTask != null)
			mTask.cancel(true);

		super.onDestroy();
	}

	private void shortcut_dlg() {
		final EditText renameText = new EditText(this);
		String text = nosun.toString() + "_" + stopname.toString();
		renameText.setText(text);
		renameText.setSelection(text.length());

		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setMessage("바탕화면에 바로가기 이름을 입력 해주세요.").setView(renameText).setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Intent intent = new Intent();
				intent.setComponent(new ComponentName("com.kmshack.BusanBus", "com.kmshack.BusanBus.activity.BusArriveActivity"));
				intent.putExtra("NOSUN", nosun);
				intent.putExtra("UNIQUEID", stopid);
				intent.putExtra("BUSSTOPNAME", stopname);
				intent.putExtra("ORD", ord);
				intent.putExtra("UPDOWN", updown);

				Intent result = new Intent();
				result.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

				result.putExtra(Intent.EXTRA_SHORTCUT_NAME, renameText.getText().toString());

				Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.link);
				result.putExtra(Intent.EXTRA_SHORTCUT_ICON, src);

				result.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
				sendBroadcast(result);

				Toast.makeText(BusArriveActivity.this, "바탕화면 바로가기 생성 완료.", Toast.LENGTH_SHORT).show();

			}
		}).setNegativeButton("취소", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = alt_bld.create();
		alert.setTitle("바로가기 이름 입력");
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
				mGoogleMap.setMyLocationEnabled(true);
				mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
				mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
				mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

}
