package com.hhg.educappclient.models;


public class Enrollment {
	
	private int id;
	
	private Course course;
	
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
