package com.hhg.educappclient.models;


public class ContactDetails {
	
	private int id;
	
	private PhoneNumber phoneNumber;
	
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
