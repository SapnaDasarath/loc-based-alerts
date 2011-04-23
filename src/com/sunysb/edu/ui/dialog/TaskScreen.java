package com.sunysb.edu.ui.dialog;

import java.util.HashMap;

import com.sunysb.edu.R;
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

public class TaskScreen extends Activity{
	
	 private EditText nameEditText;
	 private EditText descriptionEditText;
	 private Spinner  prioritySpinner;
	 
	 private Button okButton;
	 private Button sendToFriendButton;
	 private Button closeButton;
	 
	 public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task);
		 
		Log.e( "LBA", "Loading task screen" );      
			
		nameEditText = (EditText) findViewById(R.id.name_EditText);
		descriptionEditText = (EditText) findViewById(R.id.description_EditText);
		prioritySpinner = (Spinner) findViewById(R.id.priority_Spinner);
			
		okButton = (Button) findViewById(R.id.ok_Task_button);
		sendToFriendButton = (Button) findViewById(R.id.send_To_Friend_button);
		closeButton = (Button) findViewById(R.id.close_Task_button);
			
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
		            this, R.array.priority_list, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		prioritySpinner.setAdapter(adapter);
		    
	   //add to tasks domain
		okButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					createNewTaskInDB();
				}
			});
		
		sendToFriendButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				sendTaskToFriend(); 
			}
		});
	        
		closeButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					startActivity(new Intent(TaskScreen.this, Map.class));
				}
			});
	 }
	 
	 
	private void createNewTaskInDB()
	{
		Log.e( "LBA", "update DB from task" ); 
		
		String nameStr = "";
		String descriptionStr = "";
		String priorityStr = "";
		//TODO: poo set the values
		String latitude ="";
		String longitude ="";
		
		Object name = nameEditText.getText();
		if(name != null)
		{
			nameStr = name.toString();
		}
		
		Object description = descriptionEditText.getText();
		if(description != null)
		{
			descriptionStr = description.toString();
		}
		
		Object priority = prioritySpinner.getSelectedItem();
		if(priority != null)
		{
			priorityStr = name.toString();
		}
		
		try {
			SimpleDbUtil util = new SimpleDbUtil();
			
			String domain = SimpleDbUtil.getCurrentUser();
			String taskid = String.valueOf(System.currentTimeMillis());
			
			HashMap<String,String> taskInfoMap = new HashMap<String,String>();
			taskInfoMap.put(StringUtil.TASK_NAME, nameStr);
			taskInfoMap.put(StringUtil.TASK_DESCRIPTION, descriptionStr);
			taskInfoMap.put(StringUtil.TASK_PRIORITY, priorityStr);
			taskInfoMap.put(StringUtil.TASK_OWNER, SimpleDbUtil.getCurrentUser());
			taskInfoMap.put(StringUtil.TASK_OWNER_ID,taskid);
			taskInfoMap.put(StringUtil.TASK_LAT, latitude);
			taskInfoMap.put(StringUtil.TASK_LONG, longitude);
			
			util.createItem(domain, taskid, taskInfoMap);

		} catch (Exception e) {
			Toast.makeText(this, "Not able to connect to server, Try again..",
					Toast.LENGTH_LONG).show();
		}
	}
	
	private void updateExistingTaskInDB(String taskid)
	{
		Log.e( "LBA", "update Task from DB" ); 
		
		String nameStr = "";
		String descriptionStr = "";
		String priorityStr = "";
		
		Object name = nameEditText.getText();
		if(name != null)
		{
			nameStr = name.toString();
		}
		
		Object description = descriptionEditText.getText();
		if(description != null)
		{
			descriptionStr = description.toString();
		}
		
		Object priority = prioritySpinner.getSelectedItem();
		if(priority != null)
		{
			priorityStr = name.toString();
		}
		
		String domain = SimpleDbUtil.getCurrentUser();
		try {
			SimpleDbUtil util = new SimpleDbUtil();
			HashMap<String,String> attrList = util.getAttributesForItem(domain, taskid);
			
			String nameStrDb = attrList.get(StringUtil.TASK_NAME);
			String descriptionStrDb = attrList.get(StringUtil.TASK_DESCRIPTION);
			String priorityStrDb = attrList.get(StringUtil.TASK_PRIORITY);
			
			HashMap<String,String> attrListToUpdate = new HashMap<String,String>();
			if(nameStrDb != nameStr)
			{
				if(nameStr != null)
				{
					attrListToUpdate.put(StringUtil.TASK_NAME, nameStr);
				}
			}
			
			if(descriptionStrDb != descriptionStr)
			{
				if(descriptionStr != null)
				{
					attrListToUpdate.put(StringUtil.TASK_DESCRIPTION, descriptionStr);
				}
			}
			
			if(priorityStrDb != priorityStr)
			{
				if(priorityStr != null)
				{
					attrListToUpdate.put(StringUtil.TASK_PRIORITY, priorityStr);
				}
			}
			
			if(attrListToUpdate.size()>0)
			{
				util.updateAttributesForItem(domain, taskid, attrListToUpdate);
			}
		} catch (Exception e) {
			Toast.makeText(this, "Not able to connect to server, Try again..",
					Toast.LENGTH_LONG).show();
		}
		
	}
	/**
	 * This method should be called when user selects a task in teh table
	 * @param taskid
	 */
	private void updateUIforTask(String taskid)
	{
		Log.e( "LBA", "update Task from DB" ); 
		
		String domain = SimpleDbUtil.getCurrentUser();
		try {
			SimpleDbUtil util = new SimpleDbUtil();
			HashMap<String,String> attrList = util.getAttributesForItem(domain, taskid);
			
			String nameStr = attrList.get(StringUtil.TASK_NAME);
			String descriptionStr = attrList.get(StringUtil.TASK_DESCRIPTION);
			String priorityStr = attrList.get(StringUtil.TASK_PRIORITY);
			
			if(nameStr != null)
			{
				nameEditText.setText(nameStr);
			}
			
			if(descriptionStr != null)
			{
				descriptionEditText.setText(descriptionStr);
			}
			
			if(priorityStr != null)
			{
				prioritySpinner.setSelection(getPosition(priorityStr));
			}
		} catch (Exception e) {
			Toast.makeText(this, "Not able to connect to server, Try again..",
					Toast.LENGTH_LONG).show();
		}
	}
	
	private int getPosition(String val)
	{
		int retval = 0;
		if(val.equals(StringUtil.PRIOR_LOW))
		{
			retval = 0;
		}
		else if(val.equals(StringUtil.PRIOR_MED))
		{
			retval = 1;
		}
		else if(val.equals(StringUtil.PRIOR_HIGH))
		{
			retval = 2;
		}
		return retval;
	}
	
	private void sendTaskToFriend()
	{
		//TODO add a screen to get all friends of this user and show in drop down
		//select one/multiple friends and do a send action.
		//with this action we have to get domains of those users and send (owner domain, taskid) in notification
		//on notification to user if user accepts task retrieve the task from original owner
		//insert into current users task list
		//when the task is completed check if current owner matched task owner
		//if it does not find the task in original owner list and update task id.
		//slso if you ahve shared the task with other ppl update their status also.
	}
}
