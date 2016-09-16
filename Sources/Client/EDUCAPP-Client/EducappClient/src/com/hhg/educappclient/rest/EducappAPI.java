package com.hhg.educappclient.rest;

import java.util.Collection;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

import com.hhg.educappclient.models.AssistanceControl;
import com.hhg.educappclient.models.Course;
import com.hhg.educappclient.models.Enrollment;
import com.hhg.educappclient.models.Notification;
import com.hhg.educappclient.models.UserPublicProfile;

public interface EducappAPI {
	//
	//
	//
	//REST parameters
	
		//User related
	public static final String USER_PUBLIC_ID = "usr";
	public static final String PASSWORD = "pwd";
	public static final String FIRST_NAME = "fname";
	public static final String LAST_NAME = "lname";
	public static final String NIF = "nif";
	public static final String DESCRIPTION = "description";
	public static final String COURSE_ID = "course_id";
	public static final String EXAM_ID = "exam_id";
	public static final String USER_LEVEL = "user_level";
	
		//Course related
	public static final String COURSE_TAG = "ctag";
	public static final String COURSE_NAME = "cfname";
	public static final String COURSE_DESC = "cdesc";
	public static final String COURSE_MAX_ATT = "cmaxatt";
	public static final String COURSE_DATE_BEGIN = "cbegin";
	public static final String COURSE_DATE_END = "cend";
	
		//Assistance control related
	public static final String DATE = "date";
	
		//Notifications
	public static final String NOTIFICATION_ID = "notid";
	
		
	//
	//
	//
	//REST path url's
	public static final String RESOURCE_BASE = "/educapp";
	
	public static final String TOKEN_PATH = "/oauth/token";
	public static final String SIGN_UP = RESOURCE_BASE+"/signup"+"/"
										+"{"+USER_PUBLIC_ID + "}/"
										+"{"+PASSWORD + "}/"
										+"{"+FIRST_NAME + "}/"
										+"{"+LAST_NAME + "}/"
										+"{"+NIF + "}/"
										+"{"+USER_LEVEL+"}";
	public static final String GET_PROFILES = RESOURCE_BASE+"/profile/";
	
	public static final String GET_PROFILES_SINGLE = GET_PROFILES
			+"{"+USER_PUBLIC_ID+"}";
	
	public static final String GET_PUBLIC_PROFILE = GET_PROFILES
			+"/public/{"+USER_PUBLIC_ID+"}";
	
	public static final String POST_USER_PROFILE = RESOURCE_BASE+"/profile/post/"
										+"{"+DESCRIPTION+"}";

	
	public static final String LOGGED_USER_GET_AUTHORITIES 
								= RESOURCE_BASE+"/get_authorities";
	
	
	public static final String COURSE_LIST = RESOURCE_BASE+"/courses";
	public static final String COURSE_CREATE = COURSE_LIST +"/add/"
										+"{" + COURSE_TAG + "}/"
										+"{" + COURSE_NAME + "}/"
										+"{" + COURSE_MAX_ATT + "}/"
										+"{" + COURSE_DATE_BEGIN + "}/"
										+"{" + COURSE_DATE_END + "}";
	public static final String COURSE_MODIFY = COURSE_LIST +"/courses/"
			+"{" + COURSE_TAG + "}/"
			+"modify/"
			+"{" + COURSE_NAME + "}/"
			+"{" + COURSE_MAX_ATT + "}/"
			+"{" + COURSE_DATE_BEGIN + "}/"
			+"{" + COURSE_DATE_END + "}";
	
	public static final String COURSE_DATA = COURSE_LIST + "/{"+COURSE_TAG+"}";
	public static final String COURSE_ADD_TEACHER = COURSE_DATA
													+"/teachers/"
													+"{"+USER_PUBLIC_ID+"}"
													+"/add";
	public static final String COURSE_REMOVE_TEACHER = COURSE_DATA
													+"/teachers/"
													+"{"+USER_PUBLIC_ID+"}"
													+"/remove";
	
	public static final String COURSE_ADD_STUDENT = COURSE_DATA 
													+ "/students/"
													+ "{"+USER_PUBLIC_ID+"}"
													+"/add";
	
	public static final String COURSE_REMOVE_STUDENT = COURSE_DATA 
													+ "/students/"
													+ "{"+USER_PUBLIC_ID+"}"
													+"/remove";
	
