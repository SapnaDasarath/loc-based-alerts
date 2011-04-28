package com.sunysb.edu.ui.dialog;

import java.util.ArrayList;

import com.sunysb.edu.R;
import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.util.StringUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class NotificationScreen extends Activity {

	SimpleDbUtil util;

	private Button taskButton;
	private Button friendButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.friendlist);

		try {
			util = new SimpleDbUtil();

		} catch (Exception e) {
			Log.e("LBA", "Unable to connect to server");
		}

		taskButton = (Button) findViewById(R.id.ok_user_button);
		friendButton = (Button) findViewById(R.id.close_user_button);

		String taskquery = "select * from " + SimpleDbUtil.getCurrentUser()
				+ " where " + StringUtil.TASK_STATUS + "= '"
				+ StringUtil.TASK_PENDING + "'";
		final ArrayList<String> tasknames = (ArrayList<String>) util
				.getItemNamesForQuery(taskquery);

		taskButton.setText(friendButton.getText() + "(" + tasknames.size()
				+ ")");

		taskButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// open table with tasks shown
				Intent intent = new Intent(NotificationScreen.this,
						EditTask.class);
				intent.putExtra(StringUtil.TRANSITION, StringUtil.NOTIFY);
				intent.putStringArrayListExtra(StringUtil.TASK_INFO, tasknames);
				startActivity(intent);
			}
		});

		String frdquery = "select * from " + SimpleDbUtil.getCurrentUser()
				+ " where " + StringUtil.FRIEND_STATUS + "= '"
				+ StringUtil.FRIEND_PENDING + "'";
		final ArrayList<String> friendnames = (ArrayList<String>) util
				.getItemNamesForQuery(frdquery);

		friendButton.setText(friendButton.getText() + "(" + friendnames.size()
				+ ")");

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
	}
}
