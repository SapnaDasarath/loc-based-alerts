package com.sunysb.edu.ui.dialog;

import com.sunysb.edu.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

	}

}
