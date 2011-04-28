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
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.sunysb.edu.R;
import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.util.StringUtil;

public class EditTask extends Activity implements OnTouchListener {

	private SimpleDbUtil util;
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

		transition = this.getIntent().getExtras().getInt(StringUtil.TRANSITION);
		List<String> taskids = new ArrayList<String>();

		switch (transition) {
		case StringUtil.CREATE:
			break;

		case StringUtil.EDIT:
			taskids.addAll(util.getTasksForUser(SimpleDbUtil.getCurrentUser()));
			break;

		case StringUtil.VIEW:
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

	// TODO When user selects a task open the edit view for that task
	// send the task id to open the edit view to get only that task from the db
	// and show it on UI
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Intent intent = new Intent(EditTask.this, TaskScreen.class);
		intent.getExtras().putInt(StringUtil.TRANSITION, transition);
		//TODO add task id in extras
		startActivity(intent);
		return false;
	}
}
