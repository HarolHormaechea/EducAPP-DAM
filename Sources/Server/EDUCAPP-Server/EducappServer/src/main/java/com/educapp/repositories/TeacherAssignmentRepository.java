package com.educapp.repositories;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.educapp.model.Course;
import com.educapp.model.TeacherAssignment;
import com.educapp.model.UserPublicProfile;

/**
 * Interface repository to be used with JPA.
 * 
 * http://docs.spring.io/spring-data/data-commons/docs/1.6.1.RELEASE/reference/html/repositories.html
 * 
 * 
 * @author Harold
 *
 */
@Repository
public interface TeacherAssignmentRepository extends CrudRepository<TeacherAssignment, Integer> {
	public TeacherAssignment findOne(Integer id);
	public Collection<TeacherAssignment> findByTeacher(UserPublicProfile teacher);
	public Collection<TeacherAssignment> findByCourse(Course course);
	public TeacherAssignment findFirstByTeacherAndCourse(UserPublicProfile teacher, Course course);
}
