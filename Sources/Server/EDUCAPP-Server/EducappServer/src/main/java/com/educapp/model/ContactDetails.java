package com.educapp.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;

@Entity
public class ContactDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@OneToOne(fetch=FetchType.EAGER)
	@OrderColumn(name = "phones")
	private PhoneNumber phoneNumber;
	
	@OneToOne(fetch=FetchType.EAGER)
	private Address address;
	
	public ContactDetails(){}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	
}
