package com.kmshack.BusanBus.activity;

import java.util.ArrayList;
import java.util.List;

import kr.hyosang.coordinate.CoordPoint;
import kr.hyosang.coordinate.TransCoord;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.kmshack.BusanBus.R;

/**
 * 특정 노선의 모든 정류소 지도
 * @author kmshack
 *
 */
public class NosunMapActivity extends BaseMapActivity {
	private SQLiteDatabase mDb;
	private String mNosunName;
	public Cursor mCursor;
	private int mCenterCount;

	private MapView mMapView = null;
	private MyLocationOverlay mMyLocationOverlay = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		Intent intent = getIntent();
		mNosunName = intent.getStringExtra("NOSUN");
		mCenterCount = intent.getIntExtra(("COUNT"), 1);

		setTitleLeft(mNosunName + "번 노선");

		openDatabase();

		mCursor = mDb.rawQuery("select * from BUSLINE where BUSLINENUM ='"
				+ mNosunName.toString() + "'", null);

		startManagingCursor(mCursor);

		mMapView = (MapView) findViewById(R.id.mapview);
		mMapView.getController().setZoom(14);
		mMapView.setBuiltInZoomControls(true);

		Drawable marker = getResources().getDrawable(R.drawable.marker_special);

		mMapView.getOverlays().add(new SitesOverlay(marker, this));

		mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
		mMapView.getOverlays().add(mMyLocationOverlay);
		mMapView.getOverlays().add(new LinesOverlay(mCursor, mMapView));

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onResume() {
		super.onResume();
		mMyLocationOverlay.enableCompass();
	}

	@Override
	public void onPause() {
		super.onPause();
		mMyLocationOverlay.disableCompass();
	}

	private void openDatabase() {
		try {
			mDb = SQLiteDatabase
					.openDatabase(
							Environment.getExternalStorageDirectory().getPath()
									+ "/Android/data/com.kmshack.BusanBus/databases/BusData.kms",
							null, SQLiteDatabase.CREATE_IF_NECESSARY);
		} catch (Exception e) {
		}
	}

	private GeoPoint getPoint(double lat, double lon) {
		return (new GeoPoint((int) (lat * 1000000.0), (int) (lon * 1000000.0)));
	}

	private class LinesOverlay extends Overlay {

		private Cursor mCursor;
		private Point targetPosition;
		private Point startPosition;
		private CoordPoint tm;
		private CoordPoint wgs;
		private Paint paintLine;
		private GeoPoint start[];
		private GeoPoint end[];

		public LinesOverlay(Cursor cursor, MapView mapview) {
			mCursor = cursor;
			targetPosition = new Point();
			startPosition = new Point();

			paintLine = new Paint();
			paintLine.setStrokeWidth(6);

			start = new GeoPoint[mCursor.getCount()];
			end = new GeoPoint[mCursor.getCount()];

			for (int i = 0; i < mCursor.getCount(); i++) {

				if (mCursor.moveToPosition(i)) {
					tm = new CoordPoint(Double.parseDouble(mCursor.getString(7)
							.replace(" ", "").toString()),
							Double.parseDouble(mCursor.getString(8)
									.replace(" ", "").toString()));
					wgs = TransCoord.getTransCoord(tm,
							TransCoord.COORD_TYPE_WGS84,
							TransCoord.COORD_TYPE_WGS84);
					start[i] = getPoint(wgs.y, wgs.x);

					if (mCursor.moveToNext()) {
						tm = new CoordPoint(Double.parseDouble(mCursor
								.getString(7).replace(" ", "").toString()),
								Double.parseDouble(mCursor.getString(8)
										.replace(" ", "").toString()));
						wgs = TransCoord.getTransCoord(tm,
								TransCoord.COORD_TYPE_WGS84,
								TransCoord.COORD_TYPE_WGS84);
						end[i] = getPoint(wgs.y, wgs.x);
					}
				}
			}

		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {

			for (int i = 0; i < mCursor.getCount(); i++) {
				if (mCursor.moveToPosition(i)) {
					if (start[i] != null)
						mapView.getProjection().toPixels(start[i],
								startPosition);

					if (mCursor.moveToNext()) {
						if (end[i] != null)
							mapView.getProjection().toPixels(end[i],
									targetPosition);

						if (i > mCursor.getCount() / 2) {
							paintLine.setARGB(80, 255, 150, 0);
							canvas.drawLine(startPosition.x, startPosition.y,
									targetPosition.x, targetPosition.y,
									paintLine);
						} else {
							paintLine.setARGB(80, 0, 255, 0);
							canvas.drawLine(startPosition.x, startPosition.y,
									targetPosition.x, targetPosition.y,
									paintLine);
						}

					}
				}
			}

			super.draw(canvas, mapView, shadow);
		}

	}

	private class SitesOverlay extends ItemizedOverlay<OverlayItem> {
		private List<OverlayItem> items = new ArrayList<OverlayItem>();
		private Drawable marker = null;
		private Context mContext;

		public SitesOverlay(Drawable marker, Context context) {
			super(marker);
			mContext = context;
			this.marker = marker;

			mCursor.moveToFirst();

			CoordPoint tm;
			CoordPoint wgs;

			while (mCursor.moveToNext()) {
				tm = new CoordPoint(Double.parseDouble(mCursor.getString(7)
						.replace(" ", "").toString()),
						Double.parseDouble(mCursor.getString(8)
								.replace(" ", "").toString()));
				wgs = TransCoord.getTransCoord(tm, TransCoord.COORD_TYPE_WGS84,
						TransCoord.COORD_TYPE_WGS84);

				items.add(new OverlayItem(getPoint(wgs.y, wgs.x), null, null));

				if (mCursor.getPosition() == mCenterCount)
					mMapView.getController().setCenter(getPoint(wgs.y, wgs.x));
			}
			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return (items.get(i));
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			boundCenterBottom(marker);
		}

		@Override
		public int size() {
			return (items.size());
		}

		@Override
		protected boolean onTap(int index) {
			if (mCursor.moveToPosition(index + 1)) {
				final String name = mCursor.getString(3);
				final String id = mCursor.getString(9);

				AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
				alert.setTitle("정류소 확인");
				alert.setMessage("정류소 명: " + name + "\n" + "정류소 번호: " + id
						+ "\n\n" + "도착정보 페이지로 이동 하시겠습니까?");

				alert.setPositiveButton("예",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (id.equals("0")) {
									Toast.makeText(mContext,
											"도착정보가 지원되지 않는 정류소입니다.",
											Toast.LENGTH_SHORT).show();
									dialog.dismiss();
								} else {
									Intent busarr = new Intent(mContext,
											BusstopDetailActivity.class);
									busarr.putExtra("BusStop", id);
									busarr.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									startActivity(busarr);
								}
							}
						});

				alert.setNegativeButton("아니오",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				alert.show();
			}
			return true;
		}

	}

}
