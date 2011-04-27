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

	SimpleDbUtil util;

	private EditText newfriendEditText;
	private Button validateButton;

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

		newfriendEditText = (EditText) findViewById(R.id.newfriend_EditText);

		validateButton = (Button) findViewById(R.id.validate_button);
		validateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Check DB if the username is a valid username.
				if (validateFriend()) {
					addNewFriend();
				}
			}
		});
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
		
		//add this friend to the other user also
		HashMap<String, String> otherfriendmap = new HashMap<String, String>();
		otherfriendmap.put(StringUtil.FRIEND_NAME, SimpleDbUtil.getCurrentUser());
		otherfriendmap.put(StringUtil.FRIEND_STATUS, StringUtil.FRIEND_PENDING);
		util.createItem(username, StringUtil.FRIEND_INFO
				+ SimpleDbUtil.getCurrentUser(), otherfriendmap);
		
		HashMap<String, String> attr = util.getAttributesForItem(username, StringUtil.FRIEND_INFO);
		String sendto = attr.get(StringUtil.EMAIL);
		
		//send notification to user
		LinkedList<String> recipients = new LinkedList<String>();
		recipients.add(sendto); 
	
		new AWSEmail().SendMail(StringUtil.SENDER, recipients, StringUtil.SUBJECT_FRDREQ, StringUtil.BODY_FRDREQ);	
	}
}
