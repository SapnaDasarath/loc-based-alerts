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

	SimpleDbUtil util;
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
		drawUI();
	}

	private void drawUI() {

		TableLayout table = (TableLayout) findViewById(R.id.friendTableList);
		table.removeAllViews();
		
		List<String> friendlist = getFriendsForUser();
		for (String namestr : friendlist) {

			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));

			TextView name = new TextView(this);
			name.setText(namestr);
			name.setTextColor(Color.YELLOW);
			tr.addView(name);

			table.addView(tr, new TableLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		}

		addFriendButton = (Button) findViewById(R.id.add_Friend);
		addFriendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(FriendListScreen.this,
						NewFriendScreen.class));
			}
		});

		// TODO add remove friend request
	}

	private List<String> getFriendsForUser() {
		List<String> friends = new ArrayList<String>();
		friends = util.getFriendsForUser(SimpleDbUtil.getCurrentUser());
		return friends;
	}

	// Remove friend from your list and the other user list also
	// remove all shared tasks.
	private void removeFriend(String name) {
		//TODO
		//get all shared tasks between these two ppl.
		//there must be some query way of doing this.
		
		//go tru this guys tasks list to see if there is a task with owner as friend to remove
		
		//go tru friend to remove's task list to see if there is a task with owner as current user
		//remove both.
		
		//send alert to remove this user from the friend list.
		
		//remove from current user
		util.deleteItem(SimpleDbUtil.getCurrentUser(), StringUtil.FRIEND_INFO+name);
		drawUI();
	}
}
