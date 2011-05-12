package com.sunysb.edu.ui.map;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.sunysb.edu.R;
import com.sunysb.edu.ui.dialog.TaskScreen;
import com.sunysb.edu.util.StringUtil;

public class Map extends MapActivity {

	private MapView mapView;
	private Button addTaskButton;
	private Button btnSearch;

	private double lat = 0;
	private double lng = 0;
	public GeoPoint initGeoPoint;

	//Get current loc of user. Update lat and lng. 
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadmap);
		
		//LocationManager locationManager;
	    //locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	    //Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
	    //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,(LocationListener) this);

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
		zoomLayout.addView(zoomView, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		mapView.displayZoomControls(true);
		Log.e("LBA", "Zoom control display");

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
			
			lat = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
			lng = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
			Log.e("LBA", "GotLastKnownLocation latitude "+String.valueOf(lat)+" longitude is "+String.valueOf(lng));
		}
		initGeoPoint = new GeoPoint((int) (lat * 1000000),(int) (lng * 1000000));

		MapOverlay mapOverlay = new MapOverlay(this);
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);

		mapView.invalidate();
		
		mapView.getController().animateTo(initGeoPoint);
		mapView.getController().setZoom(15);
			
		/*mapView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.e("LBA", "mapView clicked ");
				MapOverlay mapOverlay = new MapOverlay(Map.this);
				List<Overlay> listOfOverlays = mapView.getOverlays();
				listOfOverlays.clear();
				listOfOverlays.add(mapOverlay);
			}
		}); */

		addTaskButton = (Button) findViewById(R.id.add_Task);

		addTaskButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				callAddTask();
			}
		});
	}

	private void callAddTask() {
		Log.e("LBA", "Add Task dialog launched");
		Intent intent = new Intent(Map.this, TaskScreen.class);
		intent.putExtra(StringUtil.TRANSITION, StringUtil.CREATE);
		intent.putExtra(StringUtil.TASK_LAT, String.valueOf(lat));
		intent.putExtra(StringUtil.TASK_LONG, String.valueOf(lng));
		startActivity(intent);
	}

	public void changeMap(String area) {

		mapView = (MapView) findViewById(R.id.mapView);
		MapController mc = mapView.getController();

		GeoPoint myLocation = null;
		try {
			Geocoder g = new Geocoder(this, Locale.getDefault());

			java.util.List<android.location.Address> result = g.getFromLocationName(area, 3);//area,5 gives 5 suggestions
			if (result.size() > 0) {
				lat = result.get(0).getLatitude();
				lng = result.get(0).getLongitude();
				Log.e("LBA", "Found lat and lng in ChangeMap"+String.valueOf(lat)+" lng is"+String.valueOf(lng));
			} else {
				return;
			}
		} catch (IOException io) {
			
			Toast.makeText(Map.this, "Connection Error", Toast.LENGTH_SHORT).show();
		}
		myLocation = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));

		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.pushpin);
		MapItemizedOverlay itemizedOverlay = new MapItemizedOverlay(drawable);

		OverlayItem overlayitem = new OverlayItem(myLocation, "", "");
		itemizedOverlay.addOverlay(overlayitem);
		mapOverlays.add(itemizedOverlay);

		mc.animateTo(myLocation);
		mc.setZoom(16);
		mapView.invalidate(); // is it required?
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
