package com.sunysb.edu.ui.dialog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

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
import android.widget.Toast;

public class NewUserScreen extends Activity {

	private EditText newuserEditText;
	private EditText passwdEditText;
	private EditText reenterpwdEditText;

	private Button okButton;
	private Button closeButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newuser);
		Log.e("LBA", "Loading New user screen");

		newuserEditText = (EditText) findViewById(R.id.newusername_EditText);
		passwdEditText = (EditText) findViewById(R.id.newpassword_EditText);
		reenterpwdEditText = (EditText) findViewById(R.id.renewpassword_EditText);

		okButton = (Button) findViewById(R.id.ok_user_button);
		closeButton = (Button) findViewById(R.id.close_user_button);

		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (validate()) {
					addUserToDB();
					startActivity(new Intent(NewUserScreen.this,
							UserOptionScreen.class));
				}
			}
		});

		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(NewUserScreen.this,
						LocationBasedAlerts.class));
			}
		});
	}

	private boolean validate() {
		String username = null;
		String password = null;
		String repassword = null;

		Object usernameObj = newuserEditText.getText();
		if (usernameObj != null) {
			username = usernameObj.toString().trim();
		}

		Object passwdObj = passwdEditText.getText();
		if (passwdObj != null) {
			password = passwdObj.toString().trim();
		}

		Object repasswdObj = reenterpwdEditText.getText();
		if (repasswdObj != null) {
			repassword = repasswdObj.toString().trim();
		}

		if (username == null || password == null || repassword == null) {
			Toast.makeText(this, "Enter valid username and password",
					Toast.LENGTH_SHORT).show();
			return false;
		}

		if (username.equals("") || password.equals("") || repassword.equals("")) {
			Toast.makeText(this, "Enter valid username and password",
					Toast.LENGTH_SHORT).show();
			return false;
		}

		if (!password.equals(repassword)) {
			Toast.makeText(this, "Password's don't match", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		// check if user name already exists
		try {
			SimpleDbUtil dbAccess = new SimpleDbUtil(username);
			if (dbAccess.doesDomainExist(username)) {
				Toast.makeText(this, "User Name Exists", Toast.LENGTH_SHORT)
						.show();
				return false;
			}

		} catch (Exception e) {
			Toast.makeText(this, "Not able to connect to server, Try again..",
					Toast.LENGTH_LONG).show();
		}

		return true;
	}

	private boolean addUserToDB() {
		String userName = newuserEditText.getText().toString();
		String passwd = newuserEditText.getText().toString();
		String pwd = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(passwd.getBytes());
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			pwd = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			Toast.makeText(this, "Not able to add user, Try again..",
					Toast.LENGTH_LONG).show();
			return false;
		}
	
		try {
			SimpleDbUtil dbAccess = new SimpleDbUtil(userName);

			//Create a new domain with the user name
			dbAccess.createDomain(userName);
			
			//create items to contain name value pairs for user info and friend info.
			//task info will be added as items for each task.
			HashMap<String,String> userInfoMap = new HashMap<String,String>();
			userInfoMap.put(StringUtil.USRNAME, userName);
			userInfoMap.put(StringUtil.PASSWD, pwd);
			dbAccess.createItem(userName, StringUtil.USER_INFO,userInfoMap);
			
		} catch (Exception e) {
			Toast.makeText(this, "Not able to connect to server, Try again..",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
}
