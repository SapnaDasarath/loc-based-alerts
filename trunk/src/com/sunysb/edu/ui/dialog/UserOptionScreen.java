package com.sunysb.edu.ui.dialog;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sunysb.edu.LocationBasedAlerts;
import com.sunysb.edu.R;
import com.sunysb.edu.ui.map.Map;

public class UserOptionScreen extends Activity{
	
	private Button addTaskButton;
	private Button editTaskButton;
	private Button addFriendsButton;
	private Button editProfileButton;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.useraction);
		
		addTaskButton = (Button) findViewById(R.id.add_Task_button);
		editTaskButton = (Button) findViewById(R.id.edit_Task_button);
		addFriendsButton = (Button) findViewById(R.id.organize_Friends_button);
		editProfileButton = (Button) findViewById(R.id.edit_Profile_button);
		
        addTaskButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity( new Intent(UserOptionScreen.this, Map.class));
			}
		});
		
        editTaskButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(UserOptionScreen.this, EditTask.class));
			}
		});
		
        addFriendsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(UserOptionScreen.this, Map.class));
			}
		});
        
        editProfileButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(UserOptionScreen.this, Map.class));
			}
		});
	}
	
}
