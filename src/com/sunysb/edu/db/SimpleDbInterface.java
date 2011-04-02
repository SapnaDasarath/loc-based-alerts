package com.sunysb.edu.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.amazonaws.services.simpledb.model.GetAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesResult;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.SelectRequest;

public class SimpleDbInterface {
	
	private static AmazonSimpleDB sdb = null;
	public static BasicAWSCredentials credentials = null;
	
	public SimpleDbInterface()
	{
		getCredentials();
	}
	
	public AmazonSimpleDB getDB()
	{
		return getInstance();
	}
	
	private static AmazonSimpleDB getInstance() {

        if ( sdb == null && credentials != null) {
		    sdb = new AmazonSimpleDBClient(credentials);
            sdb.setEndpoint( "https://sdb.amazonaws.com:443" );  		
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
     
    public static void createDomain( String domainName ) {
		getInstance().createDomain( new CreateDomainRequest( domainName ) );
	}
		
	public static void deleteDomain( String domainName ) {
		getInstance().deleteDomain( new DeleteDomainRequest( domainName ) );
	}

	public static void createItem( String domainName, String itemName ) {
		List<ReplaceableAttribute> attributes = new ArrayList<ReplaceableAttribute>(1);
		attributes.add( new ReplaceableAttribute().withName( "Name").withValue( "Value") );
		getInstance().putAttributes( new PutAttributesRequest( domainName, itemName, attributes ) );
	}

	public static void createAttributeForItem( String domainName, String itemName, String attributeName, String attributeValue ) {
		List<ReplaceableAttribute> attributes = new ArrayList<ReplaceableAttribute>(1);
		attributes.add( new ReplaceableAttribute().withName( attributeName ).withValue( attributeValue ).withReplace( true ) );
		getInstance().putAttributes( new PutAttributesRequest( domainName, itemName, attributes ) );
	}

	public static String[] getItemNamesForDomain( String domainName ) {
		SelectRequest selectRequest = new SelectRequest( "select itemName() from `" + domainName + "`" ).withConsistentRead( true );
		List items = getInstance().select( selectRequest ).getItems();	
		
		String[] itemNames = new String[ items.size() ];
		for ( int i = 0; i < items.size(); i++ ) {
			itemNames[ i ] = ((Item)items.get( i )).getName();
		}
		
		return itemNames;
	}

	public static HashMap<String,String> getAttributesForItem( String domainName, String itemName ) {
		GetAttributesRequest getRequest = new GetAttributesRequest( domainName, itemName ).withConsistentRead( true );
		GetAttributesResult getResult = getInstance().getAttributes( getRequest );	
		
		HashMap<String,String> attributes = new HashMap<String,String>(30);
		for ( Object attribute : getResult.getAttributes() ) {
			String name = ((Attribute)attribute).getName();
			String value = ((Attribute)attribute).getValue();
			
			attributes.put(  name, value );
		}

		return attributes;
	}
	
	public static void updateAttributesForItem( String domainName, String itemName, HashMap<String,String> attributes ) {
		List<ReplaceableAttribute> replaceableAttributes = new ArrayList<ReplaceableAttribute>( attributes.size() ); 
		
		for ( String attributeName : attributes.keySet() ) {
			replaceableAttributes.add( new ReplaceableAttribute().withName( attributeName ).withValue( attributes.get( attributeName ) ).withReplace( true ) );
		}

		getInstance().putAttributes( new PutAttributesRequest( domainName, itemName, replaceableAttributes ) );
	}

	public static void deleteItem( String domainName, String itemName ) {
		getInstance().deleteAttributes( new DeleteAttributesRequest( domainName, itemName ) );
	}
	
	public static void deleteItemAttribute( String domainName, String itemName, String attributeName ) {
		getInstance().deleteAttributes(  new DeleteAttributesRequest( domainName, itemName ).withAttributes( new Attribute[] { new Attribute().withName( attributeName ) } ) );
	}
}
