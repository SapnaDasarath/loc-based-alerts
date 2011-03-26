package com.sunysb.edu.ui.map;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.sunysb.edu.R;

public class MapOverlay extends com.google.android.maps.Overlay
{
	MapActivity map;
	GeoPoint p;

	public MapOverlay(MapActivity map)
	{
		this.map = map;
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) 
	{
		super.draw(canvas, mapView, shadow);                   

		//---translate the GeoPoint to screen pixels---
		if(p!=null)
		{
			Point screenPts = new Point();
			mapView.getProjection().toPixels(p, screenPts);

			//---add the marker---
			Bitmap bmp = BitmapFactory.decodeResource(map.getResources(), R.drawable.pushpin);            
			canvas.drawBitmap(bmp, screenPts.x, screenPts.y-50, null);     
		}

		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) 
	{   
		//---when user lifts his finger---
		if (event.getAction() == 1) {                
			p = mapView.getProjection().fromPixels((int) event.getX(),(int) event.getY());

			Geocoder geoCoder = new Geocoder(map.getBaseContext(), Locale.getDefault());
			try {
				List<Address> addresses = geoCoder.getFromLocation(p.getLatitudeE6()/ 1E6, p.getLongitudeE6()/ 1E6, 1);

				String add = "";
				if (addresses.size() > 0) 
				{
					for (int i=0; i<addresses.get(0).getMaxAddressLineIndex(); i++)
						add += addresses.get(0).getAddressLine(i) + "\n";
				}

				Toast.makeText(map.getBaseContext(), add, Toast.LENGTH_SHORT).show();
			}
			catch (IOException e) {                
				e.printStackTrace();
			}   
			return true;
		}
		else                
			return false;
	}           
} 
