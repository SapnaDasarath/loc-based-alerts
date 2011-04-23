package com.sunysb.edu;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.ui.dialog.NewUserScreen;
import com.sunysb.edu.ui.dialog.UserOptionScreen;
import com.sunysb.edu.util.StringUtil;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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
		Log.e("LBA", "Loading main screen");

		usernameEditText = (EditText) findViewById(R.id.username_EditText);
		passwordEditText = (EditText) findViewById(R.id.password_EditText);

		loginButton = (Button) findViewById(R.id.login_button);
		registerButton = (Button) findViewById(R.id.register_button);

		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (authenticate()) {
					registerApp();
					startLocationManagerServices();
					startActivity(new Intent(LocationBasedAlerts.this,
							UserOptionScreen.class));
				}
			}
		});

		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// the assumption here is a domain for the user name is created
				// when a user creates an account for the first time
				startActivity(new Intent(LocationBasedAlerts.this,
						NewUserScreen.class));
			}
		});
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

		HashMap<String, String> userinfo = util.getAttributesForItem(username,
				StringUtil.USER_INFO);
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
	         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
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

		return true;
	}

	private void registerApp() {
		Intent registrationIntent = new Intent(
				"com.google.android.c2dm.intent.REGISTER");
		registrationIntent.putExtra("lba",
				PendingIntent.getBroadcast(this, 0, new Intent(), 0));
		registrationIntent.putExtra("sender", "androidcse591@gmail.com");
		startService(registrationIntent);
	}

	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(
				"com.google.android.c2dm.intent.REGISTRATION")) {
			String registration = intent.getStringExtra("registration_id");
			if (intent.getStringExtra("error") != null) {
				Toast.makeText(this, "Device registration failed..",
						Toast.LENGTH_SHORT).show();
			} else if (registration != null) {
				// store the registration id of this app in the db.
				// when you want to send an alert to thise user
				// get this id from db and send alert.
			}
		} else if (intent.getAction().equals(
				"com.google.android.c2dm.intent.RECEIVE")) {

		}
	}

	private void startLocationManagerServices() {
		startActivity(new Intent(LocationBasedAlerts.this, LocationAlert.class));
	}
}