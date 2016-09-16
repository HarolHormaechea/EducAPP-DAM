package com.educapp.repositories;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.educapp.model.Course;

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
public interface CoursesRepository extends CrudRepository<Course, Integer> {
	public Course findOne(Integer id);
	public Course findFirstByCourseTag(String courseTag);
	public Collection<Course> findByCourseFullName(String lastname);
}
