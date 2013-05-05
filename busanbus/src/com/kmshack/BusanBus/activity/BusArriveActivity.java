package com.kmshack.BusanBus.activity;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import kr.hyosang.coordinate.CoordPoint;
import kr.hyosang.coordinate.TransCoord;
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

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.kmshack.BusanBus.KakaoLink;
import com.kmshack.BusanBus.MapOverlay;
import com.kmshack.BusanBus.R;
import com.kmshack.BusanBus.database.BusDb;
import com.kmshack.BusanBus.database.UserDb;
import com.kmshack.BusanBus.task.BaseAsyncTask.PostListener;
import com.kmshack.BusanBus.task.HtmlAsync;

/**
 * 도착정보(상세) 
 * @author kmshack
 *
 */
public class BusArriveActivity extends BaseMapActivity {

	private final static int PARSING_ARRIVA_INFORMATION = 1000;

	private BusDb mBusDb;
	private UserDb mUserDb;

	private HtmlAsync mTask;
	
	private TextView mFirstArrive, mSecondArrive;
	private TextView mBtnLocal, mBtnShortCut, mBtnFavorite, mBtnMap,
			mShareKakao;

	private MapView mMapView;
	
	private String mNosun, mStopId, mStopName = "", mOrd = "", mRealTime = "", mLineId = "", mHangSun = "", mX = "", mY = "";
	private String mHtmlString;

	private boolean mIsFavorite;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PARSING_ARRIVA_INFORMATION:
				if (mHtmlString.indexOf("차량이") != -1) {
					String infotmp1 = mHtmlString.substring(
							mHtmlString.indexOf("분후") - 2,
							mHtmlString.indexOf("분후"));
					String infotmp2 = mHtmlString.substring(
							mHtmlString.indexOf("번째") - 2,
							mHtmlString.indexOf("번째"));
					String infotmp3 = mHtmlString.substring(
							mHtmlString.indexOf("번 차량이") - 4,
							mHtmlString.indexOf("번 차량이"));

					mFirstArrive.setText(infotmp1.replace(" ", "").toString()
							+ "분후 도착\n" + infotmp2.replace(" ", "").toString()
							+ "번째 전 정류소\n" + infotmp3.toString() + "번 차량");
				} else {
					mFirstArrive.setText("도착정보 없음.");
				}

				if (mHtmlString.indexOf(">2.") != -1) {
					String infotmp1 = mHtmlString.substring(
							mHtmlString.lastIndexOf("분후") - 2,
							mHtmlString.lastIndexOf("분후"));
					String infotmp2 = mHtmlString.substring(
							mHtmlString.lastIndexOf("번째") - 2,
							mHtmlString.lastIndexOf("번째"));
					String infotmp3 = mHtmlString.substring(
							mHtmlString.lastIndexOf("번 차량이") - 4,
							mHtmlString.lastIndexOf("번 차량이"));

					mSecondArrive.setText(infotmp1.replace(" ", "").toString()
							+ "분후 도착\n" + infotmp2.replace(" ", "").toString()
							+ "번째 전 정류소\n" + infotmp3.toString() + "번 차량");
				} else {
					mSecondArrive.setText("도착정보 없음.");
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.busarrive);

		mBusDb = BusDb.getInstance(getApplicationContext());
		mUserDb = UserDb.getInstance(getApplicationContext());

		Intent intent = getIntent();
		mNosun = intent.getStringExtra("NOSUN");
		mStopId = intent.getStringExtra("UNIQUEID");

