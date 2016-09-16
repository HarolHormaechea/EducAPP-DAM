package com.educapp.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class FollowerRelationship {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	//Student mentioned in this notification.
	@ManyToOne(fetch=FetchType.EAGER)
	private UserPublicProfile userFollowed;
	
	//User who will receive the notification
	@ManyToOne(fetch=FetchType.EAGER)
	private UserPublicProfile userWhoFollows;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UserPublicProfile getUserFollowed() {
		return userFollowed;
	}

	public void setUserFollowed(UserPublicProfile userFollowed) {
		this.userFollowed = userFollowed;
	}

	public UserPublicProfile getUserWhoFollows() {
		return userWhoFollows;
	}

	public void setUserWhoFollows(UserPublicProfile userWhoFollows) {
		this.userWhoFollows = userWhoFollows;
	} 
	
	
}
