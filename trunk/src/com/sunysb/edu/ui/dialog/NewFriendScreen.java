package com.sunysb.edu.ui.dialog;

import java.util.HashMap;
import java.util.LinkedList;

import com.sunysb.edu.R;
import com.sunysb.edu.db.AWSEmail;
import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.util.StringUtil;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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

		transition = (Integer)this.getIntent().getExtras().get(StringUtil.TRANSITION);
		friendname = getIntent().getExtras().getString(StringUtil.FRIEND_NAME);

		newfriendEditText = (EditText) findViewById(R.id.newfriend_EditText);
		emailEditText = (EditText) findViewById(R.id.email_EditText);

		acceptButton = (Button) findViewById(R.id.validate_button);
		declineButton = (Button) findViewById(R.id.decline_button);

		switch (transition) {
		case StringUtil.EDIT:
			acceptButton.setText("Ok");
			declineButton.setText("Remove");
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
					removeFriend(friendname);
					break;
				case StringUtil.DELETE:
					removeFriend(friendname);
					break;
				}	
			}
		});
	}

	private void updateUIwithFriendInfo(String friendname) {
		Log.e("LBA", "update Task from DB");

		String domain = SimpleDbUtil.getCurrentUser();

		HashMap<String, String> attrList = util.getAttributesForItem(domain,
				friendname);

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

		Object usernameObj = newfriendEditText.getText();
		if (usernameObj != null) {
			username = usernameObj.toString().trim();
		}

		if (username == null || username.equals("")) {
			Toast.makeText(this, "Enter valid username", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		// check if user name already exists
		if (util.doesDomainExist(StringUtil.FRIEND_INFO + username)) {
			Toast.makeText(this, "User Name Exists", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void addNewFriend() {
		String username = newfriendEditText.getText().toString();

		HashMap<String, String> friendmap = new HashMap<String, String>();
		friendmap.put(StringUtil.FRIEND_NAME, username);
		friendmap.put(StringUtil.FRIEND_STATUS, StringUtil.FRIEND_PENDING);
		util.createItem(SimpleDbUtil.getCurrentUser(), StringUtil.FRIEND_INFO
				+ username, friendmap);

		// add this friend to the other user also
		HashMap<String, String> otherfriendmap = new HashMap<String, String>();
		otherfriendmap.put(StringUtil.FRIEND_NAME,
				SimpleDbUtil.getCurrentUser());
		otherfriendmap.put(StringUtil.FRIEND_STATUS, StringUtil.FRIEND_PENDING);
		util.createItem(username,
				StringUtil.FRIEND_INFO + SimpleDbUtil.getCurrentUser(),
				otherfriendmap);

		// send notification to user
		HashMap<String, String> attr = util.getAttributesForItem(username,
				StringUtil.FRIEND_INFO);
		String sendto = attr.get(StringUtil.EMAIL);

		LinkedList<String> recipients = new LinkedList<String>();
		recipients.add(sendto);

		new AWSEmail().SendMail(StringUtil.SENDER, recipients,
				StringUtil.SUBJECT_FRDREQ, StringUtil.BODY_FRDREQ);
	}
	

	protected void acceptFriendRequest(String friendname) {
		String domain = SimpleDbUtil.getCurrentUser();
		HashMap<String, String> attrListToUpdate = new HashMap<String, String>();
		attrListToUpdate.put(StringUtil.FRIEND_STATUS, StringUtil.FRIEND_CONFIRMED);
		util.updateAttributesForItem(domain, friendname, attrListToUpdate);
		
		//update other guys friend status too..
		util.updateAttributesForItem(friendname, domain, attrListToUpdate);
		
		// send notification to user
		HashMap<String, String> attr = util.getAttributesForItem(friendname,
				StringUtil.FRIEND_INFO);
		String sendto = attr.get(StringUtil.EMAIL);
		LinkedList<String> recipients = new LinkedList<String>();
		recipients.add(sendto);
		StringBuffer sb = new StringBuffer();
		sb.append("Hello ").append(friendname).append(",\n");
		sb.append(domain).append(" has accepted your friend request");

		new AWSEmail().SendMail(StringUtil.SENDER, recipients,
				StringUtil.SUBJECT_FRDREQ_ACC, sb.toString());
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
		util.deleteItem(SimpleDbUtil.getCurrentUser(), name);
		util.deleteItem(name, SimpleDbUtil.getCurrentUser());
	}
}
