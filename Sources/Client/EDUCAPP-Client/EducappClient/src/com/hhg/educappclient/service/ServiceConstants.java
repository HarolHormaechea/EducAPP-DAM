package com.hhg.educappclient.service;


public class ServiceConstants {

	public static final String DOMAIN = "com.hhg.educappclient.service";
	
	public class RequestTypes{
		public static final int LOGIN = 0;
		public static final int COURSE_LIST = 1;
		public static final int GET_AUTH_LIST = 2;
		public static final int COURSE_ADD = 3;
		public static final int GET_USER_LIST = 4;
		public static final int GET_NEW_ASSISTANCE_CONTROL = 5;
		public static final int POST_ASSISTANCE_CONTROL = 6;
		public static final int PROFILE_REQUEST = 7;
		public static final int NOTIFICATION_REQUEST = 8;
	}
	
	public class ExtraCodes{
		
		
		// Custom key for the data returned in operations
	 	// callers
	    public static final String EXTRA_PRIVILEGES_LIST =
	    		DOMAIN+".PRIVILEGES_RESULT";

		public static final String EXTRA_COURSE_LIST = 
				DOMAIN+".COURSES_RESULT";

		public static final String EXTRA_USERS_LIST = 
				DOMAIN+".USER_LIST_EXTRA";
	    
	    // Broadcasted operation results
	    public static final String OPERATION_RESULT = DOMAIN+".RESULT";
	    public static final String STATUS_CONNECTION_ERROR = 
	    		OPERATION_RESULT+".connection_error";
	    public static final String STATUS_BAD_CREDENTIALS = 
	    		OPERATION_RESULT+".bad_credentials";
	    public static final String STATUS_OK= 
	    		OPERATION_RESULT+".login_ok";

	}
	
	// Custom intent action for progress/result updates to
	// callers
    public static final String BROADCAST_LOGIN_ACTION =
        DOMAIN+".BROADCAST.login";
    public static final String BROADCAST_GET_PRIVILEGES_ACTION =
    		DOMAIN+".BROADCAST.getselfprivileges";
    
    // Custom key for the data returned in operations
	// callers
    public static final String BROADCAST_LOGIN_EXTENDED_RESULT =
    		DOMAIN+".RESULT";
    
    

    // Username key for intent data
    public static final String USER_EXTRA = 
    		DOMAIN+".USER_EXTRA";
    
    // Password key for intent data
    public static final String PASSWORD_EXTRA = 
    		DOMAIN+".PASSWORD_EXTRA";

	public static final String AUTH_LIST_EXTRA =
			DOMAIN+".AUTH_EXTRA";
	public static final String COURSE_TAG_EXTRA = 
			DOMAIN+".COURSE_TAG_EXTRA";
	public static final String COURSE_NAME_EXTRA = 
			DOMAIN+".COURSE_NAME_EXTRA";;
	public static final String COURSE_DESC_EXTRA = 
			DOMAIN+".COURSE_DESC_EXTRA";;
	public static final String COURSE_MAX_STUDENTS_EXTRA = 
			DOMAIN+".COURSE_MAXSTUDENTS_EXTRA";;
	public static final String COURSE_BEGINS_EXTRA = 
			DOMAIN+".COURSE_BEGINDATE_EXTRA";;
	public static final String COURSE_FINISHES_EXTRA = 
			DOMAIN+".COURSE_ENDATE_EXTRA";
	public static final String ASSISTANCE_CONTROL_EXTRA = 
			DOMAIN+".ASSISTANCE_CONTROL_EXTRA";
	public static final String DATE_VALUE = 
			DOMAIN+".DATE_VALUE_EXTRA";
	public static final String PROFILE_EXTRA = 
			DOMAIN+".PROFILE_EXTRA";
	public static final String NOTIFICATION_EXTRA = 
			DOMAIN+".NOTIFICATION_EXTRA";
}
