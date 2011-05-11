package com.sunysb.edu.util;

public class StringUtil {

	// This is a domain that contains name value pair with user name and id
	// This should be used for validating if user exists
	public static final String USER_INFO = "userid";
	// key with user name
	public static final String USRNAME = "name";
	// key with hashed password
	public static final String PASSWD = "password";
	public static final String EMAIL = "email";
	public static final String SENDER = "sapnadasarath@gmail.com";
	public static final String SUBJECT_FRDREQ = "New Friend request";
	public static final String SUBJECT_FRDREQ_ACC = "Friend request accepted";
	public static final String BODY_FRDREQ = "You have a new friend request. Check notifications to accept request.";

	public static final String TEMP_USER = "tempUser";
	
	public static final String SUBJECT_TASK_NOTICE = "Task Recieved";
	public static final String SUBJECT_TASK_DELETE = "Task Deleted";

	// This is a domain that contains name value pair with task info.
	public static final String TASK_INFO = "taskinfo";
	// key with task name
	public static final String TASK_NAME = "taskname";
	// key with task description
	public static final String TASK_DESCRIPTION = "taskdesc";
	public static final String TASK_ID = "taskID";
	// key with task priority
	public static final String TASK_PRIORITY = "taskpriority";
	public static final String PRIOR_LOW = "low";
	public static final String PRIOR_MED = "med";
	public static final String PRIOR_HIGH = "high";

	public static final String TASK_LAT = "latitude";
	public static final String TASK_LONG = "longitude";

	public static final String TASK_OWNER = "Owner";
	public static final String TASK_OWNER_TASK_ID = "OwnerTaskID";
	public static final String TASK_FRIENDS_NAMES = "OwnerSharedID";

	public static final String TASK_STATUS = "taskstatus";

	public static final String TASK_ACCEPTED = "accepted";
	public static final String TASK_PENDING = "taskpending";

	// This is a domain containing friend name as a list
	public static final String FRIEND_INFO = "friendinfo";

	public static final String FRIEND_NAME = "friendname";

	public static final String FRIEND_STATUS = "status";

	// key will be friend name. value will be status of friend request.
	public static final String FRIEND_PENDING = "pending";
	public static final String FRIEND_CONFIRMED = "confirmed";
	
	public static final String TRANSITION = "transition";

	public static final int EDIT = 1;
	public static final int CREATE = 2;
	public static final int NOTIFY = 3;
	public static final int DELETE = 4;

}
