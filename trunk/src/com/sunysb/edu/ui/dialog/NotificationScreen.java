package com.sunysb.edu.ui.dialog;

import java.util.ArrayList;

import com.sunysb.edu.R;
import com.sunysb.edu.util.StringUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NotificationScreen extends Activity {

	private Button taskButton;
	private Button friendButton;

	ArrayList<String> tasknames;
	ArrayList<String> friendnames;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.notification);

		tasknames = getIntent().getExtras().getStringArrayList(
				StringUtil.TASK_INFO);
		friendnames = getIntent().getExtras().getStringArrayList(
				StringUtil.FRIEND_INFO);

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
