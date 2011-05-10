package com.sunysb.edu;

import java.util.List;

import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.util.StringUtil;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class LocationAlertService extends IntentService implements LocationListener{

	private LocationManager myManager;
	
	public LocationAlertService() {
		super("LocationAlertService");
	}

	public void onCreate(){
		myManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Log.e("LBA", "LocationAlertService created");
		super.onCreate();
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
	    return super.onStartCommand(intent,flags,startId);
	}
	
	
	@Override
	protected void onHandleIntent(Intent arg0) { //arg0?
		// TODO Auto-generated method stub
		Log.e("LBA", "onHandleIntent() method called");
		startListening();
	}
	
	
	private void startListening() {
		// 50 m
		Log.e("LBA", "startListening() method: Location alert listening");
		myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,50, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		// query db to see if this location is available
		Log.e("LBA","onLocationChanged() method called");
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

			List<String> taskids = util.getItemNamesForQuery(matchingQuery);
			showTaskAndUpdate(taskids);

		} catch (Exception e) {
			Toast.makeText(this, "Not able to connect to server, Try again..",
					Toast.LENGTH_LONG).show();
		}	
	}
	
	public void showTaskAndUpdate(List<String> taskids)
	{
		//show tasks to users
		//if you are not the owner send mail to owner that task complete
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
