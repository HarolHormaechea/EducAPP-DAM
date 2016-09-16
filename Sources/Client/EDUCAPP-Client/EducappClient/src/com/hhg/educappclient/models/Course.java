package com.hhg.educappclient.models;

import java.util.Calendar;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.annotations.SerializedName;
import com.hhg.educappclient.utilities.CustomDateDeserializerSerializer.CustomDateSerializer;

public class Course {
	
	private int id;
	
	private Classroom classroom;
	
	private String courseTag;
	
	private String courseFullName;
	private String courseDescription;
	private int maxAttendants;
	
	@SerializedName("beginDate")
	@JsonSerialize(using = CustomDateSerializer.class)
	//@JsonDeserialize(using = CustomDateDeserializer.class)
	private Calendar beginDate;
	
	@SerializedName("endDate")
	@JsonSerialize(using = CustomDateSerializer.class)
	private Calendar endDate;
	
	public Course(){}
	
	private int noteRequiredToPass = 5; //Default value.
	
	
	
	/**
	 * Contructor with mandatory fields.
	 */
	public Course(String courseTag, String courseFullName, int maxAttendants,
			Calendar beginDate2, Calendar endDate2){
		this.courseTag = courseTag;
		this.courseFullName = courseFullName;
		this.maxAttendants = maxAttendants;
		this.beginDate = beginDate2;
		this.endDate = endDate2;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCourseTag() {
		return courseTag;
	}
	public void setCourseTag(String courseTag) {
		this.courseTag = courseTag;
	}
	public String getCourseFullName() {
		return courseFullName;
	}
	public void setCourseFullName(String courseFullName) {
		this.courseFullName = courseFullName;
	}
	public String getCourseDescription() {
		return courseDescription;
	}
	public void setCourseDescription(String courseDescription) {
		this.courseDescription = courseDescription;
	}
	public int getMaxAttendants() {
		return maxAttendants;
	}
	public void setMaxAttendants(int maxAttendants) {
		this.maxAttendants = maxAttendants;
	}
	public Calendar getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(Calendar beginDate) {
		this.beginDate = beginDate;
	}
	public Calendar getEndDate() {
		return endDate;
	}
	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}
	public Classroom getClassroom() {
		return classroom;
	}
	public void setClassroom(Classroom classroom) {
		this.classroom = classroom;
	}
	public int getNoteRequiredToPass() {
		return noteRequiredToPass;
	}

	public void setNoteRequiredToPass(int noteRequiredToPass) {
		this.noteRequiredToPass = noteRequiredToPass;
	}
}
