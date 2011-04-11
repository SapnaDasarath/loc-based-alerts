package com.sunysb.edu.ui.dialog;

import com.sunysb.edu.R;
import com.sunysb.edu.db.SimpleDbUtil;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewFriendScreen extends Activity {
	private EditText newfriendEditText;

	private Button validateButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newfriend);
		Log.e("LBA", "Loading Add New Friend screen");

		newfriendEditText = (EditText) findViewById(R.id.newfriend_EditText);

		validateButton = (Button) findViewById(R.id.validate_button);
		validateButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Check DB if the username is a valid username.
				if (validateFriend()) {
					// To-do: add this friend to my friendlist in DB
				}
			}
		});

	}

	private boolean validateFriend() {
		String username = null;
		
		Object usernameObj = newfriendEditText.getText();
		if (usernameObj != null) {
			username = usernameObj.toString().trim();
		}

		if (username == null || username.equals("")) {
			Toast.makeText(this, "Enter valid username", Toast.LENGTH_SHORT)
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
			return false;
		}
		return true;
	}
}
