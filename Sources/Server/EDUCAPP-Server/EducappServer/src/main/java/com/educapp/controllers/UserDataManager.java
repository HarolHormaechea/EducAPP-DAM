package com.educapp.controllers;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.educapp.model.User;
import com.educapp.model.UserPublicProfile;
import com.educapp.repositories.UsersPublicProfilesRepository;
import com.educapp.repositories.UsersRepository;
import com.google.common.collect.Lists;

/**
 * Manager for any user-related information or operation.
 * 
 * This class is used as a dependency by both the UserDetailsManager
 * required for OAuth and any controller which requires read/write
 * access to user data.
 * 
 * This allows us several advantages:
 * 	- Simpler user management: All the required functions are implemented
 * 	  in a single class.
 *  - Redundancy prevention: A single method required by both controllers
 *    and OAuth2 will be accessible to both.
 * 
 * @author Harold Hormaechea Garcia
 *
 */
@Service
public class UserDataManager {

	@Autowired
	private UsersRepository users;
	
	@Autowired
	private UsersPublicProfilesRepository usersProfiles;
	
	
	public Collection<User> loadAllUsers(){
		return Lists.newArrayList(users.findAll());
	}
	
	@Transactional
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		System.err.println("loadUserByUsername()"+username);
		User user = users.findOne(username);
		if(user != null)
			return user;
		else
			throw new UsernameNotFoundException("User not found.");
	}

	@Transactional
	public UserPublicProfile findProfileById(int id){
		UserPublicProfile userProfile = usersProfiles.findOne(id);
		if(userProfile != null)
			return userProfile;
		else
			throw new UsernameNotFoundException("User not found.");
	}
	
	@Transactional
	public User findUserByPublicProfileId(int publicProfileId){
		UserPublicProfile profile = findProfileById(publicProfileId);
		return users.findByUserProfile(profile);
	}
	
	@Transactional
	public void createUser(UserDetails user) throws UserCreationFailedException{
		System.err.println("creating "+user.getUsername());
		User userInDB = users.findOne(user.getUsername());
		if(userInDB != null){
			System.err.println(userInDB.getUsername()+ " is in use");
			throw new UserCreationFailedException(
					"An user with the same name is already in database.");
		}else{
			usersProfiles.save(((User) user).getUserProfile());
			users.save((User) user);
		}

	}
	

	@Transactional
	public void updateUser(UserDetails user) {
		users.save((User)user);
	}


	@Transactional
	public void deleteUser(String username) {
		users.delete(username);
	}

	@Transactional
	public void changePassword(String oldPassword, String newPassword) throws UsernameNotFoundException{
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		User user = users.findOne(currentUser.getName());
		if(user != null && user.getPassword() == oldPassword){
			user.setPassword(newPassword);
			users.save(user);
		}
		else{
			throw new UsernameNotFoundException("Invalid credentials or user not found.");
		}
	}



	@Transactional
	public boolean userExists(String username) {
		return users.findOne(username) != null;
	}
	
	public class UserCreationFailedException extends AuthenticationException{
		public UserCreationFailedException(String msg) {
			super(msg);
		}
		private static final long serialVersionUID = -1052928644129770643L;
	}

	public ArrayList<UserPublicProfile> loadAllUserProfiles() {
		return Lists.newArrayList(usersProfiles.findAll());
	}
}
