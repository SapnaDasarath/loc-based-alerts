package com.sunysb.edu.ui.map;

import java.util.List;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.MapView.LayoutParams;
import com.sunysb.edu.R;

public class Map extends MapActivity{

	private MapView mapView;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadmap);
		
		mapView = (MapView) findViewById(R.id.mapView);
		
		LinearLayout zoomLayout = (LinearLayout)findViewById(R.id.zoom);  
		View zoomView = mapView.getZoomControls(); 
		zoomLayout.addView(zoomView, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 
		mapView.displayZoomControls(true);
		
		MapOverlay mapOverlay = new MapOverlay(this);
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);
		
		mapView.invalidate();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void setLocation(MapView mapView, int latitude, int longitude)
	{
		MapController mc = mapView.getController();
		GeoPoint p = new GeoPoint(latitude, longitude);
		mc.animateTo(p);
		mc.setZoom(17); 
		System.out.print("Recieved click");
	}
}