	public static final String ASSISTANCE_CONTROL = RESOURCE_BASE 
													+ "/assistance";
	
	public static final String ASSISTANCE_CONTROL_GET_EMPTY_LIST = ASSISTANCE_CONTROL 
													+ "/retrieve_unfilled/"
													+ "{"+COURSE_TAG+"}/"
													+ "{"+DATE+"}";
	
	public static final String ASSISTANCE_CONTROL_ADD_LIST = ASSISTANCE_CONTROL 
													+ "/upload";
	
	public static final String NOTIFICATIONS = RESOURCE_BASE + "/notifications";
	
	
	//
	//
	//
	//Client REST request methods
	@GET(LOGGED_USER_GET_AUTHORITIES)
	public String[] getLoggedUserAuthorities();
	
	@POST(SIGN_UP)
	public boolean signUp( @Path(USER_PUBLIC_ID) String username,
						@Path(PASSWORD) String password,
						@Path(FIRST_NAME) String firstName,
						@Path(LAST_NAME) String lastName,
						@Path(NIF) String nif,
						@Path(USER_LEVEL) DEFAULT_USER_LEVELS userLevel);
	
	@GET(GET_PROFILES_SINGLE)
	public UserPublicProfile getUserProfile(@Path(USER_PUBLIC_ID) String username);
	
	@GET(GET_PROFILES_SINGLE)
	public UserPublicProfile getUserPublicProfile(@Path(USER_PUBLIC_ID) int userPublicId);
	
	@GET(GET_PROFILES)
	public Collection<UserPublicProfile> getAllProfiles();
	
	@POST(POST_USER_PROFILE)
	public boolean postUserProfile(@Path(DESCRIPTION) String newProfileDescription);
	
	@POST(COURSE_CREATE)
	public boolean createCourse(@Path(COURSE_TAG) String courseTag,
						@Path(COURSE_NAME) String courseName,
						@Body String courseDesc,
						@Path(COURSE_MAX_ATT) int courseMaxStudents,
						@Path(COURSE_DATE_BEGIN) String beginDate,
						@Path(COURSE_DATE_END) String endDate
						);
	
	@POST(COURSE_MODIFY)
	public Course modifyCourse(@Path(COURSE_TAG) String courseTag,
						@Path(COURSE_NAME) String courseName,
						@Body String courseDesc,
						@Path(COURSE_MAX_ATT) int courseMaxStudents,
						@Path(COURSE_DATE_BEGIN) String beginDate,
						@Path(COURSE_DATE_END) String endDate
						);
	
	@GET(COURSE_LIST)
	public Collection<Course> fetchCourseList();
	
	@GET(COURSE_DATA)
	public Course getCourseInformation();
	
	@POST(COURSE_ADD_TEACHER)
	public boolean assignTeacherToCourse(
						@Path(USER_PUBLIC_ID) int teacherPublicProfileId,
						@Path(COURSE_TAG) String courseTag);
	
	@POST(COURSE_REMOVE_TEACHER)
	public boolean removeTeacherFromCourse(
			@Path(USER_PUBLIC_ID) int teacherPublicProfileId,
			@Path(COURSE_TAG) String courseTag);
	
	@POST(COURSE_ADD_STUDENT)
	public Enrollment assignStudentToCourse(
						@Path(USER_PUBLIC_ID) int studentPublicProfileId,
						@Path(COURSE_TAG) String courseTag);
	
	@POST(COURSE_REMOVE_STUDENT)
	public boolean removeStudentFromCourse(
			@Path(USER_PUBLIC_ID) int studentPublicProfileId,
			@Path(COURSE_TAG) String courseTag);
	
	@GET(ASSISTANCE_CONTROL_GET_EMPTY_LIST)
	public Collection<AssistanceControl> getNewAssistanceControlForCourse(
			@Path(COURSE_TAG) String courseTag,
			@Path(DATE)String date);
	
	@POST(ASSISTANCE_CONTROL_ADD_LIST)
	public Collection<AssistanceControl> addNewAssistanceControlForCourse(
			@Body Collection<AssistanceControl> controlList);
	
	@GET(NOTIFICATIONS)
	public Collection<Notification> getAllNotificationsDestinedToUser();
	
	
	
	//
	//
	//
	//User privileges list

