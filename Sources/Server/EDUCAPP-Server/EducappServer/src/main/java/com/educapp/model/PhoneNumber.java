package com.educapp.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PhoneNumber {
	@Id
	private String number;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	
}
