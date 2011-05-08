package com.sunysb.edu.ui.map;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.OverlayItem;
import com.sunysb.edu.LocationBasedAlerts;
import com.sunysb.edu.R;
import com.sunysb.edu.ui.dialog.TaskScreen;
import com.sunysb.edu.util.StringUtil;

public class Map extends MapActivity {

	private MapView mapView;

	private Button addTaskButton;
	private Button btnSearch;
	
	//Button btnSearch=(Button) findViewById(R.id.search);
	
	public void changeMap(String area){
		
        mapView = (MapView) findViewById(R.id.mapView);
        MapController mc=mapView.getController();
        //MapOverlay mapOverlays;
        
        
        GeoPoint myLocation=null;
        double lat = 0;
        double lng = 0;
        try
        {
       	 	Geocoder g = new Geocoder(this, Locale.getDefault()); 

            java.util.List<android.location.Address> result=g.getFromLocationName(area, 1); 
            if(result.size()>0){
            	Toast.makeText(Map.this, "country: " + String.valueOf(result.get(0).getCountryName()), Toast.LENGTH_SHORT).show();
            	lat = result.get(0).getLatitude();
            	lng = result.get(0).getLongitude();
            }             
            else{
            	Toast.makeText(Map.this, "record not found", Toast.LENGTH_SHORT).show();
            	return;
            }
        }
        catch(IOException io)
        {
        	Toast.makeText(Map.this, "Connection Error", Toast.LENGTH_SHORT).show();
        }
        myLocation = new GeoPoint(
            (int) (lat * 1E6), 
            (int) (lng * 1E6));
 
        //moverlay = (MapOverlay) mapView.getOverlays();
        //Drawable drawable = this.getResources().getDrawable(R.drawable.pushpin);
        //MapOverlay itemizedoverlay = new MapOverlay(drawable);
        //drawable.draw(canvas);
        
        List<Overlay>mapOverlays =  mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.pushpin);
        MapItemizedOverlay itemizedOverlay = new MapItemizedOverlay(drawable);
       // GeoPoint point = new GeoPoint((int)lat, (int)lng);
       // OverlayItem overlayitem = new OverlayItem(point, "", "");
        OverlayItem overlayitem = new OverlayItem(myLocation, "", "");
        itemizedOverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedOverlay);

        
        mc.animateTo(myLocation);
        mc.setZoom(10); 
        mapView.invalidate(); //is it required?
        
        
        // ---add the marker---
		//Bitmap bmp = BitmapFactory.decodeResource(getResources(),	R.drawable.pushpin);
		//canvas.drawBitmap(bmp, screenPts.x, screenPts.y - 50, null);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadmap);

		btnSearch = (Button) findViewById(R.id.search_icon);
		btnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText txtSearch = (EditText) findViewById(R.id.search_box);
				String area = txtSearch.getText().toString();
				Map.this.changeMap(area);
			}
		});

		mapView = (MapView) findViewById(R.id.mapView);
		LinearLayout zoomLayout = (LinearLayout) findViewById(R.id.zoom);
		View zoomView = mapView.getZoomControls();
		zoomLayout.addView(zoomView, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		mapView.displayZoomControls(true);
		Log.e("LBA", "Zoom control display");

		MapOverlay mapOverlay = new MapOverlay(this);
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);

		mapView.invalidate();

		addTaskButton = (Button) findViewById(R.id.add_Task);

		addTaskButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.e("LBA", "Add Task dialog launched");
				Intent intent = new Intent(Map.this, TaskScreen.class);
				intent.putExtra(StringUtil.TRANSITION, StringUtil.CREATE);
				//TODO poo add activity lat and long values
				//intent.putExtra(StringUtil.TASK_LAT, value);
				//intent.putExtra(StringUtil.TASK_LONG, value);
				startActivity(intent);
			}
		});
	}


	@Override
	public void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		Log.e("LBA", "Inside handleIntent()");
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			Log.e("LBA", "Inside handleIntent() - Entered if block");
			String query = intent.getStringExtra(SearchManager.QUERY);
			doMySearch(query);
		}
	}

	private void doMySearch(String query) {
		Log.i("Search String {}", query);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public boolean onSearchRequested() {
		Log.e("LBA", "Inside onSearchRequested()");
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		Log.e("LBA", "Inside onCreateOptionsMenu()");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search:
			onSearchRequested();
			return true;
		default:
			return false;
		}
	}
}

