package com.educapp.model;

import java.util.Collection;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * User definition class, for authentication purposes through OAuth2, and other
 * uses.
 * 
 * TODO: Add NIF validation TODO: Implement expiration and account locking
 * 
 * @author Harold
 *
 */
@Entity
public class User implements UserDetails {
	private static final long serialVersionUID = -4506626412347152132L;

	@Id
	private String username;

	@OneToOne(fetch = FetchType.EAGER)
	private UserPublicProfile userProfile;

	// We do NOT want this field to be sent through network. Do we?
	@JsonIgnore
	private String password;

	@OneToOne(fetch = FetchType.EAGER)
	private ContactDetails contactDetails;
	
	@JsonIgnore
	@ElementCollection(fetch = FetchType.EAGER)
	private Collection<GrantedAuthority> authorities;

	/**
	 * Private default constructor.
	 * 
	 * Build users through the build() method instead to guarantee integrity.
	 */
	private User() {
	}

	public static User build(String username, String password,
			String firstName, String lastName, String nif, String[] authorities) {
		User newUser = new User();
		UserPublicProfile userProfile = new UserPublicProfile();
		userProfile.setFirstName(firstName);
		userProfile.setLastName(lastName);
		userProfile.setNif(nif);
		newUser.setAuthorities(AuthorityUtils.createAuthorityList(authorities));

		newUser.setUserProfile(userProfile);

		newUser.setUsername(username);
		newUser.setPassword(password);

		return newUser;
	}

	public ContactDetails getContactDetails() {
		return contactDetails;
	}

	public void setUserProfile(UserPublicProfile userProfile) {
		this.userProfile = userProfile;
	}

	public void setContactDetails(ContactDetails contactDetails) {
		this.contactDetails = contactDetails;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public UserPublicProfile getUserProfile() {
		return userProfile;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setAuthorities(Collection<GrantedAuthority> authorities) {
		this.authorities = authorities;

	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	
	
	public static boolean validateUsername(String username) {
		return (username.length() > 4 && username.length() < 10);
	}

	public static boolean validatePwd(String username) {
		return username != null
				&& (username.length() > 4 && username.length() < 10);
	}

}
