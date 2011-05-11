package com.sunysb.edu.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sunysb.edu.LocationBasedAlerts;
import com.sunysb.edu.R;
import com.sunysb.edu.db.AWSEmail;
import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.util.StringUtil;

public class FriendScreen extends Activity {

	private SimpleDbUtil util;
	private int transition;
	private String taskId;

	private EditText nameEditText;
	private Button sendButton;
	private Button closeButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend);

		try {
			util = new SimpleDbUtil();
		} catch (Exception e) {
			Toast.makeText(this, "Not able to connect to server, Try again..",
					Toast.LENGTH_LONG).show();
		}
		transition = (Integer) this.getIntent().getExtras()
				.get(StringUtil.TRANSITION);
		taskId = (String) this.getIntent().getExtras().get(StringUtil.TASK_ID);

		nameEditText = (EditText) findViewById(R.id.frndname_EditText);
		sendButton = (Button) findViewById(R.id.frndsend_To_Friend_button);
		closeButton = (Button) findViewById(R.id.frndclose_Task_button);

		sendButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				sendtask();
			}
		});

		closeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				switch (transition) {
				case StringUtil.EDIT:
					Intent intent = new Intent(FriendScreen.this,
							TaskScreen.class);
					intent.putExtra(StringUtil.TRANSITION, transition);
					intent.putExtra(StringUtil.TASK_ID, taskId);
					startActivity(intent);
					break;
				case StringUtil.NOTIFY:
					Intent intent1 = new Intent(FriendScreen.this,
							NotificationScreen.class);
					intent1.putExtra(StringUtil.TRANSITION, transition);
					intent1.putExtra(StringUtil.TASK_ID, taskId);
					startActivity(intent1);
					break;
				}
			}
		});
	}

	private void sendtask() {

		if (nameEditText.getText() == null) {
			Toast.makeText(this, "Enter valid username", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		try {
			String username = nameEditText.getText().toString();
			if (!util.doesDomainExist(username)) {
				Toast.makeText(this, "Enter valid username", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			// is this user a confirmed friend
			List<String> allfriends = util.getFriendsForUser(SimpleDbUtil
					.getCurrentUser());
			if (!allfriends.contains(username)) {
				Toast.makeText(this, "User name not in friend list",
						Toast.LENGTH_SHORT).show();
				return;
			}

			String frndname = StringUtil.FRIEND_INFO + username;
			HashMap<String, String> attrs = util.getAttributesForItem(
					SimpleDbUtil.getCurrentUser(), frndname);
			String status = attrs.get(StringUtil.FRIEND_STATUS);
			if (!status.equals(StringUtil.FRIEND_CONFIRMED)) {
				Toast.makeText(this, "User not confirmed as friend",
						Toast.LENGTH_SHORT).show();
				return;
			}

		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_SHORT).show();
			return;
		}

		addTaskToFriend(nameEditText.getText().toString(), taskId);
	}

	private void addTaskToFriend(String friendname, String taskid) {
		String currentuser = SimpleDbUtil.getCurrentUser();
		String sendto = null;
		try {

			HashMap<String, String> oldattr = new HashMap<String, String>();
			oldattr.putAll(util.getAttributesForItem(currentuser, taskid));

			String existingFrndNames = oldattr
					.get(StringUtil.TASK_FRIENDS_NAMES);

			List<String> friendsList = new ArrayList<String>();
			if (existingFrndNames == null) {
				friendsList.add(friendname);
			} else {
				friendsList
						.addAll(util.getFriendsFromString(existingFrndNames));
				if (friendsList.contains(friendname)) {
					return;
				}
				friendsList.add(friendname);
			}

			String friendsstr = util.getStringFromList(friendsList);
			HashMap<String, String> attributes = new HashMap<String, String>();
			attributes.put(StringUtil.TASK_FRIENDS_NAMES, friendsstr);

			util.updateAttributesForItem(currentuser, taskid, attributes);

			HashMap<String, String> taskInfoMap = new HashMap<String, String>();

			taskInfoMap.put(StringUtil.TASK_NAME,
					oldattr.get(StringUtil.TASK_NAME));

			taskInfoMap.put(StringUtil.TASK_DESCRIPTION,
					oldattr.get(StringUtil.TASK_DESCRIPTION));

			taskInfoMap.put(StringUtil.TASK_PRIORITY,
					oldattr.get(StringUtil.TASK_PRIORITY));

			taskInfoMap.put(StringUtil.TASK_OWNER,
					SimpleDbUtil.getCurrentUser());

			taskInfoMap.put(StringUtil.TASK_LAT,
					oldattr.get(StringUtil.TASK_LAT));

			taskInfoMap.put(StringUtil.TASK_LONG,
					oldattr.get(StringUtil.TASK_LONG));

			taskInfoMap.put(StringUtil.TASK_OWNER_TASK_ID, taskid);
			taskInfoMap.put(StringUtil.TASK_STATUS, StringUtil.TASK_PENDING);

			String newtaskid = String.valueOf(System.currentTimeMillis());
			util.createItem(friendname, newtaskid, taskInfoMap);
			Toast.makeText(this, "Task sent successfully", Toast.LENGTH_SHORT)
					.show();

			HashMap<String, String> attr = util.getAttributesForItem(
					friendname, StringUtil.USER_INFO);
			sendto = attr.get(StringUtil.EMAIL);

		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_SHORT).show();
			return;
		}

		try {
			LinkedList<String> recipients = new LinkedList<String>();
			recipients.add(sendto);

			StringBuffer body = new StringBuffer();
			body.append("Hi ").append(friendname).append(",\n");
			body.append(currentuser).append(" has sent the following task\n");
			body.append("Please check notifications to accept or decline task")
					.append("\n");

			new AWSEmail().SendMail(StringUtil.SENDER, recipients,
					StringUtil.SUBJECT_TASK_NOTICE, body.toString());
		} catch (Exception e) {
			return;
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
			startActivity(new Intent(FriendScreen.this,
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
}
