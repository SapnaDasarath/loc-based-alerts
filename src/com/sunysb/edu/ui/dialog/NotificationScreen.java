package com.sunysb.edu.ui.dialog;

import java.util.ArrayList;

import com.sunysb.edu.R;
import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.util.StringUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NotificationScreen extends Activity {

	private SimpleDbUtil util;

	private Button taskButton;
	private Button friendButton;

	private ArrayList<String> tasknames;
	private ArrayList<String> friendnames;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.notification);
		try {
			util = new SimpleDbUtil();
			tasknames = getIntent().getExtras().getStringArrayList(
					StringUtil.TASK_INFO);
			if (tasknames == null) {
				String taskquery = "select * from "
						+ SimpleDbUtil.getCurrentUser() + " where "
						+ StringUtil.TASK_STATUS + " = '"
						+ StringUtil.TASK_PENDING + "'";
				tasknames.addAll(util.getItemNamesForQuery(taskquery));
			}
			friendnames = getIntent().getExtras().getStringArrayList(
					StringUtil.FRIEND_INFO);
			if (friendnames == null) {
				String frdquery = "select * from "
						+ SimpleDbUtil.getCurrentUser() + " where "
						+ StringUtil.FRIEND_STATUS + " = '"
						+ StringUtil.FRIEND_PENDING + "'";
				friendnames.addAll(util.getItemNamesForQuery(frdquery));
			}
		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_SHORT).show();
			return;
		}

		taskButton = (Button) findViewById(R.id.newtask_button);
		friendButton = (Button) findViewById(R.id.newfriend_button);
		try {

			taskButton.setText(taskButton.getText() + " (" + tasknames.size()
					+ ")");

			taskButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// open table with tasks shown
					Intent intent = new Intent(NotificationScreen.this,
							EditTask.class);
					intent.putExtra(StringUtil.TRANSITION, StringUtil.NOTIFY);
					intent.putStringArrayListExtra(StringUtil.TASK_INFO,
							tasknames);
					startActivity(intent);
				}
			});

			friendButton.setText(friendButton.getText() + " ("
					+ friendnames.size() + ")");

			friendButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// open table with friends shown
					Intent intent = new Intent(NotificationScreen.this,
							FriendListScreen.class);
					intent.putExtra(StringUtil.TRANSITION, StringUtil.NOTIFY);
					intent.putStringArrayListExtra(StringUtil.FRIEND_INFO,
							friendnames);
					startActivity(intent);
				}
			});

		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_SHORT).show();
			return;
		}
	}
}
