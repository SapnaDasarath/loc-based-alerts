package com.sunysb.edu.ui.dialog;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sunysb.edu.LocationBasedAlerts;
import com.sunysb.edu.R;
import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.ui.map.Map;
import com.sunysb.edu.util.StringUtil;

public class UserOptionScreen extends Activity {

	private Button addTaskButton;
	private Button editTaskButton;
	private Button notificationButton;
	private Button organizeFriendsButton;
	private Button editProfileButton;

	private SimpleDbUtil util;

	final ArrayList<String> tasknames = new ArrayList<String>();
	final ArrayList<String> friendnames = new ArrayList<String>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.useraction);

		addTaskButton = (Button) findViewById(R.id.add_Task_button);
		editTaskButton = (Button) findViewById(R.id.edit_Task_button);
		notificationButton = (Button) findViewById(R.id.notifications_button);
		organizeFriendsButton = (Button) findViewById(R.id.organize_Friends_button);
		editProfileButton = (Button) findViewById(R.id.edit_Profile_button);

		try {
			util = new SimpleDbUtil();
			String taskquery = "select * from " + SimpleDbUtil.getCurrentUser()
					+ " where " + StringUtil.TASK_STATUS + " = '"
					+ StringUtil.TASK_PENDING + "'";
			tasknames.addAll(util.getItemNamesForQuery(taskquery));

			String frdquery = "select * from " + SimpleDbUtil.getCurrentUser()
					+ " where " + StringUtil.FRIEND_STATUS + " = '"
					+ StringUtil.FRIEND_PENDING + "'";
			friendnames.addAll(util.getItemNamesForQuery(frdquery));

			int size = tasknames.size() + friendnames.size();
			notificationButton.setText(notificationButton.getText() + " ("
					+ size + ")");

		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_SHORT).show();
			return;
		}

		addTaskButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(UserOptionScreen.this, Map.class));
			}
		});

		editTaskButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserOptionScreen.this,
						EditTask.class);
				intent.putExtra(StringUtil.TRANSITION, StringUtil.EDIT);
				startActivity(intent);
			}
		});

		notificationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserOptionScreen.this,
						NotificationScreen.class);
				intent.putExtra(StringUtil.TRANSITION, StringUtil.NOTIFY);
				intent.putStringArrayListExtra(StringUtil.TASK_INFO, tasknames);
				intent.putStringArrayListExtra(StringUtil.FRIEND_INFO,
						friendnames);
				startActivity(intent);
			}
		});

		organizeFriendsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserOptionScreen.this,
						FriendListScreen.class);
				intent.putExtra(StringUtil.TRANSITION, StringUtil.EDIT);
				startActivity(intent);
			}
		});

		editProfileButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(UserOptionScreen.this,
						UserProfileScreen.class));
			}
		});
	}

	private void CreateMenu(Menu menu) {
		menu.add(0, 0, 0, "Sign out");
	}

	private boolean MenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			SharedPreferences app_preferences = PreferenceManager
					.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = app_preferences.edit();
			editor.putBoolean(StringUtil.TASK_INFO, false);
			editor.commit();
			startActivity(new Intent(UserOptionScreen.this,
					LocationBasedAlerts.class));
			return true;
		}
		return false;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		CreateMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return MenuChoice(item);
	}
}
