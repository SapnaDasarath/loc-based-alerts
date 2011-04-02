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
	private static String userName = null;
	private static String currentUser = null;
	
	//this method is called when a db instance
	//is instantiated for the first time
	public SimpleDbInterface(String userName)
	{
		this.userName = userName;
		if(credentials == null)
		{
			getCredentials();
		}
		//if credentials is still null error
	}
	
	//this method is called later by all accessors
	public SimpleDbInterface()
	{

	}
	
	public AmazonSimpleDB getDB()
	{
		return getInstance();
	}
	
	public static String getCurrentUser()
	{
		return currentUser;
	}
	
	private static AmazonSimpleDB getInstance() {
        if ( sdb == null && credentials != null) 
        {
		    sdb = new AmazonSimpleDBClient(credentials);
            sdb.setEndpoint( "https://sdb.amazonaws.com:443" );  	
            currentUser = userName;
        }
        return sdb;
	}
	
    private  void getCredentials() {
    	 Properties properties = new Properties();
         try {
			properties.load( getClass().getResourceAsStream( "AwsCredentials.properties" ) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
         String accessKeyId = properties.getProperty( "accessKey" );
         String secretKey = properties.getProperty( "secretKey" );
         
         if ( ( accessKeyId == null ) || ( accessKeyId.equals( "" ) ) ||
         	 ( accessKeyId.equals( "CHANGEME" ) ) ||( secretKey == null )   || 
              ( secretKey.equals( "" ) ) || ( secretKey.equals( "CHANGEME" ) ) ) {
             Log.e( "AWS", "Aws Credentials not configured correctly." );                                    
         } else {
         credentials = new BasicAWSCredentials( properties.getProperty( "accessKey" ), properties.getProperty( "secretKey" ) );
         }
    }
}
