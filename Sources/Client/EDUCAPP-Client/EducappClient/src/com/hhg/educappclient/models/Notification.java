package com.hhg.educappclient.models;

import java.util.Calendar;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hhg.educappclient.utilities.CustomDateDeserializerSerializer.CustomDateSerializer;

public class Notification {
	private int id;
	
	//Student mentioned in this notification.
	private UserPublicProfile causingUser;
	
	//User who will receive the notification
	private UserPublicProfile userToReceiveNotification; 
	
	//The teacher who generates it.
	private UserPublicProfile createdBy; 
	
	@JsonSerialize(using = CustomDateSerializer.class)
	private Calendar date;
	
	//Notification text.
	private String text;
	
	private boolean isRead;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UserPublicProfile getCausingUser() {
		return causingUser;
	}

	public void setCausingUser(UserPublicProfile causingUser) {
		this.causingUser = causingUser;
	}

	public UserPublicProfile getUserToReceiveNotification() {
		return userToReceiveNotification;
	}

	public void setUserToReceiveNotification(
			UserPublicProfile userToReceiveNotification) {
		this.userToReceiveNotification = userToReceiveNotification;
	}

	public UserPublicProfile getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserPublicProfile createdBy) {
		this.createdBy = createdBy;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	
	

}
