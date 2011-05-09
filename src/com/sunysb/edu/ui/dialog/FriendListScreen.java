package com.sunysb.edu.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

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
		
		transition = (Integer)this.getIntent().getExtras().get(StringUtil.TRANSITION);
		List<String> friendlist = new ArrayList<String>();

		switch (transition) {
		case StringUtil.CREATE:
			break;

		case StringUtil.EDIT:
			friendlist.addAll(util.getFriendsForUser(SimpleDbUtil.getCurrentUser()));
			break;

		case StringUtil.NOTIFY:
			friendlist.addAll(this.getIntent().getExtras()
					.getStringArrayList(StringUtil.FRIEND_INFO));
			break;

		case StringUtil.DELETE:
			break;
		}
		drawUI(friendlist);
	}

	private void drawUI(List<String> friendlist) {

		TableLayout table = (TableLayout) findViewById(R.id.friendTableList);
		table.removeAllViews();
		
		for (String namestr : friendlist) {

			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));

			TextView name = new TextView(this);
			String newStr = namestr.replace(StringUtil.FRIEND_INFO, "");
			name.setText(newStr);
			name.setTextColor(Color.YELLOW);
			tr.addView(name);
			
			tr.setTag(namestr);
			
			tr.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					String namestr = (String) v.getTag();
					Log.e("LBA", "TableRow clicked!");
					Intent intent = new Intent(FriendListScreen.this, NewFriendScreen.class);
					intent.putExtra(StringUtil.TRANSITION, transition);
					intent.putExtra(StringUtil.FRIEND_NAME, namestr);
					startActivity(intent);
				}
			});

			table.addView(tr, new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		}

		addFriendButton = (Button) findViewById(R.id.add_Friend);
		addFriendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FriendListScreen.this,NewFriendScreen.class);
				intent.putExtra(StringUtil.TRANSITION, StringUtil.CREATE);
				startActivity(intent);
			}
		});
	}
}
