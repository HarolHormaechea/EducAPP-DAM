package com.hhg.educappclient.service;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import retrofit.ErrorHandler;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hhg.educappclient.models.AssistanceControl;
import com.hhg.educappclient.models.Course;
import com.hhg.educappclient.models.Notification;
import com.hhg.educappclient.models.UserPublicProfile;
import com.hhg.educappclient.rest.EducappAPI;
import com.hhg.educappclient.rest.SecuredRestBuilder;
import com.hhg.educappclient.rest.UnsafeHttpsClient;
import com.hhg.educappclient.service.ServiceConstants.ExtraCodes;
import com.hhg.educappclient.service.ServiceConstants.RequestTypes;
import com.hhg.educappclient.utilities.CustomDateDeserializerSerializer.CustomRetrofitCaldendarDeSerializer;
import com.hhg.educappclient.utilities.CustomDateDeserializerSerializer.CustomRetrofitGregorianCalendarSerializer;

public class NetworkOperationsService extends Service implements ErrorHandler {


	private String TAG = "NetworkOperationsService";

	private EducappAPI educappAPI;
	private String CLIENT_ID = "mobile";
	private UserPublicProfile loggedUser;
	private String loggedUsername;
	private String[] loggedUserAuthorities;
	private Collection<Course> courseList;
	
	/**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    
    public NetworkOperationsService(){}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       return START_STICKY;
    }
    
	@Override
	public IBinder onBind(Intent arg0) {
		return mMessenger.getBinder();
	}

	
	@Override
	/**
	 * Will manage the behaviour when handling errors found by the
	 * service retrofit implementations after attempting network
	 * operations.
	 */
	public Throwable handleError(RetrofitError arg0) {
		Log.d(TAG, "handleError retrofit");
		return arg0;
	}
	
