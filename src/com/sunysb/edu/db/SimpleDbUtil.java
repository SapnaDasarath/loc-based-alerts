package com.sunysb.edu.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import android.content.Context;

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
import com.sunysb.edu.util.CryptoUtils;
import com.sunysb.edu.util.StringUtil;

public class SimpleDbUtil {

	private SimpleDbInterface dbInterface = null;

	public SimpleDbUtil() throws Exception {
		dbInterface = new SimpleDbInterface();
	}
	
	public SimpleDbUtil(Context context) throws Exception {
		dbInterface = new SimpleDbInterface(context);
	}

	public SimpleDbUtil(Context context, String userName) throws Exception {
		dbInterface = new SimpleDbInterface(context, userName);
	}

	public static String getCurrentUser() {
		return SimpleDbInterface.getCurrentUser();
	}

	public static void setCurrentUser(String user) {
		SimpleDbInterface.setCurrentUser(user);
	}

	// Domain operations

	// create a new domain
	public void createDomain(String domainName) throws Exception {
		dbInterface.getDB().createDomain(new CreateDomainRequest(domainName));
	}

	// delete an existing domain
	public void deleteDomain(String domainName) throws Exception {
		dbInterface.getDB().deleteDomain(new DeleteDomainRequest(domainName));
	}

	// get all domain names in the db
	public List<String> getDomainNames() throws Exception {
		return (List<String>) dbInterface.getDB().listDomains()
				.getDomainNames();
	}

	// to an existing domain add an item
	public void createItem(String domainName, String itemName,
			HashMap<String, String> attributes) throws Exception {
		List<ReplaceableAttribute> replaceableAttributes = new ArrayList<ReplaceableAttribute>(
				attributes.size());

		for (String attributeName : attributes.keySet()) {
			// Encrypt attribute value and store
			String val = attributes.get(attributeName);
			String encVal = CryptoUtils.getEncryptedMessage(val, dbInterface.enc_publickey);
			replaceableAttributes.add(new ReplaceableAttribute()
					.withName(attributeName).withValue(encVal)
					.withReplace(true));
		}
		dbInterface.getDB().putAttributes(
				new PutAttributesRequest(domainName, itemName,
						replaceableAttributes));
	}

	// to an existing domain add an item
	public void createItem(String domainName, String itemName,
			HashMap<String, String> attributes, String key) throws Exception {
		List<ReplaceableAttribute> replaceableAttributes = new ArrayList<ReplaceableAttribute>(
				attributes.size());

		for (String attributeName : attributes.keySet()) {
			// Encrypt attribute value and store
			String val = attributes.get(attributeName);
			String encVal = CryptoUtils.getEncryptedMessage(val, key);
			replaceableAttributes.add(new ReplaceableAttribute()
					.withName(attributeName).withValue(encVal)
					.withReplace(true));
		}
		dbInterface.getDB().putAttributes(
				new PutAttributesRequest(domainName, itemName,
						replaceableAttributes));
	}

	// get all items as part of a given domain
	public List<String> getItemNamesForDomain(String domainName)
			throws Exception {
		SelectRequest selectRequest = new SelectRequest(
				"select itemName() from `" + domainName + "`")
				.withConsistentRead(true);
		List<Item> items = (List<Item>) dbInterface.getDB()
				.select(selectRequest).getItems();

		List<String> itemNames = new ArrayList<String>();
		for (int i = 0; i < items.size(); i++) {
			itemNames.add(((Item) items.get(i)).getName());
		}
		return itemNames;
	}

	public List<String> getTasksForUser(String domainName) throws Exception {
		List<String> domain = new ArrayList<String>();
		List<String> taskidlist = getItemNamesForDomain(domainName);
		if (taskidlist != null && taskidlist.size() > 0) {
			for (String taskname : taskidlist) {
				if (!taskname.equals(StringUtil.USER_INFO)) {
					if (!taskname.startsWith(StringUtil.FRIEND_INFO)) {
						domain.add(taskname);
					}
				}
			}
		}
		return domain;
	}

