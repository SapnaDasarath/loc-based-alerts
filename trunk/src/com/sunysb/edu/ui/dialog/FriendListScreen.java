package com.sunysb.edu.ui.dialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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

	private Button addFriendButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.friendlist);

		TableLayout table = (TableLayout) findViewById(R.id.friendTableList);
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
		try {
			SimpleDbUtil util = new SimpleDbUtil();
			HashMap<String, String> friendmap = util.getAttributesForItem(
					SimpleDbUtil.getCurrentUser(), StringUtil.FRIEND_INFO);
			if (friendmap != null) {
				Set<String> friendnames = friendmap.keySet();
				if (friendnames != null && friendnames.size() > 0) {
					friends.addAll(friendnames);
					Collections.sort(friends);
				}
			}

		} catch (Exception e) {
			Log.e("LBA", "Unable to connect to server");
		}

		return friends;
	}
}
