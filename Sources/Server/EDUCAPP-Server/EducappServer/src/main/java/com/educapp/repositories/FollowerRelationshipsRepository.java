package com.educapp.repositories;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.educapp.model.FollowerRelationship;
import com.educapp.model.UserPublicProfile;

@Repository
public interface FollowerRelationshipsRepository extends 
	CrudRepository<FollowerRelationship, Integer>{
	public FollowerRelationship findOne(Integer id);
	public Collection<FollowerRelationship> findByUserFollowed(
			UserPublicProfile userFollowed);
	public Collection<FollowerRelationship> findByUserWhoFollows(
			UserPublicProfile userWhoFollows);
}
