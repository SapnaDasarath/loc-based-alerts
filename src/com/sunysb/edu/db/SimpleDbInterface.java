package com.sunysb.edu.db;

import java.io.IOException;
import java.util.Properties;
import android.util.Log;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;

public class SimpleDbInterface {

	private static AmazonSimpleDB sdb = null;
	private static BasicAWSCredentials credentials = null;
	private static String currentUser = null;

	/**
	 * This method is called when a db instance is instantiated for the first time
	 * @param uname current user name
	 */ 
	public SimpleDbInterface(String userName) {
		currentUser = userName;
		if (credentials == null) {
			getCredentials();
		}
	}

	/**
	 * This method is called later by all other db users.
	 * @throws Exception credentials not found
	 * TODO: change exception to show more details.
	 */
	public SimpleDbInterface() throws Exception {
		if (credentials == null) {
			Log.e("LBA", "Credentials not found for DB");
			throw new Exception();
		}
	}

	/**
	 * 
	 * @return AmazonSimpleDB instance. this is a singleton
	 */
	public AmazonSimpleDB getDB() {
		return getInstance();
	}

	private static AmazonSimpleDB getInstance() {
		if (sdb == null && credentials != null) {
			sdb = new AmazonSimpleDBClient(credentials);
			sdb.setEndpoint("https://sdb.amazonaws.com:443");
		}
		return sdb;
	}

	/**
	 * 
	 * @return return the current user name as a string
	 */
	public static String getCurrentUser() {
		return currentUser;
	}
	
	public static void setCurrentUser(String user) {
		currentUser = user;
	}


	/**
	 * load the file with access key
	 * use it to authenticate with server
	 */
	private void getCredentials() {
		Properties properties = new Properties();
		try {
			properties.load(getClass().getResourceAsStream(
					"AwsCredentials.properties"));
		} catch (IOException e) {
			Log.e("LBA", "Not able to access key files");
		}

		String accessKeyId = properties.getProperty("accessKey");
		String secretKey = properties.getProperty("secretKey");

		if ((accessKeyId == null) || (accessKeyId.equals(""))|| (accessKeyId.equals("CHANGEME")) 
				|| (secretKey == null)|| (secretKey.equals("")) || (secretKey.equals("CHANGEME"))) {
			Log.e("LBA", "Aws Credentials not configured correctly.");
		} else {
			credentials = new BasicAWSCredentials(
					properties.getProperty("accessKey"),
					properties.getProperty("secretKey"));
		}
	}
}