	public List<String> getFriendsForUser(String domainName) throws Exception {
		List<String> domain = new ArrayList<String>();
		List<String> friendList = getItemNamesForDomain(domainName);
		if (friendList != null && friendList.size() > 0) {
			for (String taskname : friendList) {
				if (taskname.startsWith(StringUtil.FRIEND_INFO)) {
					domain.add(taskname);
				}
			}
		}
		return domain;
	}

	// get all items as part of a given query
	public List<String> getItemNamesForQuery(String query) throws Exception {
		SelectRequest selectRequest = new SelectRequest(query)
				.withConsistentRead(true);
		List<Item> items = (List<Item>) dbInterface.getDB()
				.select(selectRequest).getItems();

		List<String> itemNames = new ArrayList<String>();
		for (int i = 0; i < items.size(); i++) {
			itemNames.add(((Item) items.get(i)).getName());
		}
		return itemNames;
	}

	// to delete an item from a given domain
	public void deleteItem(String domainName, String itemName) throws Exception {
		dbInterface.getDB().deleteAttributes(
				new DeleteAttributesRequest(domainName, itemName));
	}

	// for a give item replace all attributes with new attributes
	public void updateAttributesForItem(String domainName, String itemName,
			HashMap<String, String> attributes) throws Exception {
		List<ReplaceableAttribute> replaceableAttributes = new ArrayList<ReplaceableAttribute>(
				attributes.size());

		for (String attributeName : attributes.keySet()) {
			String val = attributes.get(attributeName);
			String encVal = CryptoUtils.getEncryptedMessage(val, dbInterface.enc_publickey);
			replaceableAttributes.add(new ReplaceableAttribute()
					.withName(attributeName).withValue(encVal)
					.withReplace(true));
		}
		dbInterface.getDB().putAttributes(
				new PutAttributesRequest(domainName, itemName,
						replaceableAttributes));
	}

	// for a given domain name and item get all attributes
	public HashMap<String, String> getAttributesForItem(String domainName,
			String itemName) throws Exception {
		GetAttributesRequest getRequest = new GetAttributesRequest(domainName,
				itemName).withConsistentRead(true);
		GetAttributesResult getResult = dbInterface.getDB().getAttributes(
				getRequest);

		HashMap<String, String> attributes = new HashMap<String, String>(30);
		for (Object attribute : getResult.getAttributes()) {
			String name = ((Attribute) attribute).getName();
			String value = ((Attribute) attribute).getValue();

			String decval = CryptoUtils.getDecryptedMessage(value,
					dbInterface.enc_publickey);
			attributes.put(name, decval);
		}
		return attributes;
	}

	public List<String> getTaskAcceptedFriends(String taskid) throws Exception {
		HashMap<String, String> attr = getAttributesForItem(
				SimpleDbUtil.getCurrentUser(), taskid);
		String taskOwner = attr.get(StringUtil.TASK_FRIENDS_NAMES);
		String decval = CryptoUtils.getDecryptedMessage(taskOwner,
				dbInterface.enc_publickey);
		return getFriendsFromString(decval);
	}

	public List<String> getFriendsFromString(String taskOwner) {
		List<String> username = new ArrayList<String>();
		if (taskOwner != null) {
			StringTokenizer token = new StringTokenizer(taskOwner, ",");
			// iterate through tokens
			while (token.hasMoreTokens()) {
				username.add(token.nextToken());
			}
		}
		return username;
	}

	public String getStringFromList(List<String> names) throws Exception {
		StringBuffer sb = new StringBuffer();
		if (names != null && names.size() > 0) {
			for (String name : names) {
				sb.append(name).append(",");
			}
			return sb.toString();
		}
		return "";
	}

	// to delete an attribute in a given item in a given domain
	public void deleteItemAttribute(String domainName, String itemName,
			String attributeName) throws Exception {
		dbInterface.getDB().deleteAttributes(
				new DeleteAttributesRequest(domainName, itemName)
						.withAttributes(new Attribute[] { new Attribute()
								.withName(attributeName) }));
	}

	public boolean doesDomainExist(String username) throws Exception {
		List<String> domainNames = getDomainNames();
		if (domainNames.contains(username)) {
			return true;
		}
		return false;
	}
	
	public String getEncPublicKeyForUser(String username)
	{
		return null;
	}
	
	public void addKeyToServer(String username,String type,  String key)
	{
		
	}
}
