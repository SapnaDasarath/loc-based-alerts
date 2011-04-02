package com.sunysb.edu;

import com.sunysb.edu.ui.dialog.Task;
import com.sunysb.edu.ui.map.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

//source : http://mobiforge.com/developing/story/using-google-maps-android


public class LocationBasedAlerts extends Activity{
	
	private Button addTaskButton;
	private Button editTaskButton;
	private Button addFriendsButton;
	private Button editProfileButton;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		addTaskButton = (Button) findViewById(R.id.add_Task_button);
		editTaskButton = (Button) findViewById(R.id.edit_Task_button);
		addFriendsButton = (Button) findViewById(R.id.organize_Friends_button);
		editProfileButton = (Button) findViewById(R.id.edit_Profile_button);
		
        addTaskButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity( new Intent(LocationBasedAlerts.this, Map.class));
			}
		});
		
        editTaskButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LocationBasedAlerts.this, Task.class));
			}
		});
		
        addFriendsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LocationBasedAlerts.this, Map.class));
			}
		});
        
        editProfileButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LocationBasedAlerts.this, Map.class));
			}
		});
	}
	
}