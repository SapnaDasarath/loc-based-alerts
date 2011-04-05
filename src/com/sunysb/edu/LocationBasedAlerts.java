package com.sunysb.edu;

import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.ui.dialog.NewUserScreen;
import com.sunysb.edu.ui.dialog.UserOptionScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LocationBasedAlerts extends Activity{
	
	
	private EditText usernameEditText;
	private EditText passwordEditText;
	 
	private Button loginButton;
	private Button registerButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.e( "LBA", "Loading main screen" );        
		
		usernameEditText = (EditText) findViewById(R.id.username_EditText);
		passwordEditText = (EditText) findViewById(R.id.password_EditText);
		
		loginButton = (Button) findViewById(R.id.login_button);
		registerButton = (Button) findViewById(R.id.register_button);
		
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {    
				if(authenticate())
				{
					startActivity(new Intent(LocationBasedAlerts.this, UserOptionScreen.class));
				}
			}
		});
		
		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {    
				//the assumption here is a domain for the user name is created
				//when a user creates an account for the first time
				startActivity(new Intent(LocationBasedAlerts.this, NewUserScreen.class));
			}
		});
	}
	
	private boolean authenticate()
	{
		String username = null;
		String password = null;
		
		Object usernameObj = usernameEditText.getText();
		if(usernameObj != null)
		{
			username = usernameObj.toString().trim();
		}
		
		Object passwdObj = passwordEditText.getText();
		if(passwdObj != null)
		{
			password = passwdObj.toString().trim();
		}
		
		if(username == null || password == null)
		{
			return false;
		}
		
		if(username.equals("")|| password.equals(""))
		{
			return false;
		}
		
		//validate username and password in db now
		return true;
	}
}