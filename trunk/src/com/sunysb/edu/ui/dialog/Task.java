package com.sunysb.edu.ui.dialog;

import com.sunysb.edu.LocationBasedAlerts;
import com.sunysb.edu.R;
import com.sunysb.edu.ui.map.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
				@Override
				public void onClick(View v) {
					//add to db
				}
			});
	        
		    closeButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//close current intent
				}
			});
	
	 }

}
