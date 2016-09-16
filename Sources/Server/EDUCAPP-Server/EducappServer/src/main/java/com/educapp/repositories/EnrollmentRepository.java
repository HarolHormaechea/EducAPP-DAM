package com.educapp.repositories;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.educapp.model.Course;
import com.educapp.model.Enrollment;
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
public interface EnrollmentRepository extends CrudRepository<Enrollment, Integer> {
	public Enrollment findOne(Integer id);
	public Collection<Enrollment> findByStudent(UserPublicProfile student);
	public Collection<Enrollment> findByCourse(Course course);
	public Enrollment findFirstByStudentAndCourse(UserPublicProfile student, Course course);
}
