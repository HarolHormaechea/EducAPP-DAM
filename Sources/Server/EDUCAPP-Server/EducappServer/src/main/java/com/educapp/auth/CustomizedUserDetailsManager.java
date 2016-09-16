package com.educapp.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.educapp.controllers.UserDataManager;
import com.educapp.controllers.UserDataManager.UserCreationFailedException;
import com.educapp.repositories.UsersRepository;

/**
 * UserDetailsManager implementation. This class allows us to modify
 * the users (authentication) stored in the database: creation, 
 * password modifications, deletions, updates...
 * 
 * All operations are delegated to UserDataMager class, which
 * allows us to swap on-the-fly the database or environment we
 * use to manage users.
 *  
 * @author Harold
 *
 */
@Service
public class CustomizedUserDetailsManager implements UserDetailsManager {
	
	@Autowired
	private UsersRepository users;
	
	@Autowired
	private UserDataManager usersDataManager;
	

	@Transactional
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		return usersDataManager.loadUserByUsername(username);
	}

	
	@Transactional
	@Override
	public void createUser(UserDetails user) throws UserCreationFailedException{
		usersDataManager.createUser(user);
	}
	

	@Transactional	
	@Override
	public void updateUser(UserDetails user) {
		usersDataManager.updateUser(user);
	}


	@Transactional	
	@Override
	public void deleteUser(String username) {
		usersDataManager.deleteUser(username);
	}

	@Transactional
	@Override
	public void changePassword(String oldPassword, String newPassword) throws UsernameNotFoundException{
		usersDataManager.changePassword(oldPassword, newPassword);
	}



	@Transactional
	@Override
	public boolean userExists(String username) {
		return usersDataManager.userExists(username);
	}
}
