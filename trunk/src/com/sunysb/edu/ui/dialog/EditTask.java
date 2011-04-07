package com.sunysb.edu.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
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

public class EditTask extends Activity implements OnTouchListener{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.taskedit);

		//TODO get friendlist for a user and set text as friend name
        TableLayout table = (TableLayout)findViewById(R.id.edittask);
        for(int i = 0; i < 3; i++)
        {
        	  TableRow tr = new TableRow(this);
              tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
              TextView textview = new TextView(this);
              textview.setText("Hello");
              textview.setTextColor(Color.YELLOW);
              tr.addView(textview);
              table.addView(tr,new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        }
	}
	
	private List<String> getTasksForUser()
	{
		List<String> domain = new ArrayList<String>();
		try {
			SimpleDbUtil util = new SimpleDbUtil();
			util.getDomainNames();
			domain.remove(StringUtil.FRIEND_INFO);
			domain.remove(StringUtil.USER_INFO);
			domain.remove(StringUtil.USER_ID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return domain;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		//startActivity(new Intent(EditTask.this, Task.class));
		return false;
	}
}
