package com.educapp.model;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.educapp.utilities.CustomDateDeserializerSerializer.CustomDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.annotations.SerializedName;

@Entity
public class Course {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Classroom classroom;
	
	@Column(unique=true, length=512)
	private String courseTag;
	
	@Column(length=1024)
	private String courseFullName;
	
	@Column(length=2048)
	private String courseDescription;
	private int maxAttendants;
	
	private int noteRequiredToPass = 5; //Default value.
	
	public int getNoteRequiredToPass() {
		return noteRequiredToPass;
	}

	public void setNoteRequiredToPass(int noteRequiredToPass) {
		this.noteRequiredToPass = noteRequiredToPass;
	}
	@Temporal(TemporalType.DATE)
	@SerializedName("beginDate")
	@JsonSerialize(using = CustomDateSerializer.class)
	//@JsonDeserialize(using = CustomDateDeserializer.class)
	private Calendar beginDate;
	
	@Temporal(TemporalType.DATE)
	@SerializedName("endDate")
	@JsonSerialize(using = CustomDateSerializer.class)
	//@JsonDeserialize(using = CustomDateDeserializer.class)
	private Calendar endDate;
	
	public Course(){}
	
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
	
}
