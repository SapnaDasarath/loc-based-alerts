package com.sunysb.edu.ui.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.sunysb.edu.R;

public class FriendListScreen extends Activity{
	
	private Button addFriendButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.friendlist);
		
		addFriendButton = (Button) findViewById(R.id.add_Friend);

		//TODO get friendlist for a user and set text as friend name
        TableLayout table = (TableLayout)findViewById(R.id.friendTableList);
        for(int i = 0; i < 3; i++)
        {
        	  TableRow tr = new TableRow(this);
              tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
              TextView textview = new TextView(this);
              textview.setText("Hello");
              textview.setTextColor(Color.BLACK);
              tr.addView(textview);
              table.addView(tr,new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        }
        addFriendButton.setOnClickListener(new View.OnClickListener(){
        	@Override
			public void onClick(View v){
        		startActivity(new Intent(FriendListScreen.this, NewFriendScreen.class));
        	}
        });
        
	}
}

