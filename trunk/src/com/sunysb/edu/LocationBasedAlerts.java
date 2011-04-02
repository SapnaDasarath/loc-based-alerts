package com.sunysb.edu;

import com.sunysb.edu.ui.dialog.UserOptionScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class LocationBasedAlerts extends Activity{
	
	private Button okButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		okButton = (Button) findViewById(R.id.ok_main_button);
		
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LocationBasedAlerts.this, UserOptionScreen.class));
			}
		});
	}	
}