package com.educapp.repositories;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.educapp.model.Notification;
import com.educapp.model.UserPublicProfile;

@Repository
public interface NotificationsRepository extends CrudRepository<Notification, Integer>{
	public Notification getOne(Integer id);
	public Collection<Notification> findByCausingUser(UserPublicProfile causingUser);
	public Collection<Notification> findByUserToReceiveNotification(UserPublicProfile userToReceive);
	public Collection<Notification> findByCreatedBy(UserPublicProfile creatorUser);
	
}
