package com.educapp.model;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.educapp.utilities.CustomDateDeserializerSerializer.CustomDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	//Student mentioned in this notification.
	@ManyToOne(fetch=FetchType.EAGER)
	private UserPublicProfile causingUser;
	
	//User who will receive the notification
	@ManyToOne(fetch=FetchType.EAGER)
	private UserPublicProfile userToReceiveNotification; 
	
	//The teacher who generates it.
	@ManyToOne(fetch=FetchType.EAGER)
	private UserPublicProfile createdBy; 
	
	@Temporal(TemporalType.DATE)
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
