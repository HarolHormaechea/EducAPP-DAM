package com.educapp.auth;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.provisioning.UserDetailsManager;

import com.educapp.model.User;


public class CustomizedCombinedDetailsManager implements UserDetailsService,
ClientDetailsService{
	private final ClientDetailsService clientsService;
	
	@Autowired
	private final UserDetailsManager usersManager;
	
	private final ClientDetailsUserDetailsService clientUserDetailsService;
	
	/**
	 * Adds a new user to this service.
	 * 
	 * @param clients
	 * @param users
	 */
	public void createUser(UserDetails user){
		usersManager.createUser(user);
	}
	
	public UserDetailsManager getUsersManager(){
		return usersManager;
	}

	public CustomizedCombinedDetailsManager(ClientDetailsService clients,
			UserDetailsManager users) {
		super();
		clientsService = clients;
		usersManager = users;
		clientUserDetailsService = new ClientDetailsUserDetailsService(clientsService);
	}

	@Override
	public ClientDetails loadClientByClientId(String clientId)
			throws ClientRegistrationException {
		return clientsService.loadClientByClientId(clientId);
	}
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		UserDetails user = null;
		try{
			user = usersManager.loadUserByUsername(username);
		}catch(UsernameNotFoundException e){
			user = clientUserDetailsService.loadUserByUsername(username);
		}
		return user;
	}
}
