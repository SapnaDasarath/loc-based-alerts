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
import android.widget.Toast;

public class NotificationScreen extends Activity {

	SimpleDbUtil util;

	private Button taskButton;
	private Button friendButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.notification);

		try {
			util = new SimpleDbUtil();

		} catch (Exception e) {
			Log.e("LBA", "Unable to connect to server");
		}

		taskButton = (Button) findViewById(R.id.newtask_button);
		friendButton = (Button) findViewById(R.id.newfriend_button);

		String taskquery = "select * from " + SimpleDbUtil.getCurrentUser()
				+ " where " + StringUtil.TASK_STATUS + " = '"
				+ StringUtil.TASK_PENDING + "'";
		System.out.println(taskquery);
		
		try {
			final ArrayList<String> tasknames = new ArrayList<String> ();
			tasknames.addAll( util.getItemNamesForQuery(taskquery));

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

			String frdquery = "select * from " + SimpleDbUtil.getCurrentUser()
					+ " where " + StringUtil.FRIEND_STATUS + " = '"
					+ StringUtil.FRIEND_PENDING + "'";
			final ArrayList<String> friendnames = (ArrayList<String>) util
					.getItemNamesForQuery(frdquery);

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
