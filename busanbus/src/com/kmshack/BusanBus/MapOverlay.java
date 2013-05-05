package com.kmshack.BusanBus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * MapOverlay
 * @author kmshack
 *
 */
public class MapOverlay extends Overlay {
	
	private GeoPoint mMapPoint;
	private Context mContext;

	public MapOverlay(GeoPoint mapPoint, Context context) {
		mMapPoint = mapPoint;
		mContext = context;
	}

	@Override
	public boolean draw(Canvas arg0, MapView arg1, boolean arg2, long w) {
		super.draw(arg0, arg1, arg2);

		Point ScreenPoint = new Point();
		arg1.getProjection().toPixels(mMapPoint, ScreenPoint);

		Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.marker_special);
		arg0.drawBitmap(bmp, ScreenPoint.x - 10, ScreenPoint.y - 16, null);
		return true;
	}
}
