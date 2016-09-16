package com.educapp.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Enrollment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Course course;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private UserPublicProfile student;
	
	private boolean active;
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	private float finalGrade;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Course getCourse() {
		return course;
	}
	public void setCourse(Course course) {
		this.course = course;
	}
	public UserPublicProfile getStudent() {
		return student;
	}
	public void setStudent(UserPublicProfile student) {
		this.student = student;
	}
	public float getFinalGrade() {
		return finalGrade;
	}
	public void setFinalGrade(float finalGrade) {
		this.finalGrade = finalGrade;
	}
	
	
}
