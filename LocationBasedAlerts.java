package com.sunysb.edu;

import android.app.Activity;
import android.os.Bundle;

//source : http://mobiforge.com/developing/story/using-google-maps-android


public class LocationBasedAlerts extends Activity{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadmap);
	}
}