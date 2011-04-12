package com.sunysb.edu.ui.dialog;

import java.util.HashMap;

import com.sunysb.edu.R;
import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.util.StringUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
			//TODO find out if this updates or resets other attributes.
			SimpleDbUtil util = new SimpleDbUtil();
			HashMap<String, String> newattrset = new HashMap<String, String> ();
			newattrset.put(StringUtil.PASSWD, passwdEditText.getText().toString());
			util.updateAttributesForItem(SimpleDbUtil.getCurrentUser(), StringUtil.USER_INFO, newattrset);
		} catch (Exception e) {
			Toast.makeText(this, "Not able to connect to server, Try again..",
					Toast.LENGTH_LONG).show();
		}
		
	}

}
