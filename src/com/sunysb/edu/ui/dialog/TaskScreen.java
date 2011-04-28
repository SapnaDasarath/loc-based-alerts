package com.sunysb.edu.ui.dialog;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.sunysb.edu.R;
import com.sunysb.edu.db.AWSEmail;
import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.ui.map.Map;
import com.sunysb.edu.util.StringUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class TaskScreen extends Activity {

	private SimpleDbUtil util;
	private int transition = -1;
	private String taskId = null;

	private EditText nameEditText;
	private EditText descriptionEditText;
	private Spinner prioritySpinner;

	private Button okButton;
	private Button tempButton;
	private Button closeButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task);

		Log.e("LBA", "Loading task screen");
		try {
			util = new SimpleDbUtil();
		} catch (Exception e) {
			Toast.makeText(this, "Not able to connect to server, Try again..",
					Toast.LENGTH_LONG).show();
		}

		transition = this.getIntent().getExtras().getInt(StringUtil.TRANSITION);
		// set task id taskId=;

		nameEditText = (EditText) findViewById(R.id.name_EditText);
		descriptionEditText = (EditText) findViewById(R.id.description_EditText);
		prioritySpinner = (Spinner) findViewById(R.id.priority_Spinner);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.priority_list,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		prioritySpinner.setAdapter(adapter);

		okButton = (Button) findViewById(R.id.ok_Task_button);
		tempButton = (Button) findViewById(R.id.send_To_Friend_button);
		closeButton = (Button) findViewById(R.id.close_Task_button);

		switch (transition) {
		case StringUtil.EDIT:
		case StringUtil.VIEW:
			updateUIforTask(taskId);
			break;
		case StringUtil.NOTIFY:
			okButton.setText("Accept");
			tempButton.setText("Decline");
			break;
		}

		// can be add to tasks domain
		// can be accept task
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				switch (transition) {
				case StringUtil.CREATE:
					createNewTaskInDB();
					break;

				case StringUtil.EDIT:
					updateExistingTaskInDB(taskId);
					break;

				case StringUtil.VIEW:
					updateExistingTaskInDB(taskId);
					break;

				case StringUtil.NOTIFY:
					acceptTaskFromFriend(taskId);
					break;

				case StringUtil.DELETE:
					break;

				}
			}
		});

		// can be send to friend
		// can be remove
		// can be decline task
		tempButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				switch (transition) {
				case StringUtil.CREATE:
					sendTaskToFriend();
					break;

				case StringUtil.EDIT:
					sendTaskToFriend();
					break;

				case StringUtil.VIEW:
					sendTaskToFriend();
					break;

				case StringUtil.NOTIFY:
					declineTaskFromFriend(taskId);
					break;

				case StringUtil.DELETE:
					removeTask(taskId);
					break;
				}
			}
		});

		// close tranistion back to different screens can be different
		closeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				switch (transition) {
				case StringUtil.CREATE:
					startActivity(new Intent(TaskScreen.this, Map.class));
					break;

				case StringUtil.EDIT:
					startActivity(new Intent(TaskScreen.this, EditTask.class));
					break;

				case StringUtil.VIEW:
					startActivity(new Intent(TaskScreen.this, EditTask.class));
					break;

				case StringUtil.NOTIFY:
					startActivity(new Intent(TaskScreen.this,
							NotificationScreen.class));
					break;

				case StringUtil.DELETE:
					startActivity(new Intent(TaskScreen.this, EditTask.class));
					break;
				}
			}
		});
	}

	private void createNewTaskInDB() {
		Log.e("LBA", "update DB from task");

		String nameStr = "";
		String descriptionStr = "";
		String priorityStr = "";
		// TODO: poo set the values
		String latitude = "";
		String longitude = "";

		Object name = nameEditText.getText();
		if (name != null) {
			nameStr = name.toString();
		}

		Object description = descriptionEditText.getText();
		if (description != null) {
			descriptionStr = description.toString();
		}

		Object priority = prioritySpinner.getSelectedItem();
		if (priority != null) {
			priorityStr = name.toString();
		}

		String domain = SimpleDbUtil.getCurrentUser();
		String taskid = String.valueOf(System.currentTimeMillis());

		HashMap<String, String> taskInfoMap = new HashMap<String, String>();
		taskInfoMap.put(StringUtil.TASK_NAME, nameStr);
		taskInfoMap.put(StringUtil.TASK_DESCRIPTION, descriptionStr);
		taskInfoMap.put(StringUtil.TASK_PRIORITY, priorityStr);
		taskInfoMap.put(StringUtil.TASK_OWNER, SimpleDbUtil.getCurrentUser());
		taskInfoMap.put(StringUtil.TASK_OWNER_ID, taskid);
		taskInfoMap.put(StringUtil.TASK_LAT, latitude);
		taskInfoMap.put(StringUtil.TASK_LONG, longitude);
		taskInfoMap.put(StringUtil.TASK_STATUS, StringUtil.TASK_ACCEPTED);

		util.createItem(domain, taskid, taskInfoMap);
	}

	private void updateExistingTaskInDB(String taskid) {
		Log.e("LBA", "update Task from DB");

		String nameStr = "";
		String descriptionStr = "";
		String priorityStr = "";

		Object name = nameEditText.getText();
		if (name != null) {
			nameStr = name.toString();
		}

		Object description = descriptionEditText.getText();
		if (description != null) {
			descriptionStr = description.toString();
		}

		Object priority = prioritySpinner.getSelectedItem();
		if (priority != null) {
			priorityStr = name.toString();
		}

		String domain = SimpleDbUtil.getCurrentUser();

		HashMap<String, String> attrList = util.getAttributesForItem(domain,
				taskid);

		String nameStrDb = attrList.get(StringUtil.TASK_NAME);
		String descriptionStrDb = attrList.get(StringUtil.TASK_DESCRIPTION);
		String priorityStrDb = attrList.get(StringUtil.TASK_PRIORITY);

		HashMap<String, String> attrListToUpdate = new HashMap<String, String>();
		if (nameStrDb != nameStr) {
			if (nameStr != null) {
				attrListToUpdate.put(StringUtil.TASK_NAME, nameStr);
			}
		}

		if (descriptionStrDb != descriptionStr) {
			if (descriptionStr != null) {
				attrListToUpdate.put(StringUtil.TASK_DESCRIPTION,
						descriptionStr);
			}
		}

		if (priorityStrDb != priorityStr) {
			if (priorityStr != null) {
				attrListToUpdate.put(StringUtil.TASK_PRIORITY, priorityStr);
			}
		}

		if (attrListToUpdate.size() > 0) {
			util.updateAttributesForItem(domain, taskid, attrListToUpdate);
		}
	}

	protected void acceptTaskFromFriend(String taskId2) {
		// TODO Auto-generated method stub

	}

	protected void declineTaskFromFriend(String taskId2) {
		// TODO Auto-generated method stub

	}

	/**
	 * This method should be called when user selects a task in teh table
	 * 
	 * @param taskid
	 */
	private void updateUIforTask(String taskid) {
		Log.e("LBA", "update Task from DB");

		String domain = SimpleDbUtil.getCurrentUser();

		HashMap<String, String> attrList = util.getAttributesForItem(domain,
				taskid);

		String nameStr = attrList.get(StringUtil.TASK_NAME);
		String descriptionStr = attrList.get(StringUtil.TASK_DESCRIPTION);
		String priorityStr = attrList.get(StringUtil.TASK_PRIORITY);

		if (nameStr != null) {
			nameEditText.setText(nameStr);
		}

		if (descriptionStr != null) {
			descriptionEditText.setText(descriptionStr);
		}

		if (priorityStr != null) {
			prioritySpinner.setSelection(getPosition(priorityStr));
		}
	}

	private int getPosition(String val) {
		int retval = 0;
		if (val.equals(StringUtil.PRIOR_LOW)) {
			retval = 0;
		} else if (val.equals(StringUtil.PRIOR_MED)) {
			retval = 1;
		} else if (val.equals(StringUtil.PRIOR_HIGH)) {
			retval = 2;
		}
		return retval;
	}

	private void sendTaskToFriend() {
		// TODO add a screen to get all friends of this user and show in drop
		// down
		// select one/multiple friends and do a send action.
		// with this action we have to get domains of those users and send
		// (owner domain, taskid) in notification
		// on notification to user if user accepts task retrieve the task from
		// original owner
		// insert into current users task list
		// when the task is completed check if current owner matched task owner
		// if it does not find the task in original owner list and update task
		// id.
		// slso if you ahve shared the task with other ppl update their status
		// also.

		String friendid = "";
		String taskid = "";
		addTaskToFriend(friendid, taskid);
	}

	private void addTaskToFriend(String friendname, String taskid) {

		String currentuser = SimpleDbUtil.getCurrentUser();
		String domain = friendname;
		String newtaskid = String.valueOf(System.currentTimeMillis());

		HashMap<String, String> oldattr = util.getAttributesForItem(
				currentuser, taskid);
		String friendnames = oldattr.get(StringUtil.TASK_FRIENDS_NAMES);
		List<String> friends = util.getFriendsFromString(friendnames);
		if (friends.contains(friendname)) {
			return;
		}
		friends.add(friendname);
		String friendsstr = util.getStringFromList(friends);
		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put(StringUtil.TASK_FRIENDS_NAMES, friendsstr);
		util.updateAttributesForItem(currentuser, taskid, attributes);

		HashMap<String, String> taskInfoMap = new HashMap<String, String>();
		taskInfoMap
				.put(StringUtil.TASK_NAME, oldattr.get(StringUtil.TASK_NAME));
		taskInfoMap.put(StringUtil.TASK_DESCRIPTION,
				oldattr.get(StringUtil.TASK_DESCRIPTION));
		taskInfoMap.put(StringUtil.TASK_PRIORITY,
				oldattr.get(StringUtil.TASK_PRIORITY));
		taskInfoMap.put(StringUtil.TASK_OWNER,
				oldattr.get(StringUtil.TASK_NAME));
		taskInfoMap.put(StringUtil.TASK_OWNER_ID, newtaskid);
		taskInfoMap.put(StringUtil.TASK_LAT, oldattr.get(StringUtil.TASK_LAT));
		taskInfoMap
				.put(StringUtil.TASK_LONG, oldattr.get(StringUtil.TASK_LONG));
		taskInfoMap.put(StringUtil.TASK_STATUS, StringUtil.TASK_PENDING);
		util.createItem(domain, taskid, taskInfoMap);

		HashMap<String, String> attr = util.getAttributesForItem(friendname,
				StringUtil.FRIEND_INFO);
		String sendto = attr.get(StringUtil.EMAIL);
		LinkedList<String> recipients = new LinkedList<String>();
		recipients.add(sendto);

		StringBuffer body = new StringBuffer();
		body.append("Hi ").append(friendname).append(",\n");
		body.append(currentuser).append(" has sent the following task\n");
		body.append("Task Name: ").append(oldattr.get(StringUtil.TASK_NAME))
				.append("\n");
		body.append("Task Description: ")
				.append(oldattr.get(StringUtil.TASK_DESCRIPTION)).append("\n");
		body.append("Please check notifications to accept or decline task")
				.append("\n");

		new AWSEmail().SendMail(StringUtil.SENDER, recipients,
				StringUtil.SUBJECT_TASK_NOTICE, body.toString());
	}

	// If user selects delete task remove it from UI and DB and if the task is a
	// shared task
	// remove it from the person who has the task too
	public boolean removeTask(String taskId) {

		String currentuser = SimpleDbUtil.getCurrentUser();
		HashMap<String, String> currentuserattr = util.getAttributesForItem(
				currentuser, taskId);

		StringBuffer body = new StringBuffer();
		body.append(currentuser).append(" has removed the following task\n");
		body.append("Task Name: ")
				.append(currentuserattr.get(StringUtil.TASK_NAME)).append("\n");
		body.append("Task Description: ")
				.append(currentuserattr.get(StringUtil.TASK_DESCRIPTION))
				.append("\n");
		body.append("This will be removed from your task list").append("\n");

		List<String> username = util.getTaskAcceptedFriends(taskId);
		if (username.size() > 0) {
			for (String user : username) {
				// now that i have the domain name remove the task from the
				// list.
				// get every task.. check if the task shared id matches this
				// if it foes delete it
				// better way of doing this write query
				String usertaskId = null;
				// TODO get usertaskid using db query
				util.deleteItem(user, usertaskId);

				// send notification of delete.
				HashMap<String, String> attr = util.getAttributesForItem(user,
						StringUtil.FRIEND_INFO);
				String sendto = attr.get(StringUtil.EMAIL);

				// send notification to user
				LinkedList<String> recipients = new LinkedList<String>();
				recipients.add(sendto);

				StringBuffer msg = new StringBuffer();
				msg.append("Hi ").append(attr.get(StringUtil.USRNAME))
						.append(",\n");
				msg.append(body.toString());

				new AWSEmail().SendMail(StringUtil.SENDER, recipients,
						StringUtil.SUBJECT_TASK_DELETE, msg.toString());
			}
		}
		util.deleteItem(SimpleDbUtil.getCurrentUser(), taskId);
		return true;
	}
}
