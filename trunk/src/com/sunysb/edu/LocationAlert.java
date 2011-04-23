package com.sunysb.edu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.util.StringUtil;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class LocationAlert extends Activity implements LocationListener {
	private LocationManager myManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	}

	@Override
	protected void onDestroy() {
		stopListening();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		stopListening();
		super.onPause();
	}

	@Override
	protected void onResume() {
		startListening();
		super.onResume();
	}

	private void startListening() {
		// 50 m
		myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,
				50, this);
	}

	private void stopListening() {
		if (myManager != null)
			myManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		// query db to see if this location is available
		SimpleDbUtil util = null;
		try {
			util = new SimpleDbUtil();
			String domain = SimpleDbUtil.getCurrentUser();
			String latituteUpper = Double.toString(latitude + 100);
			String latituteLower = Double.toString(latitude - 100);

			String longitudeUpper = Double.toString(longitude + 100);
			String longitudeLower = Double.toString(longitude - 100);

			String query = "select * from " + domain + " where " + "("
					+ StringUtil.TASK_LAT + " between " + "`" + latituteUpper
					+ "`" + " and " + "`" + latituteLower + "`" + ")" + " and "
					+ "(" + StringUtil.TASK_LONG + " between " + "`"
					+ longitudeUpper + "`" + " and " + "`" + longitudeLower
					+ "`" + ")";

			String matchingQuery = "select * from " + domain + " where "
					+ StringUtil.TASK_LAT + " is " + "`"
					+ Double.toString(latitude) + "`" + " and "
					+ StringUtil.TASK_LONG + " is " + "`"
					+ Double.toString(longitude) + "`";

			String[] matchingitems = util.getItemNamesForQuery(matchingQuery);
			List<String> taskids = new ArrayList<String>();
			Collections.addAll(taskids, matchingitems); 
			showTaskAndUpdate(taskids);

		} catch (Exception e) {
			Toast.makeText(this, "Not able to connect to server, Try again..",
					Toast.LENGTH_LONG).show();
		}
	}
	
	public void showTaskAndUpdate(List<String> taskids)
	{
		//show tasks to users
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}
