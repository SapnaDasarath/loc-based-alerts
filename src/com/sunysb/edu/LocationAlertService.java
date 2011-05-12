package com.sunysb.edu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.ui.dialog.TaskScreen;
import com.sunysb.edu.util.StringUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocationAlertService extends Service {

	private LocationManager locationManager;
	private LocationListener listner;

	public static int NOTIFICATION_ID = 1;

	private double latmin = 0;
	private double lngmin = 0;

	private double latmax = 0;
	private double lngmax = 0;

	public LocationAlertService() {

	}

	public void onCreate() {
		Log.e("LBA", "onHandleIntent() method called");

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);
		criteria.setCostAllowed(true);
		String locationContext = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) getSystemService(locationContext);
		String provider = locationManager.getBestProvider(criteria, true);
		listner = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				updatelocation(location);
			}

			@Override
			public void onProviderDisabled(String provider) {
				Log.d("onProviderDisabled", provider.toString());

			}

			@Override
			public void onProviderEnabled(String provider) {
				Log.d("onProviderEnabled", provider.toString());

			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				Log.d("Provider status changed", provider.toString());

			}
		};
		locationManager.requestLocationUpdates(provider, 10000, 100f, listner);
	}

	protected void updatelocation(Location location) {

		try {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();

			boolean ret = (latitude > latmin && latitude < latmax)
					&& (longitude > lngmin && longitude < lngmax);

			if (ret)
				return;

			SimpleDbUtil util = new SimpleDbUtil();
			String domain = SimpleDbUtil.getCurrentUser();

			String query1 = "select * from " + domain + " where "
					+ StringUtil.TASK_LAT + " > " + "'"
					+ Double.toString(latitude - 0.000000000000100) + "'";
			List<String> latitudeTasks = new ArrayList<String>();
			latitudeTasks.addAll(util.getItemNamesForQuery(query1));

			String query2 = "select * from " + domain + " where "
					+ StringUtil.TASK_LAT + " < " + "'"
					+ Double.toString(latitude + 0.000000000000100) + "'";
			latitudeTasks.addAll(util.getItemNamesForQuery(query2));

			String query3 = "select * from " + domain + " where "
					+ StringUtil.TASK_LONG + " > " + "'"
					+ Double.toString(longitude - 0.000000000000100) + "'";
			List<String> longitudeTasks = new ArrayList<String>();
			longitudeTasks.addAll(util.getItemNamesForQuery(query3));

			String query4 = "select * from " + domain + " where "
					+ StringUtil.TASK_LONG + " < " + "'"
					+ Double.toString(longitude + 0.000000000000100) + "'";
			longitudeTasks.addAll(util.getItemNamesForQuery(query4));

			List<String> taskids = new ArrayList<String>();
			taskids.addAll(latitudeTasks);
			taskids.retainAll(longitudeTasks);

			latmin = latitude - 0.000000000000100;
			lngmin = longitude - 0.00000000000010;

			latmax = latitude + 0.000000000000100;
			lngmax = longitude + 0.00000000000010;

			if (taskids.size() > 0) {
				showTaskAndUpdate(taskids);
			}

		} catch (Exception e) {
			Toast.makeText(this, "Not able to connect to server, Try again..",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		if (locationManager != null) {
			locationManager.removeUpdates(listner);
		}
	}

	public void showTaskAndUpdate(List<String> taskids) {
		Log.e("LBA", "showTaskAndUpdate() method");
		// show tasks to users if you are not the owner send mail to
		// owner that task complete
		String domain = SimpleDbUtil.getCurrentUser();
		try {
			SimpleDbUtil util = new SimpleDbUtil();
			for (String id : taskids) {
				try {
					HashMap<String, String> taskattributes = util
							.getAttributesForItem(domain, id);
					String taskstate = taskattributes
							.get(StringUtil.TASK_NOTIFY);
					if (taskstate == null)
						continue;
					if (taskstate.equals(StringUtil.TASK_NOTIFY_YES)) {
						continue;
					}

					String taskid = taskattributes.get(StringUtil.TASK_ID);
					String nameStrDb = taskattributes.get(StringUtil.TASK_NAME);
					String descriptionStrDb = taskattributes
							.get(StringUtil.TASK_DESCRIPTION);

					Intent intent = new Intent(this, TaskScreen.class);
					intent.putExtra(StringUtil.TRANSITION,
							StringUtil.NOTIFICATION);
					intent.putExtra(StringUtil.TASK_ID, taskid);
					intent.putExtra(StringUtil.TASK_STATUS,
							StringUtil.TASK_ACCEPTED);

					Notification notification = new Notification(
							R.drawable.icon, "Notification!!",
							System.currentTimeMillis());
					notification.setLatestEventInfo(LocationAlertService.this,
							nameStrDb, descriptionStrDb, PendingIntent
									.getActivity(this.getBaseContext(), 0,
											intent,
											PendingIntent.FLAG_NO_CREATE));
					notification.flags |= Notification.FLAG_SHOW_LIGHTS;
					notification.ledARGB = Color.CYAN;
					notification.ledOnMS = 500;
					notification.ledOffMS = 500;
					notification.vibrate = new long[] { 100, 200, 200, 200, 200, 200,
							1000, 200, 200, 200, 1000, 200 };

					NotificationManager myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					myNotificationManager.notify(NOTIFICATION_ID + 1,
							notification);

					HashMap<String, String> attrListToUpdate = new HashMap<String, String>();
					attrListToUpdate.put(StringUtil.TASK_NOTIFY,
							StringUtil.TASK_NOTIFY_NO);
					util.updateAttributesForItem(domain, id, attrListToUpdate);

				} catch (Exception e) {
					Toast.makeText(this, "Not able to connect to server",
							Toast.LENGTH_LONG).show();
				}
			}
		} catch (Exception e1) {
			Toast.makeText(this, "Not able to connect to server, Try again..",
					Toast.LENGTH_LONG).show();
		}
	}
}
