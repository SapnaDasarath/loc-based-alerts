package com.sunysb.edu.ui.dialog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sunysb.edu.LocationBasedAlerts;
import com.sunysb.edu.R;
import com.sunysb.edu.db.SimpleDbUtil;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewUserScreen extends Activity {

	SimpleDbUtil util;
	private EditText newuserEditText;
	private EditText emailEditText;
	private EditText passwdEditText;
	private EditText reenterpwdEditText;

	private Button okButton;
	private Button closeButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newuser);
		Log.e("LBA", "Loading New user screen");

		try {
			util = new SimpleDbUtil(StringUtil.TEMP_USER);

		} catch (Exception e) {
			Toast.makeText(this, "Not able to connect to server, Try again..",
					Toast.LENGTH_LONG).show();
		}

		newuserEditText = (EditText) findViewById(R.id.newusername_EditText);
		emailEditText = (EditText) findViewById(R.id.newuseremail_EditText);
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
		String email = null;
		String password = null;
		String repassword = null;

		Object usernameObj = newuserEditText.getText();
		if (usernameObj != null) {
			username = usernameObj.toString().trim();
		}

		Object emailObj = emailEditText.getText();
		if (emailObj != null) {
			email = emailObj.toString().trim();
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

		if (email == null) {
			Toast.makeText(this, "Enter valid email", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		if (username.equals("") || password.equals("") || repassword.equals("")) {
			Toast.makeText(this, "Enter valid username and password",
					Toast.LENGTH_SHORT).show();
			return false;
		}

		if (email.equals("")) {
			Toast.makeText(this, "Enter valid email", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		// check if email format is valid
		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher m = p.matcher(email);

		if (!m.matches()) {
			Toast.makeText(this, "Enter valid email", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		if (!password.equals(repassword)) {
			Toast.makeText(this, "Password's don't match", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		// check if user name already exists
		try {
			if (util.doesDomainExist(username)) {
				Toast.makeText(this, "User Name Exists", Toast.LENGTH_SHORT)
						.show();
				return false;
			}
		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private boolean addUserToDB() {
		String userName = newuserEditText.getText().toString();
		String email = emailEditText.getText().toString();
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

		// Create a new domain with the user name
		SimpleDbUtil.setCurrentUser(userName);
		try {
			util.createDomain(userName);

			// create items to contain name value pairs for user info and friend
			// info.
			// task info will be added as items for each task.
			HashMap<String, String> userInfoMap = new HashMap<String, String>();
			userInfoMap.put(StringUtil.USRNAME, userName);
			userInfoMap.put(StringUtil.PASSWD, pwd);
			userInfoMap.put(StringUtil.EMAIL, email);
			util.createItem(userName, StringUtil.USER_INFO, userInfoMap);
		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
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
			startActivity(new Intent(NewUserScreen.this,
					LocationBasedAlerts.class));
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
		startActivity(new Intent(NewUserScreen.this, UserOptionScreen.class));
		return;
	}
}
