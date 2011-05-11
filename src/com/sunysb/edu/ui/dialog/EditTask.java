package com.sunysb.edu.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.sunysb.edu.LocationBasedAlerts;
import com.sunysb.edu.R;
import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.util.StringUtil;

public class EditTask extends Activity {

	private SimpleDbUtil util;
	private TextView name;
	private int transition;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.taskedit);

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
		List<String> taskids = new ArrayList<String>();

		// This is only to set the UI labels
		switch (transition) {
		case StringUtil.EDIT:
			try {
				taskids.addAll(util.getTasksForUser(SimpleDbUtil
						.getCurrentUser()));
			} catch (Exception e) {
				Toast.makeText(this,
						"Unable to connect to server. Try again later..",
						Toast.LENGTH_SHORT).show();
				return;
			}
			break;

		case StringUtil.NOTIFY:
			taskids.addAll(this.getIntent().getExtras()
					.getStringArrayList(StringUtil.TASK_INFO));
			break;
		}
		drawUI(taskids);
	}

	private void drawUI(List<String> taskids) {
		TableLayout table = (TableLayout) findViewById(R.id.edittask);
		table.removeAllViews();

		for (String id : taskids) {
			HashMap<String, String> taskattributes;
			try {
				taskattributes = util.getAttributesForItem(
						SimpleDbUtil.getCurrentUser(), id);

				TableRow tr = new TableRow(this);
				tr.setTag(id);
				tr.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						String id = (String) v.getTag();
						Log.e("LBA", "TableRow clicked!");
						Intent intent = new Intent(EditTask.this,
								TaskScreen.class);
						intent.putExtra(StringUtil.TRANSITION, transition);
						intent.putExtra(StringUtil.TASK_ID, id);
						startActivity(intent);
					}
				});
				tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));

				name = new TextView(this);
				name.setText(taskattributes.get(StringUtil.TASK_NAME) + "--");
				name.setTextColor(Color.YELLOW);
				tr.addView(name);
				tr.setClickable(true);

				TextView prior = new TextView(this);
				prior.setText(taskattributes.get(StringUtil.TASK_PRIORITY));
				prior.setTextColor(Color.YELLOW);
				tr.addView(prior);

				table.addView(tr, new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			} catch (Exception e) {
				Toast.makeText(this,
						"Unable to connect to server. Try again later..",
						Toast.LENGTH_SHORT).show();
				return;
			}
		}
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
			
			startActivity(new Intent(EditTask.this, LocationBasedAlerts.class));
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
		startActivity(new Intent(EditTask.this, UserOptionScreen.class));
		return;
	}
}