	public static final String AUTH_COURSES_LIST = "AUTH_COURSES_LIST";
	public static final String AUTH_COURSES_JOIN = "AUTH_COURSES_JOIN";
	public static final String AUTH_COURSES_CREATE = "AUTH_COURSES_CREATE";
	public static final String AUTH_USERS_LIST = "AUTH_USERS_LIST";
	public static final String AUTH_CREATE_ENROLLMENTS = "AUTH_CREATE_ENROLLMENTS";
	public static final String AUTH_CREATE_ASSISTANCE_CONTROL = "AUTH_CREATE_ASSISTANCE_CONTROL";
	public static final String AUTH_CREATE_EXAMS = "AUTH_CREATE_EXAMS";
	public static final String AUTH_RETRIEVE_SELF_ALERTS = "AUTH_RETRIEVE_SELF_ALERTS";
	public static final String AUTH_RETRIEVE_OTHERS_PROFILE = "AUTH_RETRIEVE_OTHERS_PROFILE";
	public static final String AUTH_ALTER_SELF_PROFILE = "AUTH_ALTER_SELF_PROFILE";
	public static final String AUTH_RETRIEVE_SELF_EXAMS = "AUTH_RETRIEVE_SELF_EXAMS";
	public static final String AUTH_ALTER_LEGAL_TUTORS = "AUTH_ALTER_LEGAL_TUTORS";
	
	//
	//
	//
	//Standardized error messages
	public static final int HTTP_ERROR_INVALID_PARAMETER = 422; //WebDAV RFC9418
	public static final String ERROR_SIGNUP_INVALID_USERNAME = "SGN_INV_USR";
	public static final String ERROR_SIGNUP_INVALID_PASSWORD = "SGN_INV_PWD";
	public static final String ERROR_SIGNUP_USER_ALREADY_EXISTS = "SGN_USR_DUPLICATED";
	
	//Basic User levels aliases
	//Should be a list of the default privileges assigned to the basic
	//user types by default. On the other hand, nothing should disallow
	//the assignment of more privileges to an user above his level. This
	//is only a handy way of accessing what operations are certain user
	//levels allowed without too much hassle.
	public enum DEFAULT_USER_LEVELS{
		LEGAL_TUTOR(EducappAPI.DEFAULT_USER_LEVEL_LEGAL_TUTOR),
		STUDENT(EducappAPI.DEFAULT_USER_LEVEL_STUDENT),
		TEACHER(EducappAPI.DEFAULT_USER_LEVEL_TEACHER),
		MANAGER(EducappAPI.DEFAULT_USER_LEVEL_MANAGER);
		
		private String[] value;
		
		private DEFAULT_USER_LEVELS(String[] value){
			this.value = value;
		}
		
		public String[] getValue(){
			return value;
		}
	}
	public static final String[] DEFAULT_USER_LEVEL_LEGAL_TUTOR={
		AUTH_ALTER_SELF_PROFILE,
		AUTH_RETRIEVE_OTHERS_PROFILE,
		AUTH_RETRIEVE_SELF_ALERTS
		};
	
	public static final String[] DEFAULT_USER_LEVEL_STUDENT ={
		AUTH_USERS_LIST,
		AUTH_COURSES_LIST,
		AUTH_COURSES_JOIN,
		AUTH_RETRIEVE_SELF_ALERTS,
		AUTH_RETRIEVE_OTHERS_PROFILE,
		AUTH_ALTER_SELF_PROFILE,
		AUTH_RETRIEVE_SELF_EXAMS};
	
	public static final String[] DEFAULT_USER_LEVEL_TEACHER = {
		AUTH_USERS_LIST,
		AUTH_COURSES_LIST,
		AUTH_RETRIEVE_SELF_ALERTS,
		AUTH_RETRIEVE_OTHERS_PROFILE,
		AUTH_ALTER_SELF_PROFILE,
		AUTH_CREATE_EXAMS,
		AUTH_CREATE_ASSISTANCE_CONTROL
		};
	
	public static final String[] DEFAULT_USER_LEVEL_MANAGER = {
		AUTH_USERS_LIST,
		AUTH_COURSES_LIST,
		AUTH_RETRIEVE_SELF_ALERTS,
		AUTH_RETRIEVE_OTHERS_PROFILE,
		AUTH_ALTER_SELF_PROFILE,
		AUTH_COURSES_CREATE,
		AUTH_CREATE_ENROLLMENTS,
		AUTH_ALTER_LEGAL_TUTORS
		};

	
}
