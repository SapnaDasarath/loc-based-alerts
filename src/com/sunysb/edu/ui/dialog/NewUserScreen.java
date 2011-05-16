package com.sunysb.edu.ui.dialog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sunysb.edu.LocationAlertService;
import com.sunysb.edu.LocationBasedAlerts;
import com.sunysb.edu.R;
import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.util.CryptoUtils;
import com.sunysb.edu.util.StringUtil;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
			util = new SimpleDbUtil(this,StringUtil.TEMP_USER);

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
					// generate a private public key for this user. store the
					// private key on phone locally and save the public one
					startLocationManagerServices();
					generateKeys();
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

	protected void generateKeys() {
		String username = newuserEditText.getText().toString();
		HashMap<String, String> map = CryptoUtils.generateKeyPair();
		
		SharedPreferences pref = getApplicationContext().getSharedPreferences(
				StringUtil.LBA_PREF, Activity.MODE_PRIVATE);
		Editor prefsEditor = pref.edit();
		prefsEditor.putString(StringUtil.SIGN_PRIVATE_KEY,
				map.get(StringUtil.PRIVATE_KEY));
		prefsEditor.commit();
		
		//TODO
		//send public key to server
		SharedPreferences prefpub = getApplicationContext().getSharedPreferences(
				StringUtil.LBA_PREF, Activity.MODE_PRIVATE);
		Editor prefsEditorpub = prefpub.edit();
		prefsEditorpub.putString(StringUtil.SIGN_PUBLIC_KEY,
				map.get(StringUtil.PUBLIC_KEY));
		prefsEditorpub.commit();
		util.addKeyToServer(username, StringUtil.SIGN_PUBLIC_KEY, map.get(StringUtil.PUBLIC_KEY));
		
		HashMap<String, String> mapenc = CryptoUtils.generateKeyPair();
		SharedPreferences prefenc = getApplicationContext()
				.getSharedPreferences(StringUtil.LBA_PREF,
						Activity.MODE_PRIVATE);
		Editor prefsEditorenc = prefenc.edit();
		prefsEditorenc.putString(StringUtil.ENCDEC_PRIVATE_KEY,
				mapenc.get(StringUtil.PRIVATE_KEY));
		prefsEditorenc.commit();
		
		//TODO
		//send public key to server
		SharedPreferences prefpub1 = getApplicationContext().getSharedPreferences(
				StringUtil.LBA_PREF, Activity.MODE_PRIVATE);
		Editor prefsEditorpub1 = prefpub1.edit();
		prefsEditorpub1.putString(StringUtil.SIGN_PUBLIC_KEY,
				mapenc.get(StringUtil.PUBLIC_KEY));
		prefsEditorpub1.commit();
		
		util.addKeyToServer(username, StringUtil.ENCDEC_PUBLIC_KEY, mapenc.get(StringUtil.PUBLIC_KEY));
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
					Toast.LENGTH_LONG).show();
			return false;
		}

		if (email == null) {
			Toast.makeText(this, "Enter valid email", Toast.LENGTH_LONG).show();
			return false;
		}

		if (username.equals("") || password.equals("") || repassword.equals("")) {
			Toast.makeText(this, "Enter valid username and password",
					Toast.LENGTH_LONG).show();
			return false;
		}

		if (email.equals("")) {
			Toast.makeText(this, "Enter valid email", Toast.LENGTH_LONG).show();
			return false;
		}

		// check if email format is valid
		Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
		Matcher m = p.matcher(email);

		if (!m.matches()) {
			Toast.makeText(this, "Enter valid email", Toast.LENGTH_LONG).show();
			return false;
		}

		if (!password.equals(repassword)) {
			Toast.makeText(this, "Password's don't match", Toast.LENGTH_LONG)
					.show();
			return false;
		}

		// check if user name already exists
		try {
			if (util.doesDomainExist(username)) {
				Toast.makeText(this, "User Name Exists", Toast.LENGTH_LONG)
						.show();
				return false;
			}
		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_LONG).show();
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
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = app_preferences.edit();
		editor.putString(StringUtil.USRNAME, userName);
		editor.commit();
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

			Toast.makeText(this, "Registration succesful.", Toast.LENGTH_LONG)
					.show();

		} catch (Exception e) {
			Toast.makeText(this,
					"Unable to connect to server. Try again later..",
					Toast.LENGTH_LONG).show();
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

			editor.putString(StringUtil.USRNAME, "");
			editor.commit();

			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancelAll();

			String query = "select * from " + SimpleDbUtil.getCurrentUser()
					+ " where " + StringUtil.TASK_NOTIFY + " = '"
					+ StringUtil.TASK_NOTIFY_YES + "'";
			try {
				List<String> tasklist = new ArrayList<String>();
				tasklist.addAll(util.getItemNamesForQuery(query));
				for (String itemid : tasklist) {
					HashMap<String, String> attrListToUpdate = new HashMap<String, String>();
					attrListToUpdate.put(StringUtil.TASK_NOTIFY,
							StringUtil.TASK_NOTIFY_NO);
					util.updateAttributesForItem(SimpleDbUtil.getCurrentUser(),
							itemid, attrListToUpdate);
				}
			} catch (Exception e) {
				Toast.makeText(this,
						"Unable to connect to server. Try again later..",
						Toast.LENGTH_LONG).show();
			}

			startActivity(new Intent(NewUserScreen.this,
					LocationBasedAlerts.class));
			return true;
		}
		return false;
	}

	private void startLocationManagerServices() {
		Log.e("LBA", "Entered startLocationManagerService() method");
		Intent intent = new Intent(this, LocationAlertService.class);
		startService(intent);
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
