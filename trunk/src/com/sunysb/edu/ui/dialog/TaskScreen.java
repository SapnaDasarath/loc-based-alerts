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
	private int transition;
	private String taskId;
	private String latitude;
	private String longitude;

	private EditText nameEditText;
	private EditText descriptionEditText;
	private Spinner prioritySpinner;

	private Button okButton;
	private Button tempButton;
	private Button closeButton;
	private Button delButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task);

		try {
			util = new SimpleDbUtil();
		} catch (Exception e) {
			Toast.makeText(this, "Not able to connect to server, Try again.",
					Toast.LENGTH_LONG).show();
		}

		transition = (Integer) this.getIntent().getExtras()
				.get(StringUtil.TRANSITION);
		taskId = getIntent().getExtras().getString(StringUtil.TASK_ID);
		latitude = getIntent().getExtras().getString(StringUtil.TASK_LAT);
		longitude = getIntent().getExtras().getString(StringUtil.TASK_LONG);

		nameEditText = (EditText) findViewById(R.id.name_EditText);
		descriptionEditText = (EditText) findViewById(R.id.description_EditText);
		prioritySpinner = (Spinner) findViewById(R.id.priority_Spinner);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.priority_list,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		prioritySpinner.setAdapter(adapter);

		okButton = (Button) findViewById(R.id.ok_Task_button);
		delButton = (Button) findViewById(R.id.del_Task_button);
		tempButton = (Button) findViewById(R.id.send_To_Friend_button);
		closeButton = (Button) findViewById(R.id.close_Task_button);

		// Set UI appropriately
		switch (transition) {
		case StringUtil.CREATE:
			// Do nothing
			tempButton.setVisibility(View.INVISIBLE);
			break;

		case StringUtil.EDIT:
			updateUIforTask(taskId);
			break;

		case StringUtil.NOTIFY:
			updateUIforTask(taskId);
			okButton.setText("Accept");
			tempButton.setText("Decline");
			delButton.setVisibility(View.INVISIBLE);
			break;
		}

		// There will be 4 buttons - ok/accept task, send to friend/decline ,
		// remove, close
		// can be add to tasks domain - ok
		// can be accept task - accept
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				switch (transition) {
				case StringUtil.CREATE:
					createNewTaskInDB();
					break;

				case StringUtil.EDIT:
					updateExistingTaskInDB(taskId);
					break;

				case StringUtil.NOTIFY:
					acceptTaskFromFriend(taskId);
					Intent intent = new Intent(TaskScreen.this,
							NotificationScreen.class);
					intent.putExtra(StringUtil.TRANSITION, transition);
					intent.putExtra(StringUtil.TASK_ID, taskId);
					intent.putExtra(StringUtil.TASK_LAT, latitude);
					intent.putExtra(StringUtil.TASK_LONG, longitude);
					startActivity(intent);
					break;
				}
			}
		});

		// can be send to friend
		// can be decline task
		tempButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				switch (transition) {
				case StringUtil.EDIT:
					Intent intent = new Intent(TaskScreen.this,
							FriendScreen.class);
					intent.putExtra(StringUtil.TRANSITION, transition);
					intent.putExtra(StringUtil.TASK_ID, taskId);
					intent.putExtra(StringUtil.TASK_LAT, latitude);
					intent.putExtra(StringUtil.TASK_LONG, longitude);
					startActivity(intent);
					break;

				case StringUtil.NOTIFY:
					declineTaskFromFriend(taskId);
					Intent intent1 = new Intent(TaskScreen.this,
							NotificationScreen.class);
					intent1.putExtra(StringUtil.TRANSITION, transition);
					intent1.putExtra(StringUtil.TASK_ID, taskId);
					intent1.putExtra(StringUtil.TASK_LAT, latitude);
					intent1.putExtra(StringUtil.TASK_LONG, longitude);
					startActivity(intent1);
					break;
				}
			}
		});

		// close tranistion back to different screens can be different
		delButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				removeTask(taskId);
				Intent intent = new Intent(TaskScreen.this, EditTask.class);
				intent.putExtra(StringUtil.TRANSITION, transition);
				intent.putExtra(StringUtil.TASK_ID, taskId);
				intent.putExtra(StringUtil.TASK_LAT, latitude);
				intent.putExtra(StringUtil.TASK_LONG, longitude);
				startActivity(intent);
			}
		});

		closeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				switch (transition) {
				case StringUtil.CREATE:
					Intent intent = new Intent(TaskScreen.this, Map.class);
					intent.putExtra(StringUtil.TRANSITION, transition);
					intent.putExtra(StringUtil.TASK_ID, taskId);
					intent.putExtra(StringUtil.TASK_LAT, latitude);
					intent.putExtra(StringUtil.TASK_LONG, longitude);
					startActivity(intent);
					break;

				case StringUtil.EDIT:
					Intent intent1 = new Intent(TaskScreen.this, EditTask.class);
					intent1.putExtra(StringUtil.TRANSITION, transition);
					intent1.putExtra(StringUtil.TASK_ID, taskId);
					intent1.putExtra(StringUtil.TASK_LAT, latitude);
					intent1.putExtra(StringUtil.TASK_LONG, longitude);
					startActivity(intent1);
					break;

				case StringUtil.NOTIFY:
					Intent intent2 = new Intent(TaskScreen.this,
							NotificationScreen.class);
					intent2.putExtra(StringUtil.TRANSITION, transition);
					intent2.putExtra(StringUtil.TASK_ID, taskId);
					intent2.putExtra(StringUtil.TASK_LAT, latitude);
					intent2.putExtra(StringUtil.TASK_LONG, longitude);
					startActivity(intent2);
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

		try {
			util.createItem(domain, taskid, taskInfoMap);
		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_SHORT).show();
			return;
		}
		Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT)
				.show();
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

		HashMap<String, String> attrList;
		try {
			attrList = util.getAttributesForItem(domain, taskid);
		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_SHORT).show();
			return;
		}

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
			try {
				util.updateAttributesForItem(domain, taskid, attrListToUpdate);
			} catch (Exception e) {
				Toast.makeText(this,
						"Unable to connect to server. Try again later..",
						Toast.LENGTH_SHORT).show();
				return;
			}
		}
	}

	protected void acceptTaskFromFriend(String taskid) {
		// change task state to accepted in your list and the other persons list
		HashMap<String, String> attrListToUpdate = new HashMap<String, String>();
		attrListToUpdate.put(StringUtil.TASK_STATUS, StringUtil.TASK_ACCEPTED);

		// put this in current user list and put this in friend list
		String domain = SimpleDbUtil.getCurrentUser();
		try {
			util.updateAttributesForItem(domain, taskid, attrListToUpdate);
		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_SHORT).show();
			return;
		}

	}

	/**
	 * This method should be called when user selects a task in the table
	 * 
	 * 
	 * @param taskid
	 */
	private void updateUIforTask(String taskid) {
		Log.e("LBA", "update Task from DB");

		String domain = SimpleDbUtil.getCurrentUser();

		HashMap<String, String> attrList;
		try {
			attrList = util.getAttributesForItem(domain, taskid);
		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_SHORT).show();
			return;
		}

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

	// If user selects delete task remove it from UI and DB and if the task is a
	// shared task
	// remove it from the person who has the task too
	// public boolean removeTask(String taskId) {
	// Log.e("LBA ", "In removeTask " + taskId);
	/*
	 * List<String> tasks = util.getTaskAcceptedFriends(taskId);
	 * 
	 * if (tasks.size() > 0) { // for each user name send the task id to be
	 * deleted. }
	 */
	// util.deleteItem(SimpleDbUtil.getCurrentUser(), taskId);
	// drawUI();
	// return true;
	// }

	protected void declineTaskFromFriend(String taskId) {
		// remove task from your list
		try {
			util.deleteItem(SimpleDbUtil.getCurrentUser(), taskId);
		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_SHORT).show();
			return;
		}
	}

	// If user selects delete task remove it from UI and DB and if the task is a
	// shared task
	// remove it from the person who has the task too
	public boolean removeTask(String taskId) {

		String currentuser = SimpleDbUtil.getCurrentUser();
		HashMap<String, String> currentuserattr;
		try {
			currentuserattr = util.getAttributesForItem(currentuser, taskId);

			StringBuffer body = new StringBuffer();
			body.append(currentuser)
					.append(" has removed the following task\n");
			body.append("Task Name: ")
					.append(currentuserattr.get(StringUtil.TASK_NAME))
					.append("\n");
			body.append("Task Description: ")
					.append(currentuserattr.get(StringUtil.TASK_DESCRIPTION))
					.append("\n");
			body.append("This will be removed from your task list")
					.append("\n");

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
					HashMap<String, String> attr = util.getAttributesForItem(
							user, StringUtil.FRIEND_INFO);
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
		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_SHORT).show();
			return false;
		}

	}
}
