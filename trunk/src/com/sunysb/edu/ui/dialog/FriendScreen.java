package com.sunysb.edu.ui.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.sunysb.edu.R;
import com.sunysb.edu.db.SimpleDbUtil;
import com.sunysb.edu.util.StringUtil;

public class FriendScreen extends Activity{
	
	private SimpleDbUtil util;
	private int transition;
	//contains friend name
	//remove friend button
	//or accept/decline friend request button
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task);

		Log.e("LBA", "Loading task screen");
		try {
			util = new SimpleDbUtil();
		} catch (Exception e) {
			Toast.makeText(this, "Not able to connect to server, Try again..",
					Toast.LENGTH_LONG).show();
		}
		transition = this.getIntent().getExtras().getInt(StringUtil.TRANSITION);
	}

	public boolean onTouch(View v, MotionEvent event) {
		Intent intent = new Intent(FriendScreen.this, NewFriendScreen.class);
		intent.getExtras().putInt(StringUtil.TRANSITION, transition);
		//TODO add friend id in list
		startActivity(intent);
		return false;
	}
}
