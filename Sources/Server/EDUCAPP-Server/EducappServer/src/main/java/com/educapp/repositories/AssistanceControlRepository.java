package com.educapp.repositories;

import java.util.Calendar;
import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.educapp.model.AssistanceControl;
import com.educapp.model.Enrollment;

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
public interface AssistanceControlRepository extends CrudRepository<AssistanceControl, Integer> {
	public AssistanceControl findOne(Integer id);
	public Collection<AssistanceControl> findByEnrollmentReference(Enrollment enrollment);
	public Collection<AssistanceControl> findByDate(Calendar date);
	public AssistanceControl findOneByEnrollmentReferenceAndDate(Enrollment enrollment, Calendar date);
}
