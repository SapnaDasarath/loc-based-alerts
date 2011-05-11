package com.sunysb.edu;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.ui.dialog.NewUserScreen;
import com.sunysb.edu.ui.dialog.UserOptionScreen;
import com.sunysb.edu.util.StringUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LocationBasedAlerts extends Activity {

	private EditText usernameEditText;
	private EditText passwordEditText;

	private Button loginButton;
	private Button registerButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		usernameEditText = (EditText) findViewById(R.id.username_EditText);
		passwordEditText = (EditText) findViewById(R.id.password_EditText);

		loginButton = (Button) findViewById(R.id.login_button);
		registerButton = (Button) findViewById(R.id.register_button);

		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean appstate = app_preferences.getBoolean(StringUtil.TASK_INFO,
				false);
		if (appstate) {
			// already logged in
			startLocationManagerServices();
			startActivity(new Intent(LocationBasedAlerts.this,
					UserOptionScreen.class));
			return;
		} else {
			loginButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (authenticate()) {
						startLocationManagerServices();
						startActivity(new Intent(LocationBasedAlerts.this,
								UserOptionScreen.class));
					}
				}
			});

			registerButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(LocationBasedAlerts.this,
							NewUserScreen.class));
				}
			});
		}
	}

	private boolean authenticate() {
		String username = null;
		String password = null;

		Object usernameObj = usernameEditText.getText();
		if (usernameObj != null) {
			username = usernameObj.toString().trim();
		}

		Object passwdObj = passwordEditText.getText();
		if (passwdObj != null) {
			password = passwdObj.toString().trim();
		}

		if (username == null || password == null) {
			Toast.makeText(this, "Enter valid username and password",
					Toast.LENGTH_SHORT).show();
			return false;
		}

		if (username.equals("") || password.equals("")) {
			Toast.makeText(this, "Enter valid username and password",
					Toast.LENGTH_SHORT).show();
			return false;
		}

		// validate username and password in db now
		SimpleDbUtil util = null;
		try {
			util = new SimpleDbUtil(username);
		} catch (Exception e) {
			Toast.makeText(this, "Not able to connect to server, Try again..",
					Toast.LENGTH_LONG).show();
			return false;
		}

		HashMap<String, String> userinfo;
		try {

			if (!util.doesDomainExist(username)) {
				Toast.makeText(this, "Enter valid username and password",
						Toast.LENGTH_SHORT).show();
				return false;
			}

			userinfo = util
					.getAttributesForItem(username, StringUtil.USER_INFO);
			if (userinfo == null || userinfo.size() == 0) {
				Toast.makeText(this, "Enter valid username and password",
						Toast.LENGTH_SHORT).show();
				return false;
			}

			String pwd = userinfo.get(StringUtil.PASSWD);
			try {
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				md.update(password.getBytes());
				byte byteData[] = md.digest();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < byteData.length; i++) {
					sb.append(Integer
							.toString((byteData[i] & 0xff) + 0x100, 16)
							.substring(1));
				}
				if (!pwd.equals(sb.toString())) {
					Toast.makeText(this, "Enter valid username and password",
							Toast.LENGTH_SHORT).show();
					return false;
				}
			} catch (NoSuchAlgorithmException e) {
				Toast.makeText(this, "Enter valid username and password",
						Toast.LENGTH_SHORT).show();
				return false;
			}
		} catch (Exception e1) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_SHORT).show();
			return false;
		}

		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = app_preferences.edit();
		editor.putBoolean(StringUtil.TASK_INFO, true);
		editor.commit(); // Very important
		return true;
	}

	private void startLocationManagerServices() {
		// startActivity(new Intent(LocationBasedAlerts.this,
		// LocationAlert.class));
		Log.e("LBA", "Entered startLocationManagerService() method");
		Intent intent = new Intent(this, LocationAlertService.class);
		startService(intent);
	}
}