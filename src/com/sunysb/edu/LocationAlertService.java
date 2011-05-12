package com.sunysb.edu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.util.StringUtil;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocationAlertService extends Service{

	private LocationManager locationManager;
	LocationListener listner;
	
	NotificationManager myNotificationManager;
    private static final int NOTIFICATION_ID = 1;
    
	private double lat = 0;
	private double lng = 0;
	
	public LocationAlertService() {
		//super("LocationAlertService");
	}
/*
	public void onCreate(){
		//super.onCreate();
	}*/
	/*	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
	    return super.onStartCommand(intent,flags,startId);
	}*/
	
	//@Override
	//protected void onHandleIntent(Intent arg0) { 
	public void onCreate(){
		Log.e("LBA", "onHandleIntent() method called");
		//myManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		//myManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		
		Criteria criteria = new Criteria(); 
        criteria.setAccuracy(Criteria.ACCURACY_FINE); 
        criteria.setPowerRequirement(Criteria.POWER_LOW); 
        criteria.setAltitudeRequired(false); 
        criteria.setBearingRequired(false); 
        criteria.setSpeedRequired(false); 
        criteria.setCostAllowed(true); 
        String locationContext = Context.LOCATION_SERVICE; 
        locationManager = (LocationManager) getSystemService (locationContext); 
        String provider = locationManager.getBestProvider(criteria, true); 
        //Location location = locationManager.getLastKnownLocation(provider); 
        listner = new LocationListener(){
        	@Override
        	public void onLocationChanged(Location location) {
        		
        		Log.e("onLocationChanged","update now"); 
        		double latitude = location.getLatitude();
        		double longitude = location.getLongitude();
        		// query db to see if this location is available
        		
        		Log.e("LBA","onLocationChanged() method called");
        		
        		List<String> taskids = new ArrayList();
        		
        		//if(lat == latitude && lng == longitude)
        		SimpleDbUtil util = null;
        		try {
        			
        			if(lat != latitude || lng != longitude){ // is this correct? longitude doesn't change often
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

        				taskids = util.getItemNamesForQuery(matchingQuery);
        				lat = latitude;
        				lng = longitude;
        			}
        			
        			if(taskids.size() > 0){
        				showTaskAndUpdate(taskids);
        			}

        		} catch (Exception e) {
        			//Toast.makeText(this, "Not able to connect to server, Try again..", Toast.LENGTH_LONG).show();
        		}	
        	}
        	
        	public void showTaskAndUpdate(List<String> taskids)
        	{
        		Log.e("LBA", "showTaskAndUpdate() method");
        		//show tasks to users if you are not the owner send mail to owner that task complete
        		String domain = SimpleDbUtil.getCurrentUser();
        		try {
        			SimpleDbUtil util = new SimpleDbUtil();
        			for (String id : taskids){
        				HashMap<String, String> taskattributes;
        				try{
        					taskattributes = util.getAttributesForItem(domain, id);
        					
        					myNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        					
        					String nameStrDb = taskattributes.get(StringUtil.TASK_NAME);
        					String descriptionStrDb = taskattributes.get(StringUtil.TASK_DESCRIPTION);
        					//String priorityStrDb = taskattributes.get(StringUtil.TASK_PRIORITY);
        					
        					CharSequence NotificationTicket = "LBA";
        					CharSequence NotificationTitle = nameStrDb;
        					CharSequence NotificationContent = descriptionStrDb;
        					long when = System.currentTimeMillis();
        					
        					Notification notification = new Notification(R.drawable.notification, NotificationTicket, when);
        					
        					Context context = getApplicationContext();
        					
        					//Intent notificationIntent = new Intent(this, LocationAlertService.class);
        					//PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        					
        					//notification.setLatestEventInfo(context, NotificationTitle, NotificationContent, contentIntent);
        					//myNotificationManager.notify(NOTIFICATION_ID, notification);  
        				}catch(Exception e){
        					e.printStackTrace();
        					//Toast.makeText(this, "Not able to connect to server", Toast.LENGTH_LONG).show();
        				}
        			}
        		} catch (Exception e1) {
        			// TODO Auto-generated catch block
        			//Toast.makeText(this, "Not able to connect to server, Try again..", Toast.LENGTH_LONG).show();
        			e1.printStackTrace();
        		}
        	}

        	@Override
        	public void onProviderDisabled(String provider) {
        		 Log.d("onProviderDisabled",provider.toString()); 
        		
        	}

        	@Override
        	public void onProviderEnabled(String provider) {
        		Log.d("onProviderEnabled",provider.toString()); 
        		
        	}

        	@Override
        	public void onStatusChanged(String provider, int status, Bundle extras) {
        		Log.d("Provider status changed", provider.toString()); 
        		
        	}
        };
        locationManager.requestLocationUpdates(provider,0 ,0f,listner); 
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy(){
		if(locationManager != null){
			locationManager.removeUpdates(listner);
		}
	}
	
}
