package com.sunysb.edu.ui.dialog;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.sunysb.edu.LocationBasedAlerts;
import com.sunysb.edu.R;
import com.sunysb.edu.db.AWSEmail;
import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.ui.map.Map;
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
		case StringUtil.EDIT:
			updateUIforTask(taskId);
			break;

		case StringUtil.NOTIFY:
			updateUIforTask(taskId);
			okButton.setText("Accept");
			tempButton.setText("Decline");
			delButton.setVisibility(View.INVISIBLE);
			break;

		case StringUtil.NOTIFICATION:
			updateUIforTask(taskId);
			tempButton.setVisibility(View.INVISIBLE);
			delButton.setVisibility(View.INVISIBLE);
			closeButton.setVisibility(View.INVISIBLE);
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
					break;

				case StringUtil.NOTIFICATION:
					markAsCompleted(taskId);
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
					// send task to friend
					// show send to friend screen from here.
					Intent intent = new Intent(TaskScreen.this,
							FriendScreen.class);
					intent.putExtra(StringUtil.TRANSITION, transition);
					intent.putExtra(StringUtil.TASK_ID, taskId);
					startActivity(intent);
					break;

				case StringUtil.NOTIFY:
					// remove task from current user list
					declineTaskFromFriend(taskId);
					Intent intent1 = new Intent(TaskScreen.this,
							NotificationScreen.class);
					intent1.putExtra(StringUtil.TRANSITION, transition);
					startActivity(intent1);
					break;
				}
			}
		});

		delButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				removeTask(taskId);
				Intent intent = new Intent(TaskScreen.this, EditTask.class);
				intent.putExtra(StringUtil.TRANSITION, transition);
				startActivity(intent);
			}
		});

		// close transition back to different screens can be different
		closeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				switch (transition) {
				case StringUtil.CREATE:
					Intent intent = new Intent(TaskScreen.this, Map.class);
					intent.putExtra(StringUtil.TRANSITION, transition);
					startActivity(intent);
					break;

				case StringUtil.EDIT:
					Intent intent1 = new Intent(TaskScreen.this, EditTask.class);
					intent1.putExtra(StringUtil.TRANSITION, transition);
					startActivity(intent1);
					break;

				case StringUtil.NOTIFY:
					Intent intent2 = new Intent(TaskScreen.this,
							NotificationScreen.class);
					intent2.putExtra(StringUtil.TRANSITION, transition);
					startActivity(intent2);
					break;
				}
			}
		});
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
		taskInfoMap.put(StringUtil.TASK_NOTIFY, StringUtil.TASK_NOTIFY_NO);
		taskInfoMap.put(StringUtil.TASK_OWNER, SimpleDbUtil.getCurrentUser());
		taskInfoMap.put(StringUtil.TASK_OWNER_TASK_ID, taskid);
		taskInfoMap.put(StringUtil.TASK_LAT, latitude);
		taskInfoMap.put(StringUtil.TASK_LONG, longitude);

		try {
			util.createItem(domain, taskid, taskInfoMap);
			Toast.makeText(this, "New Task added successfully",
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_SHORT).show();
			return;
		}
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
				Toast.makeText(this, "Task updated successfully",
						Toast.LENGTH_SHORT).show();
				return;
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
			Toast.makeText(this, "Task Accepted.", Toast.LENGTH_SHORT).show();
			return;
		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_SHORT).show();
			return;
		}
	}

	protected void markAsCompleted(String taskid) {
		removeTask(taskid);
		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startMain);
	}

	protected void declineTaskFromFriend(String taskId) {
		// remove task from your list only
		// also remove this task as shared with in the original user
		try {
			String currentuser = SimpleDbUtil.getCurrentUser();
			HashMap<String, String> currentuserattr = util
					.getAttributesForItem(currentuser, taskId);

			String taskowner = currentuserattr.get(StringUtil.TASK_OWNER);
			String taskownerId = currentuserattr
					.get(StringUtil.TASK_OWNER_TASK_ID);
			HashMap<String, String> otheruserattr = util.getAttributesForItem(
					taskowner, taskownerId);
			String frndsnames = otheruserattr
					.get(StringUtil.TASK_FRIENDS_NAMES);
			List<String> frnds = util.getFriendsFromString(frndsnames);
			frnds.remove(currentuser);

			String newfrnds = util.getStringFromList(frnds);
			HashMap<String, String> attrListToUpdate = new HashMap<String, String>();
			attrListToUpdate.put(StringUtil.TASK_FRIENDS_NAMES, newfrnds);
			util.updateAttributesForItem(taskowner, taskownerId,
					attrListToUpdate);

			util.deleteItem(currentuser, taskId);
			Toast.makeText(this, "Task Declined.", Toast.LENGTH_SHORT).show();
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
					// TODO get usertaskid using db query
					//get from a task whose shared task id -= id;
					String query = "select * from " + user + " where "
					+ StringUtil.TASK_OWNER_TASK_ID + " = " + "'"
					+ taskId + "'";
					List<String> items = util.getItemNamesForQuery(query);
					util.deleteItem(user, items.get(0));

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

					//new AWSEmail().SendMail(StringUtil.SENDER, recipients,
					//		StringUtil.SUBJECT_TASK_DELETE, msg.toString());
				}
			}
			util.deleteItem(SimpleDbUtil.getCurrentUser(), taskId);
			Toast.makeText(this, "Task deleted successfully",
					Toast.LENGTH_SHORT).show();
			return true;
		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_SHORT).show();
			return false;
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

			startActivity(new Intent(TaskScreen.this, LocationBasedAlerts.class));
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

	public void onBackPressed() {
		startActivity(new Intent(TaskScreen.this, UserOptionScreen.class));
		return;
	}
}
