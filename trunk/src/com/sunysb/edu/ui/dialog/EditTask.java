package com.sunysb.edu.ui.dialog;

import java.util.ArrayList;
import java.util.Arrays;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.taskedit);
		
		TableLayout table = (TableLayout) findViewById(R.id.edittask);
		List<String> taskids = getTasksForUser();
		try {
			SimpleDbUtil util = new SimpleDbUtil();
			for(String id : taskids)
			{
				HashMap<String, String> taskattributes = util.getAttributesForItem(SimpleDbUtil.getCurrentUser(), id);
				TableRow tr = new TableRow(this);
				tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
				
				TextView name = new TextView(this);
				name.setText(taskattributes.get(StringUtil.TASK_NAME));
				name.setTextColor(Color.YELLOW);
				tr.addView(name);
				
				TextView prior = new TextView(this);
				prior.setText(taskattributes.get(StringUtil.TASK_NAME));
				prior.setTextColor(Color.YELLOW);
				tr.addView(prior);
				
				table.addView(tr, new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			}
		} catch (Exception e) {
			Log.e("LBA", "Unable to connect to server");
		}
	}

	private List<String> getTasksForUser() {
		List<String> domain = new ArrayList<String>();
		try {
			SimpleDbUtil util = new SimpleDbUtil();
			String[] items = util.getItemNamesForDomain(util.getCurrentUser());
			if (items != null) {
				List<String> taskidlist = Arrays.asList(items);
				if (taskidlist != null && taskidlist.size() > 0) {
					taskidlist.remove(StringUtil.FRIEND_INFO);
					taskidlist.remove(StringUtil.USER_INFO);
				}
				
				if (taskidlist != null && taskidlist.size() > 0) 
				{
					domain.addAll(taskidlist);
				}
			}

		} catch (Exception e) {
			Log.e("LBA", "Unable to connect to server");
		}
		return domain;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// startActivity(new Intent(EditTask.this, Task.class));
		return false;
	}
}
