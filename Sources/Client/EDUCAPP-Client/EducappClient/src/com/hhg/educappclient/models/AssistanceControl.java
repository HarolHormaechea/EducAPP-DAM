package com.hhg.educappclient.models;

import java.util.Calendar;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hhg.educappclient.utilities.CustomDateDeserializerSerializer.CustomDateSerializer;

public class AssistanceControl {
	
	private int id;

	private boolean isPresent;
	
	private Enrollment enrollmentReference;
	
	@JsonSerialize(using = CustomDateSerializer.class)
	private Calendar date;
	
	
	public AssistanceControl(){}
	
	/**
	 * 
	 * Constructor with mandatory fields.
	 */
	public AssistanceControl(Enrollment enrollment, Calendar date, boolean isPresent){
		this.enrollmentReference = enrollment;
		this.date = date;
		this.isPresent = isPresent;
	}
	
	/**
	 * Constructor of object without active status.
	 * 
	 * @param enrollment
	 * @param date
	 */
	public AssistanceControl(Enrollment enrollment, Calendar date){
		this.enrollmentReference = enrollment;
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public boolean getIsPresent() {
		return isPresent;
	}

	public void setIsPresent(boolean isPresent) {
		this.isPresent = isPresent;
	}

	public Enrollment getEnrollmentReference() {
		return enrollmentReference;
	}

	public void setEnrollmentReference(Enrollment enrollmentReference) {
		this.enrollmentReference = enrollmentReference;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}
	
	public static boolean isValid(AssistanceControl ac){
		return (ac.getDate().get(Calendar.HOUR_OF_DAY) == 0
				&& ac.getDate().get(Calendar.MINUTE) == 0
				&& ac.getDate().get(Calendar.SECOND) == 0
				&& ac.getDate().get(Calendar.MILLISECOND) == 0);
	}
}