		if (mNosun == null || mStopId == null) {
			Toast.makeText(getApplicationContext(), "지원하지 않는 정류소입니다.",
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		mStopName = mBusDb.selectStopIdToName(mStopId);
		mOrd = intent.getStringExtra("ORD");
		mHangSun = intent.getStringExtra("UPDOWN");

		mMapView = (MapView) findViewById(R.id.mapview);
		mFirstArrive = (TextView) findViewById(R.id.busarrive_text4);
		mSecondArrive = (TextView) findViewById(R.id.busarrive_text6);

		mBtnShortCut = (TextView) findViewById(R.id.busarrive_bt0);
		mBtnFavorite = (TextView) findViewById(R.id.busarrive_bt1);
		mBtnMap = (TextView) findViewById(R.id.busarrive_bt2);
		mShareKakao = (TextView) findViewById(R.id.busarrive_bt3);
		mBtnLocal = (TextView) findViewById(R.id.busstopdetail_locationbt);

		setTitleLeft(mNosun + "번 노선 - " + mStopName + "(" + mStopId + ")");

		mIsFavorite = mUserDb.isRegisterFavorite(mNosun, mStopId);
		if (mIsFavorite) {
			mBtnFavorite.setText("즐겨찾기 삭제");
		} else {
			mBtnFavorite.setText("즐겨찾기 추가");
		}

		loadArrive();

		mBtnShortCut.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				shortcutDialog();
			}
		});

		mBtnLocal.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent mapv = new Intent(BusArriveActivity.this,
						BusMapActivity.class);
				mapv.putExtra("X", mX.toString());
				mapv.putExtra("Y", mY.toString());
				mapv.putExtra("TITLE", mNosun + "번 노선 - " + mStopName + "("
						+ mStopId + ")");
				mapv.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(mapv);
			}
		});
		mShareKakao.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				shareKakao();
			}
		});

		// 새로고침
		mBtnMap.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				loadArrive();
			}
		});

		// 즐겨찾기 버튼 클릭
		mBtnFavorite.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {

				if (mIsFavorite) {
					if (mUserDb.deleteFavorite(mNosun, mStopId)) {
						Toast.makeText(BusArriveActivity.this,
								"즐겨찾기를 삭제 하였습니다.", Toast.LENGTH_SHORT).show();
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
		});
	}

	private void favoriteDialog() {
		final EditText renameText = new EditText(this);
		renameText.setText(mStopName);
		renameText.setSelection(mStopName.length());

		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setTitle("즐겨찾기 이름 입력");
		alt_bld.setMessage("노선 즐겨찾기 이름을 입력 해주세요.")
				.setView(renameText)
				.setCancelable(false)
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						if (mUserDb.insertFavorite(mNosun, mStopId, renameText
								.getText().toString(), mHangSun, mRealTime,
								mOrd)) {
							Toast.makeText(BusArriveActivity.this,
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
		AlertDialog alert = alt_bld.create();
		alert.show();
	}

	private void shareKakao() {
		String strMessage = mNosun + "번 노선 - " + mStopName + "(" + mStopId
				+ ")";
		String strURL = "busanbus://line/detail?" + "nosun=" + mNosun
				+ "&uniqueid=" + mStopId + "&ord=" + mOrd + "&busstopname="
				+ mStopName + "&updown=" + mHangSun;
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

		showDialog();
		Cursor cursor = mBusDb.selectReatime(mStopId, mNosun, mOrd);

		if (cursor.getCount() > 0) {

			cursor.moveToFirst();
			if (cursor.getCount() != 0 && !cursor.isNull(1)) {
				cursor.moveToFirst();

				mLineId = cursor.getString(0);
				mRealTime = cursor.getString(1);
				mX = cursor.getString(6).replace(" ", "");
				mY = cursor.getString(7).replace(" ", "");

				CoordPoint tm = new CoordPoint(Double.parseDouble(mX),
						Double.parseDouble(mY));
				CoordPoint wgs = TransCoord.getTransCoord(tm,
						TransCoord.COORD_TYPE_WGS84,
						TransCoord.COORD_TYPE_WGS84);

				GeoPoint mapPoint = new GeoPoint((int) ((wgs.y) * 1E6),
						(int) ((wgs.x) * 1E6));

				mMapView.setBuiltInZoomControls(false);
				mMapView.getController().setCenter(mapPoint);
				mMapView.getController().setZoom(19);

				mMapView.getOverlays().add(
						new MapOverlay(mapPoint, getApplicationContext()));

				String url = "http://121.174.75.12/01/011.html.asp?bstop_id="
						+ mRealTime.toString() + "&line_id="
						+ mLineId.toString();

				mTask = new HtmlAsync();
				mTask.setOnTapUpListener(new PostListener() {
					public void onPost(String result) {
						dismissDialog();

						mHtmlString = result;
						if (mHtmlString != null) {
							mHandler.sendMessage(Message.obtain(mHandler,
									PARSING_ARRIVA_INFORMATION));
						} else {
							mFirstArrive.setText("도착정보 없음.");
							mSecondArrive.setText("도착정보 없음.");
						}

						mFirstArrive.invalidate();
						mSecondArrive.invalidate();

					}
				});

				mTask.execute(url);

			}
		} else {
			mFirstArrive.setText("도착정보 없음.");
			mSecondArrive.setText("도착정보 없음.");
		}

		cursor.close();

	}

	@Override
	protected void onDestroy() {
		if (mTask != null)
			mTask.cancel(true);

		super.onDestroy();
	}

	private void shortcutDialog() {
		final EditText renameText = new EditText(this);
		String text = mNosun.toString() + "_" + mStopName.toString();
		renameText.setText(text);
		renameText.setSelection(text.length());

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
								"com.kmshack.BusanBus.activity.BusArriveActivity"));
						intent.putExtra("NOSUN", mNosun);
						intent.putExtra("UNIQUEID", mStopId);
						intent.putExtra("BUSSTOPNAME", mStopName);
						intent.putExtra("ORD", mOrd);
						intent.putExtra("UPDOWN", mHangSun);

						Intent result = new Intent();
						result.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

						result.putExtra(Intent.EXTRA_SHORTCUT_NAME, renameText
								.getText().toString());

						Bitmap src = BitmapFactory.decodeResource(
								getResources(), R.drawable.link);
						result.putExtra(Intent.EXTRA_SHORTCUT_ICON, src);

						result.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
						sendBroadcast(result);

						Toast.makeText(BusArriveActivity.this,
								"바탕화면 바로가기 생성 완료.", Toast.LENGTH_SHORT).show();

					}
				})
				.setNegativeButton("취소", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = dialog.create();
		alert.setIcon(R.drawable.ic_dialog_info);
		alert.setTitle("바로가기 이름 입력");
		alert.setIcon(R.drawable.link);
		alert.show();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
