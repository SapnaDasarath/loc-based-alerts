package com.sunysb.edu.ui.map;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.SearchManager;
import android.content.Intent;
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
import com.sunysb.edu.R;
import com.sunysb.edu.ui.dialog.TaskScreen;


public class Map extends MapActivity {

	private MapView mapView;

	private Button addTaskButton;
	private Button btnSearch;

	public void changeMap(String area) {

		mapView = (MapView) findViewById(R.id.mapView);
		MapController mc = mapView.getController();

		GeoPoint myLocation = null;

		double lat = 0;
		double lng = 0;
		try {

			Geocoder g = new Geocoder(this, Locale.getDefault());

			java.util.List<android.location.Address> result = g
					.getFromLocationName(area, 1);
			if (result.size() > 0) {

				Toast.makeText(
						Map.this,
						"country: "
								+ String.valueOf(result.get(0).getCountryName()),
						Toast.LENGTH_SHORT).show();
				lat = result.get(0).getLatitude();
				lng = result.get(0).getLongitude();
			} else {
				Toast.makeText(Map.this, "record not found", Toast.LENGTH_SHORT)
						.show();
				return;
			}
		} catch (IOException io) {
			Toast.makeText(Map.this, "Connection Error", Toast.LENGTH_SHORT)
					.show();
		}
		myLocation = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));

		mc.animateTo(myLocation);
		mc.setZoom(10);
		mapView.invalidate();
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
				startActivity(new Intent(Map.this, TaskScreen.class));
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
			// Use this String query to fire a HTTP map loc request. Thats the
			// action.
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

	public void setLocation(MapView mapView, int latitude, int longitude) {
		MapController mc = mapView.getController();
		GeoPoint p = new GeoPoint(latitude, longitude);
		mc.animateTo(p);
		mc.setZoom(17);
		System.out.print("Recieved click");
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
			Log.e("LBA", "case R.id.search Calling onSearchRequested()");
			onSearchRequested();
			return true;
		default:
			Log.e("LBA", "Inside onOptionsItemSelected() - Returning false");
			return false;
		}
	}
}
