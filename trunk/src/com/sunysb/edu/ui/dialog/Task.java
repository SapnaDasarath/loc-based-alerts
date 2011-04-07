package com.sunysb.edu.ui.dialog;

import java.util.HashMap;

import com.sunysb.edu.R;
import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.util.StringUtil;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class Task extends Activity{
	
	 private EditText nameEditText;
	 private EditText descriptionEditText;
	 private Spinner  prioritySpinner;
	 
	 private Button okButton;
	 private Button closeButton;
	 
	 public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task);
		 
		Log.e( "LBA", "Loading task screen" );      
			
		nameEditText = (EditText) findViewById(R.id.name_EditText);
		descriptionEditText = (EditText) findViewById(R.id.description_EditText);
		prioritySpinner = (Spinner) findViewById(R.id.priority_Spinner);
			
		okButton = (Button) findViewById(R.id.ok_Task_button);
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
	        
		closeButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					//close current intent
				}
			});
	 }
	 
	 
	private void createNewTaskInDB()
	{
		Log.e( "LBA", "update DB from task" ); 
		
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
		String taskid = String.valueOf(System.currentTimeMillis());
		
		SimpleDbUtil util;
		try {
			util = new SimpleDbUtil();
			util.createAttributeForItem(domain, taskid, StringUtil.TASK_NAME, nameStr);
			util.createAttributeForItem(domain,taskid, StringUtil.TASK_DESCRIPTION, descriptionStr);
			util.createAttributeForItem(domain, taskid, StringUtil.TASK_PRIORITY, priorityStr);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
}
