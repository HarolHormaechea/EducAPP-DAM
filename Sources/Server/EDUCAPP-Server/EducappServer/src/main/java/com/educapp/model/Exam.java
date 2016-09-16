package com.educapp.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Exam {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private UserPublicProfile evaluator;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Enrollment enrollment;
	
	private String examName;
	private String examComment;
	private Date examDate;
	private float examMark;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public UserPublicProfile getEvaluator() {
		return evaluator;
	}
	public void setEvaluator(UserPublicProfile evaluator) {
		this.evaluator = evaluator;
	}
	public Enrollment getEnrollment() {
		return enrollment;
	}
	public void setEnrollment(Enrollment enrollment) {
		this.enrollment = enrollment;
	}
	public String getExamName() {
		return examName;
	}
	public void setExamName(String examName) {
		this.examName = examName;
	}
	public String getExamComment() {
		return examComment;
	}
	public void setExamComment(String examComment) {
		this.examComment = examComment;
	}
	public Date getExamDate() {
		return examDate;
	}
	public void setExamDate(Date examDate) {
		this.examDate = examDate;
	}
	public float getExamMark() {
		return examMark;
	}
	public void setExamMark(float examMark) {
		this.examMark = examMark;
	}
	
	
}
