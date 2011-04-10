package com.sunysb.edu.ui.dialog;

import java.util.HashMap;

import com.sunysb.edu.R;
import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.util.StringUtil;

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
			Toast.makeText(this, "Enter non null username", Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		// check if username of the friend exists in DB
		try {
			SimpleDbUtil dbAccess = new SimpleDbUtil(username);
			HashMap<String, String> userinfo = dbAccess.getAttributesForItem(
					username, StringUtil.USER_ID);
			if (userinfo.size() > 0) {
				Toast.makeText(this, "User Name Exists", Toast.LENGTH_SHORT)
						.show();
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
