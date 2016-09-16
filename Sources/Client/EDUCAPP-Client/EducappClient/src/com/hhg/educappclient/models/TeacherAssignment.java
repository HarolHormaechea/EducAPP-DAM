package com.hhg.educappclient.models;

 
public class TeacherAssignment { 
	private int id;
	 
	private Course course;
	 
	private UserPublicProfile teacher;
	
	//Defines if this status applies (if false, 
	//the teacher shouldn't be able to perform any operations,
	//but any data uploaded or set by him should be still accessible.
	private boolean active;
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Required default constructor.
	 */
	public TeacherAssignment(){}
	
	public TeacherAssignment(UserPublicProfile teacher, Course course){
		this.teacher = teacher;
		this.course = course;
	}
	

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

	public UserPublicProfile getTeacher() {
		return teacher;
	}

	public void setTeacher(UserPublicProfile teacher) {
		this.teacher = teacher;
	}
	
	
}
