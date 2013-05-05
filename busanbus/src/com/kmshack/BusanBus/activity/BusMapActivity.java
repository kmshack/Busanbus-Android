package com.kmshack.BusanBus.activity;

import java.util.List;

import kr.hyosang.coordinate.CoordPoint;
import kr.hyosang.coordinate.TransCoord;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.kmshack.BusanBus.MapOverlay;
import com.kmshack.BusanBus.R;

/**
 * 특정 정류소 지도위치 
 * @author kmshack
 *
 */
public class BusMapActivity extends BaseMapActivity {

	private MapView mMapView;
	private int mMapZoom = 18;
	private GeoPoint mGeoPoint;
	private double mX, mY;
	private int mTransX, mTransY;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		Intent intent = getIntent();
		mX = Double.parseDouble(intent.getStringExtra("X"));
		mY = Double.parseDouble(intent.getStringExtra("Y"));

		setTitleLeft(intent.getStringExtra("TITLE"));

		CoordPoint tm = new CoordPoint(mX, mY);
		CoordPoint wgs = TransCoord.getTransCoord(tm,
				TransCoord.COORD_TYPE_WGS84, TransCoord.COORD_TYPE_WGS84);

		mTransY = (int) ((wgs.x) * 1E6);
		mTransX = (int) ((wgs.y) * 1E6);

		mGeoPoint = new GeoPoint(mTransX, mTransY);

		mMapView = (MapView) findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setSatellite(false);
		mMapView.getController().setCenter(mGeoPoint);
		mMapView.getController().setZoom(mMapZoom);

		MapOverlay mapov = new MapOverlay(mGeoPoint, this);

		List<Overlay> listOverlay = mMapView.getOverlays();
		listOverlay.clear();
		listOverlay.add(mapov);

		mMapView.invalidate();

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
