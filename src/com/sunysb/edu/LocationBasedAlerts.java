package com.sunysb.edu;

import com.sunysb.edu.ui.map.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

//source : http://mobiforge.com/developing/story/using-google-maps-android


public class LocationBasedAlerts extends Activity{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loadmap);
	/*	
		Button next = (Button) findViewById(R.id.button);
		next.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				Intent myIntent = new Intent(view.getContext(), Map.class);
                startActivityForResult(myIntent, 0);
			}
		}); */
		
		/* Button next = (Button) findViewById(R.id.button);
	        next.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	                Intent myIntent = new Intent(view.getContext(), Activity2.class);
	                startActivityForResult(myIntent, 0);
	            }

	        }); */
	}
	
}