	private void setUpAlarms(){
		//cancelAlarms();

		Log.d(TAG, "Setting up alarm");
		AlarmManager alarmManager = 
				(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getBaseContext(), NotificationCheckAlarm.class);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 
				PendingIntent.FLAG_UPDATE_CURRENT);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		//TODO: Remove debug
		int frequency = 1000 * 30; //30 second default value
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), frequency, alarmIntent);
	}
	
	private void cancelAlarms(){
		Log.d(TAG, "cancelling alarm");
		AlarmManager alarmManager = 
				(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getBaseContext(), NotificationCheckAlarm.class);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		//First, we have to destroy old alarms to avoid problems.
		alarmManager.cancel(alarmIntent);
	}


	/**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RequestTypes.LOGIN:
                	Log.e(TAG, "WOOOO ACTION RECEIVEED WAS LOGIN!");
    				String username = msg.getData().getString(ServiceConstants.USER_EXTRA);
                	String password = msg.getData().getString(ServiceConstants.PASSWORD_EXTRA);
                	Log.i("SVC", username);
                	Log.i("SVC", password);
                	login(msg);
        			break;
                case RequestTypes.COURSE_LIST:
                	Log.e(TAG, "WOOOO ACTION RECEIVEED WAS COURSE LIST!");
                	getCourseList(msg);
                	break;
                case RequestTypes.GET_AUTH_LIST:
                	Log.e(TAG, "WOOOO ACTION RECEIVEED WAS GET AUTH PRIVILEGES!");
                	getLoggedUserPrivileges(msg);
                	break;
                case RequestTypes.COURSE_ADD:
                	Log.e(TAG, "WOOOO ACTION RECEIVEED WAS ADD COURSE! "+
            					msg.getData().getString(ServiceConstants.COURSE_NAME_EXTRA));
                	addCourse(msg);
                	break;
                case RequestTypes.GET_USER_LIST:
                	Log.e(TAG, "WOOOO ACTION RECEIVEED WAS GET_USER_LIST!");
                	getUserList(msg);
                	break;
                case RequestTypes.GET_NEW_ASSISTANCE_CONTROL:
                	Log.e(TAG, "WOOOO ACTION RECEIVEED WAS GET_NEW_ASSISTANCE_CONTROL!");
                	getNewAssistanceControl(msg);
                	break;
                case RequestTypes.POST_ASSISTANCE_CONTROL:
                	Log.e(TAG, "WOOOO ACTION RECEIVEED WAS GET_NEW_ASSISTANCE_CONTROL!");
                	postAssistanceControl(msg);
                	break;
                case RequestTypes.PROFILE_REQUEST:
                	Log.e(TAG, "WOOOO ACTION RECEIVEED WAS PROFILE_REQUEST!");
                	requestProfile(msg);
                	break;
                case RequestTypes.NOTIFICATION_REQUEST:
                	Log.e(TAG, "WOOOO ACTION RECEIVEED WAS NOTIFICATION_REQUEST!");
                	getNotifications(msg);
                	break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
	
	private void login(Message incomingMessage){
		LoginTask task = new LoginTask(incomingMessage);
		task.execute();
	}
	
	private void addCourse(Message incomingMessage){
		AddCourseTask task = new AddCourseTask(incomingMessage);
		task.execute();
	}
	
	/**
	 * TODO: Working cache.
	 * 
	 * @param incomingMessage
	 */
	private void getCourseList(Message incomingMessage){
		if(true){
			CourseListTask task = new CourseListTask(incomingMessage);
			task.execute();
		}else{
			
		}
	}
	
	/**
	 * TODO: Working cache.
	 * 
	 * @param incomingMessage
	 */
	private void getUserList(Message incomingMessage){
		if(true){
			UserListTask task = new UserListTask(incomingMessage);
			task.execute();
		}else{
			
		}
	}
	
	
	/**
	 * SHOULD USE A CACHE! Currently using placeholder
	 * memory implementation.
	 *  
	 * @param fetchCourseList
	 */
	private void storeCourseList(Collection<Course> courseList) {
		this.courseList = courseList;
	}
	
	private void getLoggedUserPrivileges(Message incMessage) {
		Bundle data = new Bundle();
		Message outgoingMessage = Message.obtain();
		outgoingMessage.what = RequestTypes.GET_AUTH_LIST;
		outgoingMessage.setData(data);
		data.putStringArray(ServiceConstants.AUTH_LIST_EXTRA, loggedUserAuthorities);
		try {
			incMessage.replyTo.send(outgoingMessage);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	private void getNewAssistanceControl(Message inMessage){
		GetNewAssistanceControlTask task = 
				new GetNewAssistanceControlTask(inMessage);
		task.execute();
	}
	
	private void postAssistanceControl(Message inMessage){
		PostAssistanceControlTask task = 
				new PostAssistanceControlTask(inMessage);
		task.execute();
	}
	
	private void getNotifications(Message inMessage){
		if(educappAPI != null){
			GetNotificationsTask task = 
					new GetNotificationsTask(inMessage);
			task.execute();
		}
	}


	private void requestProfile(Message inMessage){
		GetUserProfileTask task = 
				new GetUserProfileTask(inMessage);
		task.execute();
	}


	public String getLoggedUsername() {
		return loggedUsername;
	}


	public void setLoggedUsername(String loggedUsername) {
		this.loggedUsername = loggedUsername;
	}

	
	
	///
	/// ASYNCTASKS
	///
	
	class LoginTask extends AsyncTask<Void, Void, Message>{
		private Messenger replyTo;
		private final String username;
		private final String password;
		
		public LoginTask(Message incomingMessage){
			username = incomingMessage.getData().getString(ServiceConstants.USER_EXTRA);
        	password = incomingMessage.getData().getString(ServiceConstants.PASSWORD_EXTRA);
        	replyTo = incomingMessage.replyTo;
		}
		@Override
		protected Message doInBackground(Void... inMessage) {
        	Log.i("SVC", username);
        	Log.i("SVC", password);
        	
			Bundle data = new Bundle();
			Message outgoingMessage = Message.obtain();
			outgoingMessage.what = RequestTypes.LOGIN;
			outgoingMessage.setData(data);
			
			try{
				GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.registerTypeAdapter(Calendar.class, new CustomRetrofitCaldendarDeSerializer());
				gsonBuilder.registerTypeAdapter(GregorianCalendar.class, new CustomRetrofitGregorianCalendarSerializer());
				
				
				educappAPI = new SecuredRestBuilder()
				.setConverter(new GsonConverter(gsonBuilder.create()))
				.setLoginEndpoint("https://192.168.111.1:8443" + EducappAPI.TOKEN_PATH)
				.setUsername(username)
				.setPassword(password)
				.setClientId(CLIENT_ID)
				.setErrorHandler(NetworkOperationsService.this)
				.setClient(new OkClient(UnsafeHttpsClient.getUnsafeOkHttpClient()))
				.setEndpoint("https://192.168.111.1:8443")
				.setLogLevel(LogLevel.FULL).build()
				.create(EducappAPI.class);
				loggedUserAuthorities = educappAPI.getLoggedUserAuthorities();
				Log.i(TAG, loggedUserAuthorities.length +"__"+ loggedUserAuthorities.toString());
				data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_OK);
				data.putStringArray(ExtraCodes.EXTRA_PRIVILEGES_LIST, loggedUserAuthorities);
				
			}catch(RetrofitError ex){
				if(ex.getMessage().contains(String.valueOf(HttpURLConnection.HTTP_UNAUTHORIZED))){
					data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_BAD_CREDENTIALS);
				}else{
					data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_CONNECTION_ERROR);
				}
			}
			return outgoingMessage;
		}

		@Override
		protected void onPostExecute(Message result) {

			try {
				loggedUsername = username;
				setUpAlarms();
				replyTo.send(result);
			
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	class CourseListTask extends AsyncTask<Void, Void, Message>{
		private Messenger replyTo;
		
		public CourseListTask(Message incomingMessage){
        	replyTo = incomingMessage.replyTo;
		}
		
		@Override
		protected Message doInBackground(Void... inMessage) {
        	
			Bundle data = new Bundle();
			Message outgoingMessage = Message.obtain();
			outgoingMessage.what = RequestTypes.COURSE_LIST;
			outgoingMessage.setData(data);
			
			try{
				Collection<Course> list = educappAPI.fetchCourseList();
				NetworkOperationsService.this.storeCourseList(list);
				Course[] courses = list.toArray(new Course[0]);
				String[] jsonTransformedItems = new String[list.size()];
				for(int i = 0; i<list.size(); i++){
					jsonTransformedItems[i] = new Gson().toJson(courses[i]);
				}
				data.putStringArray(ExtraCodes.EXTRA_COURSE_LIST, jsonTransformedItems);
				data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_OK);
			}catch(RetrofitError ex){
				if(ex.getMessage().contains(String.valueOf(HttpURLConnection.HTTP_UNAUTHORIZED))){
					data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_BAD_CREDENTIALS);
				}else{
					data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_CONNECTION_ERROR);
				}
			}
			return outgoingMessage;
		}

		@Override
		protected void onPostExecute(Message result) {

			try {
				Log.d(TAG, "Asynchronously received list of courses");
				replyTo.send(result);
				
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	class UserListTask extends AsyncTask<Void, Void, Message>{
		private Messenger replyTo;
		
		public UserListTask(Message incomingMessage){
        	replyTo = incomingMessage.replyTo;
		}
		
		@Override
		protected Message doInBackground(Void... inMessage) {
        	
			Bundle data = new Bundle();
			Message outgoingMessage = Message.obtain();
			outgoingMessage.what = RequestTypes.GET_USER_LIST;
			outgoingMessage.setData(data);
			
			try{
				Collection<UserPublicProfile> list = educappAPI.getAllProfiles();
				UserPublicProfile[] profiles = list.toArray(new UserPublicProfile[0]);
				String[] jsonTransformedItems = new String[list.size()];
				for(int i = 0; i<list.size(); i++){
					jsonTransformedItems[i] = new Gson().toJson(profiles[i]);
					Log.i(TAG, jsonTransformedItems[i]);
				}
				data.putStringArray(ExtraCodes.EXTRA_USERS_LIST, jsonTransformedItems);
				data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_OK);
			}catch(RetrofitError ex){
				if(ex.getMessage().contains(String.valueOf(HttpURLConnection.HTTP_UNAUTHORIZED))){
					data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_BAD_CREDENTIALS);
				}else{
					data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_CONNECTION_ERROR);
				}
			}
			return outgoingMessage;
		}

		@Override
		protected void onPostExecute(Message result) {

			try {
				Log.d(TAG, "Asynchronously received list of users");
				replyTo.send(result);
				
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	class AddCourseTask extends AsyncTask<Void, Void, Message>{
		private Messenger replyTo;
		private final String tag;
		private final String name;
		private final String desc;
		private final String beginDate;
		private final String endDate;
		private final int studentNumber;
		
		public AddCourseTask(Message incomingMessage){
			tag = incomingMessage.getData().getString(ServiceConstants.COURSE_TAG_EXTRA);
			name = incomingMessage.getData().getString(ServiceConstants.COURSE_NAME_EXTRA);
			Log.e("AddCourseTask", name);
			desc = incomingMessage.getData().getString(ServiceConstants.COURSE_DESC_EXTRA);
			beginDate = incomingMessage.getData().getString(ServiceConstants.COURSE_BEGINS_EXTRA);
			endDate = incomingMessage.getData().getString(ServiceConstants.COURSE_FINISHES_EXTRA);
			studentNumber = incomingMessage.getData().getInt(ServiceConstants.COURSE_MAX_STUDENTS_EXTRA);
        	replyTo = incomingMessage.replyTo;
		}
		@Override
		protected Message doInBackground(Void... inMessage) {
        	Log.i("SVC", "doInBackground addcourse "+name);
        	
			Bundle data = new Bundle();
			Message outgoingMessage = Message.obtain();
			outgoingMessage.what = RequestTypes.COURSE_ADD;
			outgoingMessage.setData(data);
			
			try{
				boolean courseGenerationResult = educappAPI.createCourse(
						tag,
						name,
						desc,
						studentNumber,
						beginDate,
						endDate);
				if (courseGenerationResult == true){
					data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_OK);
				}else{
					data.putString(ExtraCodes.OPERATION_RESULT,
							"Some details were not valid. Try again.");
				}
			}catch(RetrofitError ex){
				if(ex.getMessage().contains(String.valueOf(HttpURLConnection.HTTP_UNAUTHORIZED))){
					data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_BAD_CREDENTIALS);
				}else{
					data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_CONNECTION_ERROR);
				}
			}
			return outgoingMessage;
		}

		@Override
		protected void onPostExecute(Message result) {

			try {
				replyTo.send(result);
				NetworkOperationsService.this.loggedUsername =
						result.getData().getString(ServiceConstants.USER_EXTRA);
				NetworkOperationsService.this.loggedUserAuthorities = 
						result.getData().getStringArray(ServiceConstants.ExtraCodes.EXTRA_PRIVILEGES_LIST);
			
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	class GetNewAssistanceControlTask extends AsyncTask<Void, Void, Message>{
		private Messenger replyTo;
		private String courseTag;
		private String date;
		
		public GetNewAssistanceControlTask(Message incomingMessage){
			courseTag = incomingMessage.getData().getString(ServiceConstants.COURSE_TAG_EXTRA);
        	date = incomingMessage.getData().getString(ServiceConstants.DATE_VALUE);
        	replyTo = incomingMessage.replyTo;
		}
		
		@Override
		protected Message doInBackground(Void... inMessage) {
        	Log.i("SVC", "doInBackground GetNewAssistanceControlTask "+courseTag);
        	Bundle data = new Bundle();
			Message outgoingMessage = Message.obtain();
			outgoingMessage.what = RequestTypes.GET_NEW_ASSISTANCE_CONTROL;
			outgoingMessage.setData(data);
			
        	try
        	{
	        	Collection<AssistanceControl> list = educappAPI
	    				.getNewAssistanceControlForCourse(courseTag, date);
	        	AssistanceControl[] assistanceArray = list.toArray(new AssistanceControl[0]);
	        	if (list != null && list.size() > 0){
	        		String[] jsonTransformedItems = new String[assistanceArray.length];
					for(int i = 0; i<list.size(); i++){
						jsonTransformedItems[i] = new Gson().toJson(assistanceArray[i]);
						Log.i(TAG, jsonTransformedItems[i]);
					}
					data.putStringArray(ServiceConstants.ASSISTANCE_CONTROL_EXTRA, jsonTransformedItems);
					data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_OK);
					
				}else{
					data.putString(ExtraCodes.OPERATION_RESULT,
							"Some details were not valid. Try again.");
				}
			}catch(RetrofitError ex){
				if(ex.getMessage().contains(String.valueOf(HttpURLConnection.HTTP_UNAUTHORIZED))){
					data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_BAD_CREDENTIALS);
				}else{
					data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_CONNECTION_ERROR);
				}
			}
        	
        	return outgoingMessage;
		}
		
		@Override
		protected void onPostExecute(Message result) {

			try {
				replyTo.send(result);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	class PostAssistanceControlTask extends AsyncTask<Void, Void, Message>{
		private Messenger replyTo;
		private String[] assistanceControlGSONData;
		
		public PostAssistanceControlTask(Message incomingMessage){
			replyTo = incomingMessage.replyTo;
			assistanceControlGSONData = incomingMessage.getData()
					.getStringArray(ServiceConstants.ASSISTANCE_CONTROL_EXTRA);
		}
		
		@Override
		protected Message doInBackground(Void... inMessage) {
        	Log.i("SVC", "doInBackground PostAssistanceControlTask");
        	Bundle data = new Bundle();
			Message outgoingMessage = Message.obtain();
			outgoingMessage.what = RequestTypes.POST_ASSISTANCE_CONTROL;
			outgoingMessage.setData(data);
			
        	try
        	{
        		ArrayList<AssistanceControl> inList = new ArrayList<AssistanceControl>();
        		for(String s : assistanceControlGSONData){
        			inList.add(new Gson().fromJson(s, AssistanceControl.class));
        			AssistanceControl a = inList.get(inList.size()-1);
        			System.out.println(a.getDate()+
        					a.getEnrollmentReference().getStudent().getNif()+"@"
        					+a.getEnrollmentReference().getCourse().getCourseTag()+"__"
        					+a.getIsPresent());
        		}
	        	
        		Log.i(TAG, "Attempting to upload a control list for "+inList.size()+" students.");
        		Collection<AssistanceControl> resultList = 
        				educappAPI.addNewAssistanceControlForCourse(inList);
	        	
        		if(resultList != null && (resultList.size() == inList.size())){
        			data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_OK);					
				}else{
					data.putString(ExtraCodes.OPERATION_RESULT,
							"An error occured submitting this list. Try again.");
				}
			}catch(RetrofitError ex){
				if(ex.getMessage().contains(String.valueOf(HttpURLConnection.HTTP_UNAUTHORIZED))){
					data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_BAD_CREDENTIALS);
				}else{
					ex.printStackTrace();
					data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_CONNECTION_ERROR);
				}
			}
        	
        	return outgoingMessage;
		}
		
		@Override
		protected void onPostExecute(Message result) {

			try {
				replyTo.send(result);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	class GetUserProfileTask extends AsyncTask<Void, Void, Message>{
		private Messenger replyTo;
		private String courseTag;
		private boolean selfProfile;
		private int otherProfileId = -1;
		
		public GetUserProfileTask(Message incomingMessage){
			courseTag = incomingMessage.getData().getString(ServiceConstants.COURSE_TAG_EXTRA);
        	replyTo = incomingMessage.replyTo;
        	selfProfile = incomingMessage
        			.getData().getBoolean(ServiceConstants.USER_EXTRA);
        	if(selfProfile == false){
        		otherProfileId = incomingMessage
            			.getData().getInt(ServiceConstants.PROFILE_EXTRA);
        	}
		}
		
		@Override
		protected Message doInBackground(Void... inMessage) {
        	Log.i("SVC", "doInBackground GetNewAssistanceControlTask "+courseTag);
        	Bundle data = new Bundle();
			Message outgoingMessage = Message.obtain();
			outgoingMessage.what = RequestTypes.PROFILE_REQUEST;
			outgoingMessage.setData(data);
    		UserPublicProfile profile = null;
        	try
        	{
        		if(educappAPI != null){
        			if(selfProfile){
            			profile = educappAPI.getUserProfile(loggedUsername);
            		}else{
            			profile = educappAPI.getUserPublicProfile(otherProfileId);
            		}
        		}else{
        			this.cancel(true);
        		}
        		
        		
	        	if(profile != null){
	        		data.putString(ServiceConstants.PROFILE_EXTRA,
	        				new Gson().toJson(profile));
					data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_OK);
				}else{
					data.putString(ExtraCodes.OPERATION_RESULT,
							"Some details were not valid. Try again.");
				}
			}catch(RetrofitError ex){
				if(ex.getMessage().contains(String.valueOf(HttpURLConnection.HTTP_UNAUTHORIZED))){
					data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_BAD_CREDENTIALS);
				}else{
					data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_CONNECTION_ERROR);
				}
			}
        	
        	return outgoingMessage;
		}
		
		@Override
		protected void onPostExecute(Message result) {

			try {
				replyTo.send(result);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	class GetNotificationsTask extends AsyncTask<Void, Void, Message>{
		private Messenger replyTo;
		
		public GetNotificationsTask(Message incomingMessage){
        	replyTo = incomingMessage.replyTo;
		}
		
		@Override
		protected Message doInBackground(Void... inMessage) {
        	Log.i("SVC", "doInBackground GetNotificationsTask");
        	Bundle data = new Bundle();
			Message outgoingMessage = Message.obtain();
			outgoingMessage.what = RequestTypes.NOTIFICATION_REQUEST;
			outgoingMessage.setData(data);
			
        	try
        	{
	        	Notification[] notifArray = educappAPI
	        			.getAllNotificationsDestinedToUser()
	        			.toArray(new Notification[0]);
	        	
	        	if (notifArray != null && notifArray.length > 0){
	        		String[] jsonTransformedItems = new String[notifArray.length];
					for(int i = 0; i<notifArray.length; i++){
						jsonTransformedItems[i] = new Gson().toJson(notifArray[i]);
						Log.i(TAG, jsonTransformedItems[i]);
					}
					data.putStringArray(ServiceConstants.NOTIFICATION_EXTRA,
							jsonTransformedItems);
					data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_OK);
					
				}
			}catch(RetrofitError ex){
				if(ex.getMessage().contains(
						String.valueOf(HttpURLConnection.HTTP_UNAUTHORIZED))){
					data.putString(ExtraCodes.OPERATION_RESULT,
							ExtraCodes.STATUS_BAD_CREDENTIALS);
					
				}else if(ex.getMessage().contains(
						String.valueOf(HttpURLConnection.HTTP_NOT_FOUND))){
					data.putStringArray(ServiceConstants.NOTIFICATION_EXTRA,
							new String[0]);
					data.putString(ExtraCodes.OPERATION_RESULT, ExtraCodes.STATUS_OK);
				}else{
					data.putString(ExtraCodes.OPERATION_RESULT,
							ExtraCodes.STATUS_CONNECTION_ERROR);
				}
			}
        	
        	return outgoingMessage;
		}
		
		@Override
		protected void onPostExecute(Message result) {

			try {
				replyTo.send(result);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}
