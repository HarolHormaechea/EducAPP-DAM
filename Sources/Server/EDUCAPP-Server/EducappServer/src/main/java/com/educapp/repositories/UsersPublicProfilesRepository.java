package com.educapp.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.educapp.model.UserPublicProfile;

/**
 * Interface repository to be used with JPA.
 * 
 * http://docs.spring.io/spring-data/data-commons/docs/1.6.1.RELEASE/reference/html/repositories.html
 * 
 * 
 * @author Harold
 *
 */
@Repository
public interface UsersPublicProfilesRepository extends CrudRepository<UserPublicProfile, Integer> {
	public UserPublicProfile findOne(Integer id);
}
