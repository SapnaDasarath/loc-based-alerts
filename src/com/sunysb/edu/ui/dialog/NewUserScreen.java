package com.sunysb.edu.ui.dialog;

import com.sunysb.edu.LocationBasedAlerts;
import com.sunysb.edu.R;
import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.util.StringUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewUserScreen extends Activity{

	 private EditText newuserEditText;
	 private EditText passwdEditText;
	 private EditText reenterpwdEditText;
	 
	 private Button okButton;
	 private Button closeButton;
	 
	 public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newuser);
		Log.e( "LBA", "Loading New user screen" );        
			
		newuserEditText = (EditText) findViewById(R.id.newusername_EditText);
		passwdEditText = (EditText) findViewById(R.id.newpassword_EditText);
		reenterpwdEditText = (EditText) findViewById(R.id.renewpassword_EditText);
			
		okButton = (Button) findViewById(R.id.ok_user_button);
		closeButton = (Button) findViewById(R.id.close_user_button);
			
		okButton.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {    
			if(validate())
			{
				addUserToDB();
				startActivity(new Intent(NewUserScreen.this, UserOptionScreen.class));
			}
		}});
			
		closeButton.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {    
				startActivity(new Intent(NewUserScreen.this, LocationBasedAlerts.class));
			}
		});
	 }
	 
	 private boolean validate()
	 {
		String username = null;
		String password = null;
		String repassword = null;
		boolean userexists = false;
			
		Object usernameObj = newuserEditText.getText();
		if(usernameObj != null)
		{
			username = usernameObj.toString().trim();
		}
			
		Object passwdObj = passwdEditText.getText();
		if(passwdObj != null)
		{
			password = passwdObj.toString().trim();
		}
		
		Object repasswdObj = reenterpwdEditText.getText();
		if(repasswdObj != null)
		{
			repassword = repasswdObj.toString().trim();
		}
			
		if(username == null || password == null || repassword == null)
		{
			//Toast.MakeText(this, "Enter valid username and password", ToastLength.Short).Show(); 
			return false;
		}
			
		if(username.equals("")|| password.equals("") || repassword.equals(""))
		{
			//Toast.MakeText(this, "Enter valid username and password", ToastLength.Short).Show(); 
			return false;
		}
		 //check if user name already exists
		 SimpleDbUtil dbAccess = new SimpleDbUtil(username);
		 if(userexists)
		 {
			// Toast.MakeText(this, "User Name Exists", ToastLength.Short).Show(); 
			 return false;
		 }
			
		if(!password.equals(repassword))
		{
			//Toast.MakeText(this, "Password's don't match", ToastLength.Short).Show(); 
			return false;
		}	
		 return true;
	 }
	 
	 private void addUserToDB()
	 {
		 String userName = newuserEditText.getText().toString();
		 SimpleDbUtil dbAccess = new SimpleDbUtil(userName);
		 dbAccess.createDomain(userName);
		 dbAccess.createItem(userName, StringUtil.USER_INFO);
		 dbAccess.createItem(userName, StringUtil.FRIEND_INFO);
	 }
}
