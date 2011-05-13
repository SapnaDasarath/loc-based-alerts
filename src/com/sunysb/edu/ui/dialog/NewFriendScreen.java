package com.sunysb.edu.ui.dialog;

import java.util.HashMap;
import java.util.LinkedList;

import com.sunysb.edu.LocationBasedAlerts;
import com.sunysb.edu.R;
import com.sunysb.edu.db.AWSEmail;
import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.util.StringUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewFriendScreen extends Activity {

	private SimpleDbUtil util;
	private int transition = -1;
	private String friendname = null;

	private EditText newfriendEditText;
	private EditText emailEditText;

	private Button acceptButton;
	private Button declineButton;
	private Button closeButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newfriend);
		Log.e("LBA", "Loading Add New Friend screen");

		try {
			util = new SimpleDbUtil();
		} catch (Exception e) {
			Toast.makeText(this, "Not able to connect to server, Try again..",
					Toast.LENGTH_LONG).show();
		}

		transition = (Integer) this.getIntent().getExtras()
				.get(StringUtil.TRANSITION);
		friendname = getIntent().getExtras().getString(StringUtil.FRIEND_NAME);

		newfriendEditText = (EditText) findViewById(R.id.newfriend_EditText);
		emailEditText = (EditText) findViewById(R.id.email_EditText);

		acceptButton = (Button) findViewById(R.id.validate_button);
		declineButton = (Button) findViewById(R.id.decline_button);
		closeButton = (Button) findViewById(R.id.newfrnd_close_button);

		switch (transition) {
		case StringUtil.CREATE:
			acceptButton.setText("Send");
			declineButton.setVisibility(View.INVISIBLE);
			break;
		case StringUtil.EDIT:
			acceptButton.setText("Ok");
			declineButton.setText("Remove");
			updateUIwithFriendInfo(friendname);
			break;
		case StringUtil.NOTIFY:
			updateUIwithFriendInfo(friendname);
			break;
		}

		acceptButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (transition) {
				case StringUtil.CREATE:
					if (validateFriend()) {
						addNewFriend();
					}
					break;
				case StringUtil.NOTIFY:
					acceptFriendRequest(friendname);
					break;
				}
			}
		});

		declineButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (transition) {
				case StringUtil.NOTIFY:
					declineFriendRequest(friendname);
					break;
				case StringUtil.EDIT:
					removeFriend(friendname);
					break;
				}
			}
		});

		closeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(NewFriendScreen.this, UserOptionScreen.class));
				return;
