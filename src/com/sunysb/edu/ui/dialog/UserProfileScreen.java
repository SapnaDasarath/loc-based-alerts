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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserProfileScreen extends Activity {

	private EditText passwdEditText;
	private EditText reenterpwdEditText;

	private Button okButton;
	private Button closeButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userprofile);

		passwdEditText = (EditText) findViewById(R.id.changepwd_EditText);
		reenterpwdEditText = (EditText) findViewById(R.id.reenterpwd_EditText);

		okButton = (Button) findViewById(R.id.ok_profile_button);
		closeButton = (Button) findViewById(R.id.close_profile_button);

		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (validate()) {
					resetpasswdOnDb();
				}
			}
		});

		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(UserProfileScreen.this,
						UserOptionScreen.class));
			}
		});
	}

	private boolean validate() {
		String password = null;
		String repassword = null;

		Object passwdObj = passwdEditText.getText();
		if (passwdObj != null) {
			password = passwdObj.toString().trim();
		}

		Object repasswdObj = reenterpwdEditText.getText();
		if (repasswdObj != null) {
			repassword = repasswdObj.toString().trim();
		}

		if (password == null || repassword == null) {
			return false;
		}

		if (password.equals("") || repassword.equals("")) {
			return false;
		}

		if (!password.equals(repassword)) {
			return false;
		}
		return true;
	}

	private void resetpasswdOnDb() {

		try {
			// TODO find out if this updates or resets other attributes.
			String passwd = passwdEditText.getText().toString();
			String pwd = null;
			try {
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				md.update(passwd.getBytes());
				byte byteData[] = md.digest();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < byteData.length; i++) {
					sb.append(Integer
							.toString((byteData[i] & 0xff) + 0x100, 16)
							.substring(1));
				}
				pwd = sb.toString();
			} catch (NoSuchAlgorithmException e) {
				Toast.makeText(this,
						"Not able to change password, Try again..",
						Toast.LENGTH_LONG).show();
				return;
			}

			SimpleDbUtil util = new SimpleDbUtil();
			HashMap<String, String> newattrset = new HashMap<String, String>();
			newattrset.put(StringUtil.PASSWD, pwd);
			util.updateAttributesForItem(SimpleDbUtil.getCurrentUser(),
					StringUtil.USER_INFO, newattrset);
			Toast.makeText(this, "Password updated successfully",
					Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(this, "Not able to connect to server, Try again..",
					Toast.LENGTH_LONG).show();
		}
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
			startActivity(new Intent(UserProfileScreen.this,
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

	@Override
	public void onBackPressed() {
		startActivity(new Intent(UserProfileScreen.this, UserOptionScreen.class));
		return;
	}
}
