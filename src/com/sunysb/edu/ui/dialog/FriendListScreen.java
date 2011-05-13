package com.sunysb.edu.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.sunysb.edu.LocationBasedAlerts;
import com.sunysb.edu.R;
import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.util.StringUtil;

public class FriendListScreen extends Activity {

	private SimpleDbUtil util;
	private int transition;

	private Button addFriendButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.friendlist);

		try {
			util = new SimpleDbUtil();

		} catch (Exception e) {
			Log.e("LBA", "Unable to connect to server");
		}

		transition = (Integer) this.getIntent().getExtras()
				.get(StringUtil.TRANSITION);
		// this list contains the task ids to be displayed on ui
		// for edit put ids from db
		// for notify get it from user
		List<String> friendlist = new ArrayList<String>();

		switch (transition) {

		case StringUtil.EDIT:
			try {
				friendlist.addAll(util.getFriendsForUser(SimpleDbUtil
						.getCurrentUser()));
			} catch (Exception e) {
				Toast.makeText(this,
						"Unable to connect to server. Try again later..",
						Toast.LENGTH_LONG).show();
				return;
			}
			break;

		case StringUtil.NOTIFY:
			ArrayList<String> friendnames = getIntent().getExtras()
					.getStringArrayList(StringUtil.FRIEND_INFO);
			if (friendnames == null) {
				String frdquery = "select * from "
						+ SimpleDbUtil.getCurrentUser() + " where "
						+ StringUtil.FRIEND_STATUS + " = '"
						+ StringUtil.FRIEND_PENDING + "'";
				try {
					friendlist.addAll(util.getItemNamesForQuery(frdquery));
				} catch (Exception e) {
					Toast.makeText(this,
							"Unable to connect to server. Try again later..",
							Toast.LENGTH_LONG).show();
					return;
				}
			} else {
				friendlist.addAll(friendnames);
			}
			break;
		}
		drawUI(friendlist);
	}

	private void drawUI(List<String> friendlist) {

		TableLayout table = (TableLayout) findViewById(R.id.friendTableList);
		table.removeAllViews();

		for (String namestr : friendlist) {

			TableRow tr = new TableRow(this);
			tr.setTag(namestr);
			tr.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					String namestr = (String) v.getTag();
					Log.e("LBA", "TableRow clicked!");
					Intent intent = new Intent(FriendListScreen.this,
							NewFriendScreen.class);
					intent.putExtra(StringUtil.TRANSITION, transition);
					intent.putExtra(StringUtil.FRIEND_NAME, namestr);
					startActivity(intent);
				}
			});

			tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));

			TextView name = new TextView(this);
			String newStr = namestr.replace(StringUtil.FRIEND_INFO, "");
			name.setText(newStr);
			name.setTextColor(Color.YELLOW);
			tr.addView(name);

			table.addView(tr, new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		}

		addFriendButton = (Button) findViewById(R.id.add_Friend);
		addFriendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FriendListScreen.this,
						NewFriendScreen.class);
				intent.putExtra(StringUtil.TRANSITION, StringUtil.CREATE);
				startActivity(intent);
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
			
			editor.putString(StringUtil.USRNAME, "");
			editor.commit();
			
			startActivity(new Intent(FriendListScreen.this,
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
	
	public void onBackPressed() {
		startActivity(new Intent(FriendListScreen.this, UserOptionScreen.class));
		return;
	}
}
