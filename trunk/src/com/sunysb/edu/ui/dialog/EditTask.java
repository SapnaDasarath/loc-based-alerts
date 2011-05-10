package com.sunysb.edu.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.sunysb.edu.R;
import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.util.StringUtil;

public class EditTask extends Activity implements OnTouchListener {

	SimpleDbUtil util;
	TextView name = null;
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
		List<String> taskids = new ArrayList<String>();

		switch (transition) {
		case StringUtil.CREATE:
			break;

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

		case StringUtil.DELETE:
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

				tr.setTag(id);

				table.addView(tr, new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

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
			} catch (Exception e) {
				Toast.makeText(this,
						"Unable to connect to server. Try again later..",
						Toast.LENGTH_SHORT).show();
				return;
			}
		}
	}

	/*
	 * @Override public boolean onTouchEvent(MotionEvent event){
	 * System.out.println("LBA: onTouchEvent method event is: " +
	 * event.toString()); return false; }
	 */

	// TODO When user selects a task open the edit view for that task
	// send the task id to open the edit view to get only that task from the db
	// and show it on UI
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		System.out.println("LBA: onTouch method View value is: " + v.getId());
		// startActivity(new Intent(EditTask.this, TaskScreen.class));
		Intent intent = new Intent(EditTask.this, TaskScreen.class);
		intent.putExtra(StringUtil.TRANSITION, transition);
		// TODO add task id in extras
		startActivity(intent);
		return false;
	}

	// If user selects delete task remove it from UI and DB and if the task is a
	// shared task
	// remove it from the person who has the task too
	/*
	 * public boolean removeTask(String taskId) { List<String> tasks =
	 * util.getTaskAcceptedFriends(taskId); if (tasks.size() > 0) { // for each
	 * user name send the task id to be deleted. }
	 * util.deleteItem(SimpleDbUtil.getCurrentUser(), taskId);
	 * 
	 * drawUI(); return true; }
	 */
}
