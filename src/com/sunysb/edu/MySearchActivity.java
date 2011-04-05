package com.sunysb.edu;

import android.os.Bundle;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;

public class MySearchActivity extends ListActivity{
	/*protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent queryIntent = getIntent();
		final String queryAction = queryIntent.getAction();
		if (Intent.ACTION_SEARCH.equals(queryAction)) {
			String searchKeywords = queryIntent.getStringExtra(SearchManager.QUERY);
		}
	} */
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    //setContentView(R.layout.search);
	    handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
	    setIntent(intent);
	    handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	     // doMySearch(query);
	    }
	}
	
	public boolean onSearchRequested (){
		//Bundle appData = new Bundle();
	     //appData.putBoolean(SearchableActivity.JARGON, true);
	     //startSearch(null, false, appData, false);

		return true;
	}
}
