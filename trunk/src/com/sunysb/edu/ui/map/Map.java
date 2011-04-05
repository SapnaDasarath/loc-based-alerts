package com.sunysb.edu.ui.map;

import java.util.List;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.MapView.LayoutParams;
import com.sunysb.edu.LocationBasedAlerts;
import com.sunysb.edu.R;
import com.sunysb.edu.ui.dialog.Task;
import com.sunysb.edu.ui.dialog.UserOptionScreen;


public class Map extends MapActivity{

	private MapView mapView;

	private Button addTaskButton;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadmap);
		//startSearch("test search string", false, SEARCHABLE_ACTIVITY, null, false);
		handleIntent(getIntent());

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
		
		addTaskButton = (Button) findViewById(R.id.add_Task) ;
		addTaskButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Log.e( "LBA", "Add Task dialog launched" );
					startActivity(new Intent(Map.this, Task.class));
				}
			});
	}
	
	@Override
	public void onNewIntent(Intent intent) {
	    setIntent(intent);
	    handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	     // doMySearch(query);
	    //Use this String query to fire a HTTP map loc request. Thats the action.
	    }
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	public boolean onSearchRequested (){
		/*Bundle appDataBundle = null;
		final String queryAppDataString = mQueryAppData.getText().toString();
		if (queryAppDataString != null) {
		appDataBundle = new Bundle();
		appDataBundle.putString("demo_key", queryAppDataString);
		}

		// Now call the Activity member function that invokes the
		//Search Manager UI.
		startSearch("Stony Brook", true, appDataBundle, true);

		// Returning true indicates that we did launch the search,instead of blocking it. */
		return true;
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
