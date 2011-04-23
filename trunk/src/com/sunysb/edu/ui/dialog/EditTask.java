package com.sunysb.edu.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.sunysb.edu.R;
import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.util.StringUtil;

public class EditTask extends Activity implements OnTouchListener {

	SimpleDbUtil util;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.taskedit);

		try {
			util = new SimpleDbUtil();
		} catch (Exception e) {
			Log.e("LBA", "Unable to connect to server");
		}
		drawUI();
	}

	private void drawUI() {
		TableLayout table = (TableLayout) findViewById(R.id.edittask);
		table.removeAllViews();
		List<String> taskids = getTasksForUser();
		for (String id : taskids) {
			HashMap<String, String> taskattributes = util.getAttributesForItem(
					SimpleDbUtil.getCurrentUser(), id);
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));

			TextView name = new TextView(this);
			name.setText(taskattributes.get(StringUtil.TASK_NAME) + "--");
			name.setTextColor(Color.YELLOW);
			tr.addView(name);

			TextView prior = new TextView(this);
			prior.setText(taskattributes.get(StringUtil.TASK_PRIORITY));
			prior.setTextColor(Color.YELLOW);
			tr.addView(prior);

			table.addView(tr, new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		}
	}

	private List<String> getTasksForUser() {
		List<String> domain = new ArrayList<String>();
		util.getTasksForUser(SimpleDbUtil.getCurrentUser());
		return domain;
	}

	// TODO When user selects a task open the edit view for that task
	// send the task id to open the edit view to get only that task from the db
	// and show it on UI
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// startActivity(new Intent(EditTask.this, Task.class));
		return false;
	}

	// If user selects delete task remove it from UI and DB and if the task is a
	// shared task
	// remove it from the person who has the task too
	public boolean removeTask(String taskId) {
		List<String> tasks = util.getTaskAcceptedFriends(taskId);
		if (tasks.size() > 0) {
			// for each user name send the task id to be deleted.
		}
		util.deleteItem(SimpleDbUtil.getCurrentUser(), taskId);
		
		drawUI();
		return true;
	}
}
