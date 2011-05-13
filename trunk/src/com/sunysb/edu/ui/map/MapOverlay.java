package com.sunysb.edu.ui.map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.sunysb.edu.R;

public class MapOverlay extends com.google.android.maps.Overlay {
	private MapActivity map;
	private GeoPoint p;

	public MapOverlay(Map map) {
		this.map = map;
		p = map.initGeoPoint;
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,	long when) {
		super.draw(canvas, mapView, shadow);
		if (p != null) {
			Point screenPts = new Point();
			mapView.getProjection().toPixels(p, screenPts);

			Bitmap bmp = BitmapFactory.decodeResource(map.getResources(),	R.drawable.pushpin);
			canvas.drawBitmap(bmp, screenPts.x, screenPts.y - 50, null);
		} 
		return true;
	}
}
