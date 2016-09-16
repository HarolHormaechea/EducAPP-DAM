package com.educapp.controllers;

import java.util.Calendar;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.educapp.model.FollowerRelationship;
import com.educapp.model.Notification;
import com.educapp.model.UserPublicProfile;
import com.educapp.repositories.FollowerRelationshipsRepository;
import com.educapp.repositories.NotificationsRepository;

/**
 * Manager tasked with operations related to notifications, including
 * generating and retrieving them.
 * 
 * @author Harold Hormaechea Garcia
 *
 */
@Service
class NotificationManager {

	@Autowired
	private NotificationsRepository notificationsRepository;
	
	@Autowired
	private FollowerRelationshipsRepository followersRepository;
	
	@Autowired
	private UserDataManager userDataManager;
	
	/**
	 * Retrieves all read or unread notifications destined to a given
	 * user.
	 * 
	 * @param publicUserId
	 * @return
	 */
	public Collection<Notification> getNotificationsDestinedToUser(int publicUserId){
		UserPublicProfile userToReceive = userDataManager
				.findProfileById(publicUserId);
		
		Collection<Notification> list = notificationsRepository
				.findByUserToReceiveNotification(userToReceive);
		
		return list;
	}
	
	
	/**
	 * Adds a new follower relationship between the provided
	 * users.
	 * 
	 * @param userPublicProfileIdFollower
	 * @param userPublicProfileIdToFollow
	 * @return
	 */
	public FollowerRelationship addFollower(int userPublicProfileIdFollower,
			int userPublicProfileIdToFollow){
		UserPublicProfile follower = userDataManager
				.findProfileById(userPublicProfileIdFollower);
		UserPublicProfile userFollowed = userDataManager
				.findProfileById(userPublicProfileIdToFollow);
		
		FollowerRelationship rel = new FollowerRelationship();
		rel.setUserFollowed(userFollowed);
		rel.setUserWhoFollows(follower);
		return followersRepository.save(rel);
	}
	
	/**
	 * Generates an alert for all the users registered to receive
	 * them from the user passed as parameter with the given text.
	 * 
	 * @param userWhoTriggersAlertpublicUserId the UserPublicProfile.id
	 * 						value of the user who 'causes' the alert to
	 * 						be generated (usually an student)
	 * @param userWhoCreatesAlert the UserPublicProfile.id
	 * 						value of the user who creates the alert
	 * 						(usually a teacher)
	 * @param message
	 * @return
	 */
	public void generateNewNotification(
			int userWhoTriggersAlertpublicUserId,
			int userWhoCreatesAlert,
			String message,
			Calendar date){
		UserPublicProfile causingUser = userDataManager
				.findProfileById(userWhoTriggersAlertpublicUserId);
		UserPublicProfile createdBy = userDataManager
				.findProfileById(userWhoCreatesAlert);
		Collection<FollowerRelationship> followers =
				followersRepository.findByUserFollowed(causingUser);
		
		for(FollowerRelationship f : followers){
			Notification n = new Notification();
			n.setCausingUser(causingUser);
			n.setUserToReceiveNotification(f.getUserWhoFollows());
			n.setCreatedBy(createdBy);
			n.setText(message);
			n.setDate(date);
			notificationsRepository.save(n);
		}
	}
}
