package com.hhg.educappclient.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.hhg.educappclient.models.AssistanceControl;
import com.hhg.educappclient.models.Course;
import com.hhg.educappclient.utilities.Constants.PRIVILEGES_DEFINITIONS;
import com.hhg.educappclient.utilities.CustomDateDeserializerSerializer;

public class ServiceMessageBuilder {
	/**
	 * Hidden constructor. This is a non-instantiable class.
	 */
	private ServiceMessageBuilder(){}
	
	public static Intent CreateIntent(Context context, 
			PRIVILEGES_DEFINITIONS privilege, 
			Bundle data){
		Intent intent = new Intent();
		intent.setClass(context, NetworkOperationsService.class);
		intent.setAction(PRIVILEGES_DEFINITIONS.getIntentString(privilege));
		return intent;
	}
	
	public static Message CreateLoginMessage(String username,
			String password, Messenger replyMessenger){
		Message message = Message.obtain();
		Bundle data = new Bundle();
		data.putString(ServiceConstants.USER_EXTRA, username);
		data.putString(ServiceConstants.PASSWORD_EXTRA, password);
		message.what = ServiceConstants.RequestTypes.LOGIN;
		message.replyTo = replyMessenger;
		message.setData(data);
		
		return message;
	}

	public static Message CreateRequestCourseListMessage(
			Messenger replyMessenger) {
		Message message = Message.obtain();
		message.what = ServiceConstants.RequestTypes.COURSE_LIST;
		message.replyTo = replyMessenger;
		return message;
	}

	public static Message createRequestLoggedUserAuthValuesMessage(
			Messenger replyMessenger) {
		Message message = Message.obtain();
		message.what = ServiceConstants.RequestTypes.GET_AUTH_LIST;
		message.replyTo = replyMessenger;
		return message;
	}
	
	public static Message createAddNewCourseMessage(
			Messenger replyMessenger,
			String tag,
			String fullName,
			String description,
			int maxStudents,
			String startDate,
			String endDate) {
		Message message = Message.obtain();
		message.what = ServiceConstants.RequestTypes.COURSE_ADD;
		message.replyTo = replyMessenger;
		Bundle data = new Bundle();
		data.putString(ServiceConstants.COURSE_TAG_EXTRA, tag);
		data.putString(ServiceConstants.COURSE_NAME_EXTRA, fullName);
		Log.e("BUILDER", fullName);
		data.putString(ServiceConstants.COURSE_DESC_EXTRA, description);
		data.putInt(ServiceConstants.COURSE_MAX_STUDENTS_EXTRA, maxStudents);
		data.putString(ServiceConstants.COURSE_BEGINS_EXTRA, startDate);
		data.putString(ServiceConstants.COURSE_FINISHES_EXTRA, endDate);
		message.setData(data);
		
		
		return message;
	}

	public static Message CreateRequestUserListMessage(Messenger replyMessenger) {
		Message message = Message.obtain();
		message.what = ServiceConstants.RequestTypes.GET_USER_LIST;
		message.replyTo = replyMessenger;
		return message;
	}

	public static Message CreateRequestNewAssistanceControl(
			Messenger replyMessenger, Course course, Date date) {
		Message message = Message.obtain();
		message.what = ServiceConstants.RequestTypes.GET_NEW_ASSISTANCE_CONTROL;
		message.replyTo = replyMessenger;
		Bundle data = new Bundle();
		data.putString(ServiceConstants.COURSE_TAG_EXTRA, course.getCourseTag());
		data.putString(ServiceConstants.DATE_VALUE, CustomDateDeserializerSerializer.dateFormatter.format(date));
		message.setData(data);
		return message;
	}
	
	public static Message CreatePostAssistanceControlMessage(
			Messenger replyMessenger, ArrayList<AssistanceControl> iterableList) {
		String[] jsonData = new String[iterableList.size()];
		for(int i = 0; i<jsonData.length; i++){
			jsonData[i] = new Gson().toJson(iterableList.get(i));
		}
		Message message = Message.obtain();
		message.what = ServiceConstants.RequestTypes.POST_ASSISTANCE_CONTROL;
		message.replyTo = replyMessenger;
		Bundle data = new Bundle();
		data.putStringArray(ServiceConstants.ASSISTANCE_CONTROL_EXTRA, jsonData);
		message.setData(data);
		return message;
	}
	
	/**
	 * Generates a new message to ask the service for the
	 * profile of another user (not the currently logged in
	 * one).
	 * 
	 * @param replyMessenger
	 * @param publicProfileId
	 * @return
	 */
	public static Message CreateRetrieveUserProfileMessage(
			Messenger replyMessenger, int publicProfileId) {
		Message message = Message.obtain();
		message.what = ServiceConstants.RequestTypes.PROFILE_REQUEST;
		message.replyTo = replyMessenger;
		Bundle data = new Bundle();
		data.putBoolean(ServiceConstants.USER_EXTRA, false);
		data.putInt(ServiceConstants.PROFILE_EXTRA, publicProfileId);
		message.setData(data);
		return message;
	}
	
	/**
	 * Generates a new message to ask the service for the
	 * profile of the currently logged in user.
	 * 
	 * @param replyMessenger
	 * @param publicProfileId
	 * @return
	 */
	public static Message CreateRetrieveUserProfileMessage(
			Messenger replyMessenger) {
		Message message = Message.obtain();
		message.what = ServiceConstants.RequestTypes.PROFILE_REQUEST;
		message.replyTo = replyMessenger;
		Bundle data = new Bundle();
		data.putBoolean(ServiceConstants.USER_EXTRA, true);
		message.setData(data);
		return message;
	}

	public static Message createRequestNotificationsMessage(
			Messenger replyMessenger) {
		Message message = Message.obtain();
		message.what = ServiceConstants.RequestTypes.NOTIFICATION_REQUEST;
		message.replyTo = replyMessenger;
		return message;
	}
}
