package com.kmshack.BusanBus.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

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
import com.kmshack.BusanBus.database.BusDb;

public class NosunMapActivity extends BaseActivity {
	SQLiteDatabase db;
	String nosunnum;
	String rex, rey;
	int c;

	private GoogleMap mGoogleMap;
	private BusDb mBusDb;
	public Cursor mCursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		mBusDb = BusDb.getInstance(getApplicationContext());

		try {
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}

		Intent intent = getIntent();
		nosunnum = intent.getStringExtra("NOSUN");

		setTitle(nosunnum + "¹ø ³ë¼±");

		tracker.trackPageView("/NosunMap");

		mCursor = mBusDb.selectBusline(nosunnum);

		if (mCursor.moveToFirst()) {

			int count = mCursor.getCount();

			int center = count / 2;
			int doubleCenter = count / 4;

			LatLng centerLatlng = null;
			LatLng beforeLatlng = null;
			LatLng currentLatlng = null;

			int lineColorForUpper = Color.parseColor("#550000FF");
			int lineColorForDownner = Color.parseColor("#5500FF00");

			while (mCursor.moveToNext()) {

				int position = mCursor.getPosition();
				double latitude = mCursor.getDouble(8);
				double longitude = mCursor.getDouble(7);

				String id = mCursor.getString(9);
				String name = mCursor.getString(3);

				if (mGoogleMap != null) {
					currentLatlng = new LatLng(latitude, longitude);

					MarkerOptions marker = new MarkerOptions().position(currentLatlng).title(position + ". " + name).snippet(id);
					if (center < position) {
						marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
						if (beforeLatlng != null) {
							mGoogleMap.addPolyline(new PolylineOptions().add(beforeLatlng, currentLatlng).geodesic(true).width(7).color(lineColorForUpper));
						}

					} else {
						marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
						if (beforeLatlng != null) {
							mGoogleMap.addPolyline(new PolylineOptions().add(beforeLatlng, currentLatlng).geodesic(true).width(7).color(lineColorForDownner));
						}
					}

					if (position == doubleCenter) {
						centerLatlng = currentLatlng;
					}

					beforeLatlng = currentLatlng;

					if (position == 1)
						mGoogleMap.addMarker(marker).showInfoWindow();
					else
						mGoogleMap.addMarker(marker);
				}
			}

			if (mGoogleMap != null) {
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

	}

	@Override
	protected void onDestroy() {
		if (mCursor != null && !mCursor.isClosed())
			mCursor.close();

		super.onDestroy();
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
