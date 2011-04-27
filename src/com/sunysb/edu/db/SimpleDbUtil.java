package com.sunysb.edu.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

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
import com.sunysb.edu.util.StringUtil;

public class SimpleDbUtil {

	private SimpleDbInterface dbInterface = null;

	public SimpleDbUtil() throws Exception {
		dbInterface = new SimpleDbInterface();
	}

	public SimpleDbUtil(String userName) throws Exception {
		dbInterface = new SimpleDbInterface(userName);
	}

	public static String getCurrentUser() {
		return SimpleDbInterface.getCurrentUser();
	}

	public static void setCurrentUser(String user) {
		SimpleDbInterface.setCurrentUser(user);
	}

	// Domain operations

	// create a new domain
	public void createDomain(String domainName) {
		dbInterface.getDB().createDomain(new CreateDomainRequest(domainName));
	}

	// delete an existing domain
	public void deleteDomain(String domainName) {
		dbInterface.getDB().deleteDomain(new DeleteDomainRequest(domainName));
	}

	// get all domain names in the db
	public List<String> getDomainNames() {
		return dbInterface.getDB().listDomains().getDomainNames();
	}

	// to an existing domain add an item
	public void createItem(String domainName, String itemName,
			HashMap<String, String> attributes) {
		List<ReplaceableAttribute> replaceableAttributes = new ArrayList<ReplaceableAttribute>(
				attributes.size());

		for (String attributeName : attributes.keySet()) {
			replaceableAttributes
					.add(new ReplaceableAttribute().withName(attributeName)
							.withValue(attributes.get(attributeName))
							.withReplace(true));
		}
		dbInterface.getDB().putAttributes(
				new PutAttributesRequest(domainName, itemName,
						replaceableAttributes));
	}

	// get all items as part of a given domain
	public List<String> getItemNamesForDomain(String domainName) {
		SelectRequest selectRequest = new SelectRequest(
				"select itemName() from `" + domainName + "`")
				.withConsistentRead(true);
		List<Item> items = dbInterface.getDB().select(selectRequest).getItems();

		List<String> itemNames = new ArrayList<String>();
		for (int i = 0; i < items.size(); i++) {
			itemNames.add(((Item) items.get(i)).getName());
		}
		return itemNames;
	}

	public List<String> getTasksForUser(String domainName) {
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

	public List<String> getFriendsForUser(String domainName) {
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
	public List<String> getItemNamesForQuery(String query) {
		SelectRequest selectRequest = new SelectRequest()
				.withConsistentRead(true);
		List<Item> items = dbInterface.getDB().select(selectRequest).getItems();

		List<String> itemNames = new ArrayList<String>();
		for (int i = 0; i < items.size(); i++) {
			itemNames.add(((Item) items.get(i)).getName());
		}
		return itemNames;
	}

	// to delete an item from a given domain
	public void deleteItem(String domainName, String itemName) {
		dbInterface.getDB().deleteAttributes(
				new DeleteAttributesRequest(domainName, itemName));
	}

	// to an existing domain and item add key value pairs
	public void createAttributeForItem(String domainName, String itemName,
			String attributeName, String attributeValue) {
		List<ReplaceableAttribute> attributes = new ArrayList<ReplaceableAttribute>();
		attributes.add(new ReplaceableAttribute().withName(attributeName)
				.withValue(attributeValue).withReplace(true));
		dbInterface.getDB().putAttributes(
				new PutAttributesRequest(domainName, itemName, attributes));
	}

	// for a give item replace all attributes with new attributes
	public void updateAttributesForItem(String domainName, String itemName,
			HashMap<String, String> attributes) {
		List<ReplaceableAttribute> replaceableAttributes = new ArrayList<ReplaceableAttribute>(
				attributes.size());

		for (String attributeName : attributes.keySet()) {
			replaceableAttributes
					.add(new ReplaceableAttribute().withName(attributeName)
							.withValue(attributes.get(attributeName))
							.withReplace(true));
		}
		dbInterface.getDB().putAttributes(
				new PutAttributesRequest(domainName, itemName,
						replaceableAttributes));
	}

	// for a given domain name and item get all attributes
	public HashMap<String, String> getAttributesForItem(String domainName,
			String itemName) {
		GetAttributesRequest getRequest = new GetAttributesRequest(domainName,
				itemName).withConsistentRead(true);
		GetAttributesResult getResult = dbInterface.getDB().getAttributes(
				getRequest);

		HashMap<String, String> attributes = new HashMap<String, String>(30);
		for (Object attribute : getResult.getAttributes()) {
			String name = ((Attribute) attribute).getName();
			String value = ((Attribute) attribute).getValue();
			attributes.put(name, value);
		}
		return attributes;
	}

	public List<String> getTaskAcceptedFriends(String taskid) {
		HashMap<String, String> attr = getAttributesForItem(
				SimpleDbUtil.getCurrentUser(), taskid);
		String taskOwner = attr.get(StringUtil.TASK_FRIENDS_NAMES);
		return getFriendsFromString(taskOwner);
	}

	public List<String> getFriendsFromString(String taskOwner) {
		List<String> username = new ArrayList<String>();
		StringTokenizer token = new StringTokenizer(taskOwner, ",");
		// iterate through tokens
		while (token.hasMoreTokens()) {
			username.add(token.nextToken());
		}
		return username;
	}

	public String getStringFromList(List<String> names) {
		StringBuffer sb = new StringBuffer();
		for (String name : names) {
			sb.append(name).append(",");
		}
		return sb.toString();
	}

	// to delete an attribute in a given item in a given domain
	public void deleteItemAttribute(String domainName, String itemName,
			String attributeName) {
		dbInterface.getDB().deleteAttributes(
				new DeleteAttributesRequest(domainName, itemName)
						.withAttributes(new Attribute[] { new Attribute()
								.withName(attributeName) }));
	}

	public boolean doesDomainExist(String username) {
		List<String> domainNames = getDomainNames();
		if (domainNames.contains(username)) {
			return true;
		}
		return false;
	}
}
