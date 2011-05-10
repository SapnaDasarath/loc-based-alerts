package com.sunysb.edu.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
				Intent intent = new Intent(FriendScreen.this, TaskScreen.class);
				intent.putExtra(StringUtil.TRANSITION, transition);
				startActivity(intent);
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
			if (!util.doesDomainExist(nameEditText.getText().toString())) {
				Toast.makeText(this, "Enter valid username", Toast.LENGTH_SHORT)
						.show();
				return;
			}
		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		addTaskToFriend(nameEditText.getText().toString(), taskId);
		Intent intent = new Intent(FriendScreen.this, TaskScreen.class);
		intent.putExtra(StringUtil.TRANSITION, transition);
		startActivity(intent);
	}

	private void addTaskToFriend(String friendname, String taskid) {

		String currentuser = SimpleDbUtil.getCurrentUser();
		String domain = friendname;
		String newtaskid = String.valueOf(System.currentTimeMillis());

		HashMap<String, String> oldattr = new HashMap<String, String>();
		try {
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
					oldattr.get(StringUtil.TASK_NAME));
			
			taskInfoMap.put(StringUtil.TASK_LAT,
					oldattr.get(StringUtil.TASK_LAT));
			
			taskInfoMap.put(StringUtil.TASK_LONG,
					oldattr.get(StringUtil.TASK_LONG));
			
			taskInfoMap.put(StringUtil.TASK_OWNER_ID, newtaskid);
			taskInfoMap.put(StringUtil.TASK_STATUS, StringUtil.TASK_PENDING);
			
			util.createItem(domain, taskid, taskInfoMap);

			HashMap<String, String> attr = util.getAttributesForItem(
					friendname, StringUtil.USER_INFO);
			String sendto = attr.get(StringUtil.EMAIL);
			LinkedList<String> recipients = new LinkedList<String>();
			recipients.add(sendto);

			StringBuffer body = new StringBuffer();
			body.append("Hi ").append(friendname).append(",\n");
			body.append(currentuser).append(" has sent the following task\n");
			body.append("Task Name: ")
					.append(oldattr.get(StringUtil.TASK_NAME)).append("\n");
			body.append("Task Description: ")
					.append(oldattr.get(StringUtil.TASK_DESCRIPTION))
					.append("\n");
			body.append("Please check notifications to accept or decline task")
					.append("\n");

			new AWSEmail().SendMail(StringUtil.SENDER, recipients,
					StringUtil.SUBJECT_TASK_NOTICE, body.toString());
		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_SHORT).show();
			return;
		}
	}

}
