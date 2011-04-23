package com.sunysb.edu.util;

public class StringUtil {

	//This is a domain that contains name value pair with user name and id
	//This should be used for validating if user exists
	public static final String USER_INFO = "userid";
	//key with user name
	public static final String USRNAME = "name";
	//key with hashed password
	public static final String PASSWD = "password";
	
	//This is a domain that contains name value pair with task info.
	public static final String TASK_INFO = "taskinfo";
	//key with task name
	public static final String TASK_NAME = "taskname";
	//key with task description
	public static final String TASK_DESCRIPTION = "taskdesc";
	//key with task priority
	public static final String TASK_PRIORITY = "taskpriority";
	public static final String PRIOR_LOW = "low";
	public static final String PRIOR_MED = "med";
	public static final String PRIOR_HIGH = "high";
	
	public static final String TASK_LAT = "latitude";
	public static final String TASK_LONG = "longitude";
	
	public static final String TASK_OWNER = "Owner";
	public static final String TASK_OWNER_ID = "OwnerTaskID";
	public static final String TASK_SHARED_ID = "OwnerSharedID";
	
	//This is a domain containing friend name as a list
	public static final String FRIEND_INFO = "friendinfo";
	
	public static final String FRIEND_NAME = "friendname";
	
	public static final String FRIEND_STATUS = "status";
	
	//key will be friend name. value will be status of friend request.
	public static final String FRIEND_PENDING = "pending";
	public static final String FRIEND_CONFIRMED = "confirmed";


}