//				switch (transition) {
//				case StringUtil.CREATE:
//				case StringUtil.EDIT:
//					Intent intent = new Intent(NewFriendScreen.this,
//							FriendScreen.class);
//					intent.putExtra(StringUtil.TRANSITION, transition);
//					intent.putExtra(StringUtil.FRIEND_NAME, friendname);
//					startActivity(intent);
//					break;
//				case StringUtil.NOTIFY:
//					Intent intent1 = new Intent(NewFriendScreen.this,
//							NotificationScreen.class);
//					intent1.putExtra(StringUtil.TRANSITION, transition);
//					intent1.putExtra(StringUtil.FRIEND_NAME, friendname);
//					startActivity(intent1);
//					break;
//				}
			}
		});
	}

	private void updateUIwithFriendInfo(String friendname) {

		if (friendname == null)
			return;

		String domain = SimpleDbUtil.getCurrentUser();
		HashMap<String, String> attrList = new HashMap<String, String>();
		try {
			attrList.putAll(util.getAttributesForItem(domain, friendname));
		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_LONG).show();
			return;
		}

		String nameStr = attrList.get(StringUtil.FRIEND_NAME);
		String descriptionStr = attrList.get(StringUtil.EMAIL);

		if (nameStr != null) {
			newfriendEditText.setText(nameStr);
		}

		if (descriptionStr != null) {
			emailEditText.setText(descriptionStr);
		}
	}

	private boolean validateFriend() {
		String username = null;
		String email = null;

		Object usernameObj = newfriendEditText.getText();
		if (usernameObj != null) {
			username = usernameObj.toString().trim();
		}

		if (username == null || username.equals("")) {
			Toast.makeText(this, "Enter valid username", Toast.LENGTH_LONG)
					.show();
			return false;
		}

		Object emailnameObj = emailEditText.getText();
		if (emailnameObj != null) {
			email = emailnameObj.toString().trim();
		}

		if (email == null || email.equals("")) {
			Toast.makeText(this, "Enter valid email id", Toast.LENGTH_LONG)
					.show();
			return false;
		}

		// check if user name already exists
		try {
			if (!util.doesDomainExist(username)) {
				Toast.makeText(this, "User does not exist", Toast.LENGTH_LONG)
						.show();
				return false;
			}
		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private void addNewFriend() {
		String username = newfriendEditText.getText().toString();
		String email = emailEditText.getText().toString();
		String sendto = null;

		try {
			HashMap<String, String> friendmap = new HashMap<String, String>();
			friendmap.put(StringUtil.FRIEND_NAME, username);
			friendmap.put(StringUtil.EMAIL, email);
			friendmap.put(StringUtil.FRIEND_STATUS, StringUtil.FRIEND_REQ_SENT);
			util.createItem(SimpleDbUtil.getCurrentUser(),
					StringUtil.FRIEND_INFO + username, friendmap);

			// add this friend to the other user also
			HashMap<String, String> otherfriendmap = new HashMap<String, String>();
			otherfriendmap.put(StringUtil.FRIEND_NAME,
					SimpleDbUtil.getCurrentUser());
			HashMap<String, String> attr = util.getAttributesForItem(username,
					StringUtil.USER_INFO);
			sendto = attr.get(StringUtil.EMAIL);
			otherfriendmap.put(StringUtil.EMAIL, sendto);
			otherfriendmap.put(StringUtil.FRIEND_STATUS,
					StringUtil.FRIEND_PENDING);
			util.createItem(username,
					StringUtil.FRIEND_INFO + SimpleDbUtil.getCurrentUser(),
					otherfriendmap);

			Toast.makeText(this, "Friend request sent.", Toast.LENGTH_LONG)
					.show();
		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_LONG).show();
			return;
		}

		try {
			if (sendto != null) {
				// send notification to user
				LinkedList<String> recipients = new LinkedList<String>();
				recipients.add(sendto);

				new AWSEmail().SendMail(StringUtil.SENDER, recipients,
						StringUtil.SUBJECT_FRDREQ, StringUtil.BODY_FRDREQ);
			}
		} catch (Exception e) {
			return;
		}
	}

	protected void acceptFriendRequest(String friendname) {
		String domain = SimpleDbUtil.getCurrentUser();
		String sendto = null;
		try {
			HashMap<String, String> attrListToUpdate = new HashMap<String, String>();
			attrListToUpdate.put(StringUtil.FRIEND_STATUS,
					StringUtil.FRIEND_CONFIRMED);
			util.updateAttributesForItem(domain, friendname, attrListToUpdate);

			// update other guys friend status too..
			String otherfrnd = friendname.replace(StringUtil.FRIEND_INFO, "");
			util.updateAttributesForItem(otherfrnd, StringUtil.FRIEND_INFO
					+ domain, attrListToUpdate);

			Toast.makeText(this, "Friend request Accepted.", Toast.LENGTH_LONG)
					.show();

			// send notification to user
			HashMap<String, String> attr = util.getAttributesForItem(otherfrnd,
					StringUtil.USER_INFO);
			sendto = attr.get(StringUtil.EMAIL);

		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_LONG).show();
			return;
		}

		try {
			if (sendto != null) {
				LinkedList<String> recipients = new LinkedList<String>();
				recipients.add(sendto);
				StringBuffer sb = new StringBuffer();
				sb.append("Hello ").append(friendname).append(",\n");
				sb.append(domain).append(" has accepted your friend request");
				new AWSEmail().SendMail(StringUtil.SENDER, recipients,
						StringUtil.SUBJECT_FRDREQ_ACC, sb.toString());
			}
		} catch (Exception e) {
			return;
		}
	}

	private void declineFriendRequest(String name) {
		try {
			util.deleteItem(SimpleDbUtil.getCurrentUser(), name);

			String dom = name.replace(StringUtil.FRIEND_INFO, "");
			String item = StringUtil.FRIEND_INFO
					+ SimpleDbUtil.getCurrentUser();
			util.deleteItem(dom, item);
			Toast.makeText(this, "Friend request Declined.", Toast.LENGTH_LONG)
					.show();

		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_LONG).show();
			return;
		}
	}

	// Remove friend from your list and the other user list also
	// remove all shared tasks.
	private void removeFriend(String name) {
		// TODO
		// get all shared tasks between these two ppl.
		// there must be some query way of doing this.

		// go tru this guys tasks list to see if there is a task with owner as
		// friend to remove

		// go tru friend to remove's task list to see if there is a task with
		// owner as current user
		// remove both.

		// remove from current user
		try {
			util.deleteItem(SimpleDbUtil.getCurrentUser(), name);

			String dom = name.replace(StringUtil.FRIEND_INFO, "");
			String item = StringUtil.FRIEND_INFO
					+ SimpleDbUtil.getCurrentUser();
			util.deleteItem(dom, item);
			Toast.makeText(this, "Friend deleted.", Toast.LENGTH_LONG).show();

		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_LONG).show();
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
			
			editor.putString(StringUtil.USRNAME, "");
			editor.commit();
			
			startActivity(new Intent(NewFriendScreen.this,
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

	@Override
	public void onBackPressed() {
		startActivity(new Intent(NewFriendScreen.this, UserOptionScreen.class));
		return;
	}
}
