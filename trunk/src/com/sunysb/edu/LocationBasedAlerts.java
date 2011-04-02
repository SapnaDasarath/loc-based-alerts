package com.sunysb.edu;

import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.ui.dialog.UserOptionScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class LocationBasedAlerts extends Activity{
	
	private String userName = "sdasarath";
	private SimpleDbUtil dbAccess = null;
	
	private Button okButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.e( "LBA", "Loading main screen" );        
		
		okButton = (Button) findViewById(R.id.ok_main_button);
		
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e( "LBA", "Creating simple db" );      
				dbAccess = new SimpleDbUtil(userName);
				
				//should be done only once..
				//first time when the user account is created.
				dbAccess.createDomain(userName);
				dbAccess.createItem(userName, SimpleDbUtil.USER_INFO);
				dbAccess.createItem(userName, SimpleDbUtil.TASK_INFO);
				dbAccess.createItem(userName, SimpleDbUtil.FRIEND_INFO);
				Log.e( "LBA", "Simple DB Created" );      
				//the assumption here is a domain for the user name is created
				//when a user creates an account for the first time
				startActivity(new Intent(LocationBasedAlerts.this, UserOptionScreen.class));
			}
		});
	}
}