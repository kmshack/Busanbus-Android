package com.kmshack.BusanBus.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kmshack.BusanBus.R;

public class BusMapActivity extends BaseActivity {

	private GoogleMap mGoogleMap;

	private double x, y;
	private String title;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		try {
			// Loading map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}

		tracker.trackPageView("/BusMap");

		Intent intent = getIntent();
		x = Double.parseDouble(intent.getStringExtra("X"));
		y = Double.parseDouble(intent.getStringExtra("Y"));
		title = intent.getStringExtra("TITLE");
		setTitle(title);

		String title = intent.getStringExtra("NAME").replace("정류소명: ", "");
		String snippet = intent.getStringExtra("UNIQUEID").replace("정류소번호: ", "");

		if (mGoogleMap != null) {
			double latitude = y;
			double longitude = x;

			LatLng latlng = new LatLng(latitude, longitude);

			// create marker
			MarkerOptions marker = new MarkerOptions().position(latlng).title(title).snippet(snippet);
			marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
			// adding marker
			mGoogleMap.addMarker(marker).showInfoWindow();

			CameraPosition INIT = new CameraPosition.Builder().target(latlng).zoom(18F).bearing(0F) // orientation
					.tilt(0F) // viewing angle
					.build();
			mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(INIT));
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
