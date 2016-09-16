package com.educapp.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.educapp.EducappAPI;
import com.educapp.controllers.UserDataManager.UserCreationFailedException;
import com.educapp.model.AssistanceControl;
import com.educapp.model.Course;
import com.educapp.model.Enrollment;
import com.educapp.model.Notification;
import com.educapp.model.TeacherAssignment;
import com.educapp.model.User;
import com.educapp.model.UserPublicProfile;
import com.educapp.repositories.AssistanceControlRepository;
import com.educapp.repositories.CoursesRepository;
import com.educapp.repositories.EnrollmentRepository;
import com.educapp.repositories.TeacherAssignmentRepository;
import com.educapp.utilities.CustomDateDeserializerSerializer;
import com.google.common.collect.Lists;

/**
 * Handles persistency related operations.
 * 
 * This class serves as a "link" between a higher level controller (for example,
 * the Spring MVC Controller) and the persistence handlers for the objects
 * managed in this server.
 * 
 * @author Harold
 *
 */
@Component
public class PersistentDataManager {
	@Autowired
	private UserDataManager userDataManager;
	
	@Autowired
	private NotificationManager notifManager;

	@Autowired
	private CoursesRepository courseRepository;
	
	@Autowired
	private EnrollmentRepository enrollmentRepository;
	
	@Autowired
	private AssistanceControlRepository assistanceControlRepository;
	
	@Autowired
	private TeacherAssignmentRepository courseTeachersRepository;
	

	public PersistentDataManager() {
	};

	public void initDefaultUsers() {
		User defaultManager = User.build("pepe", "pass", "Pepe", "Garcia Sanchez",
				"01255417K", EducappAPI.DEFAULT_USER_LEVEL_MANAGER);
		User defaultTeacher = User
				.build("mihail", "pass", "Mihail", "Gorvanovich", "X2555784M",
						EducappAPI.DEFAULT_USER_LEVEL_TEACHER);
		User defaultStudent1 = User.build("estudiante1", "pass", "Marcos", "Martinez Villaverde",
				"72264752F", EducappAPI.DEFAULT_USER_LEVEL_STUDENT);
		User defaultStudent2 = User.build("estudiante2", "pass", "Felipe", "Del Campo Verde",
				"22647412L", EducappAPI.DEFAULT_USER_LEVEL_STUDENT);
		User defaultStudent3 = User.build("estudiante3", "pass", "Marta", "Torres Martinez",
				"54412001A", EducappAPI.DEFAULT_USER_LEVEL_STUDENT);
		User defaultLegalTutor = User.build("god", "pass", "Chuck", "Norris",
				"RuntimeException: Chuck Norris NIF can not be represented in a String type.",
				EducappAPI.DEFAULT_USER_LEVEL_LEGAL_TUTOR);
		System.err.println("POSTCONSTRUCT");
		if (userDataManager == null)
			System.err.println("userDetailsManager is NULL");
		userDataManager.createUser(defaultManager);
		userDataManager.createUser(defaultTeacher);
		userDataManager.createUser(defaultStudent1);
		userDataManager.createUser(defaultStudent2);
		userDataManager.createUser(defaultStudent3);
		userDataManager.createUser(defaultLegalTutor);
		
		notifManager.addFollower(defaultLegalTutor.getUserProfile().getId(),
				defaultStudent2.getUserProfile().getId());
		notifManager.addFollower(defaultLegalTutor.getUserProfile().getId(),
				defaultStudent3.getUserProfile().getId());
	}

	public String[] getAuthorities(String username) {
		Collection<? extends GrantedAuthority> authorities = userDataManager
				.loadUserByUsername(username).getAuthorities();
		String[] authorityStrings = new String[authorities.size()];

		int i = 0;
		for (GrantedAuthority auth : authorities) {
			authorityStrings[i] = auth.getAuthority();
			i++;
		}

		return authorityStrings;
	}

	public void createUser(User user) throws UserCreationFailedException {
		userDataManager.createUser(user);
	}

	public User getUser(String username)
			throws UsernameNotFoundException {
		return (User) userDataManager.loadUserByUsername(username);
	}
	
	public UserPublicProfile getUserProfile(int userPublicProfileId){
		return userDataManager.findProfileById(userPublicProfileId);
	}
	
	public Collection<UserPublicProfile> getUserList() {

		return userDataManager.loadAllUserProfiles();
	}

	public void setUserProfileDescription(String username, String description)
			throws UsernameNotFoundException {
		User userEntity = (User) userDataManager.loadUserByUsername(username);
		userEntity.getUserProfile().setProfileDescription(description);
		userDataManager.updateUser(userEntity);
	}

	public Collection<Course> fetchCompleteCourseList() {
		Collection<Course> result = Lists.newArrayList(courseRepository.findAll());
		return result;
	}

	public boolean createCourse(String courseTag, String courseName, String courseDesc,
			int courseMaxStudents, Calendar beginDate, Calendar endDate) {
		Course course = new Course(courseTag, courseName, courseMaxStudents,
				beginDate, endDate);
		course.setCourseDescription(courseDesc);

		return courseRepository.save(course) != null;
	}

	public Course findCourse(String courseTag) {
		return courseRepository.findFirstByCourseTag(courseTag);
	}

	public Course modifyCourse(String courseTag, String courseName,
			String courseDesc, int courseMaxStudents, Calendar beginDate,
			Calendar endDate) {
		Course c = courseRepository.findFirstByCourseTag(courseTag);
		if (c == null) {
			return null;
		} else {
			c.setCourseDescription(courseDesc);
			c.setCourseFullName(courseName);
			c.setEndDate(endDate);
			c.setBeginDate(beginDate);
			c.setMaxAttendants(courseMaxStudents);
			return courseRepository.save(c);
		}
	}

	public TeacherAssignment assignTeacherToCourse(int userPublicId,
			String courseTag) throws UsernameNotFoundException {
		
		try{
			Course course = findCourse(courseTag);
			if(course == null){
				return null;
			}
			//We guarantee this user has the required privileges to manage
			//a class. If he does, we request our persistenceManager
			//to create a new TeacherAssignment entity with the required
			//details.
			User teacher = userDataManager
					.findUserByPublicProfileId(userPublicId);
			
			//If this assignment already exists, we just guarantee 
			//it's status is "active" and return. Otherwise, we follow
			//the whole procedure to make it anew.
			TeacherAssignment currentStatus = courseTeachersRepository
										.findFirstByTeacherAndCourse(
												teacher.getUserProfile(),
												course);
			if(currentStatus != null){
				currentStatus.setActive(true);
				return currentStatus;
			}
			
			
			if( teacher.getAuthorities().contains(
				new SimpleGrantedAuthority(EducappAPI.AUTH_CREATE_ASSISTANCE_CONTROL))
				&& teacher.getAuthorities().contains(
			    new SimpleGrantedAuthority(EducappAPI.AUTH_CREATE_EXAMS))){
				
				//If everything is sound, we will return the save attempt
				//result.
				TeacherAssignment result = courseTeachersRepository.save(
						new TeacherAssignment(teacher.getUserProfile(), course));
				if(result == null){
					
				}
				return result;
			}else{
				return null;
			}
		}catch(UsernameNotFoundException ex){
			throw ex;
		}
	}

	/**
	 * Manages the disabling of the privileges a teacher has
	 * over a course.
	 * 
	 * This shouldn't (and doesn't) remove the relationship
	 * itself, but 'disables' it, so any hard dependency
	 * between tables is not affected.
	 * 
	 * @param username
	 * @param courseTag
	 * @return
	 */
	public boolean removeTeacherFromCourse(int teacherPublicId, String courseTag) {
		UserPublicProfile teacher = null;
		try{
			teacher = getUserProfile(teacherPublicId);
			System.err.println("Removing teacher from course: "+teacher.getFirstName());
		}catch(UsernameNotFoundException ex){
			return false;
		}
		
		Course course = findCourse(courseTag);

		if(course == null){
			return false;
		}

		System.err.println("Removing teacher from course: "+course.getCourseTag());
		
		TeacherAssignment status = courseTeachersRepository
				.findFirstByTeacherAndCourse(teacher, course);
		if(status == null){
			return false;
		}else{
			status.setActive(false);
			courseTeachersRepository.save(status);
			return true;
		}
	}
	
	
	/**
	 * Adds a student to the access list of a course,
	 * which will allow him/her to access the course
	 * resources.
	 * 
	 * @param username
	 * @return
	 */
	public Enrollment assignStudentToCourse(int userPublicId, String courseTag) 
			throws Exception{
		//We check the existence of the potential student.
		User student = null;
		try{
			student = userDataManager.findUserByPublicProfileId(userPublicId);
		}catch(UsernameNotFoundException ex){
			throw ex;
		}
		
		//We check the existence of the course.
		Course course = null;
		course = findCourse(courseTag);
		if (course == null){
			throw new Exception("Course not found.");
		}
		
		//We check if an enrollment combination for this student
		//and course already exists, and make sure it is activated
		//if so.
		Enrollment pEnroll = enrollmentRepository
				.findFirstByStudentAndCourse(student.getUserProfile(), course);
		if(pEnroll != null){
			pEnroll.setActive(true);
		}else{
		}
		//Otherwise, we create it anew after checking the potential
		//student has the right privileges to be assigned to a 
		//course.
		if(student.getAuthorities().contains(
				new SimpleGrantedAuthority(EducappAPI.AUTH_COURSES_JOIN))){
			pEnroll = new Enrollment();
			pEnroll.setStudent(student.getUserProfile());
			pEnroll.setCourse(course);
			pEnroll.setActive(true);
		}
		
		if(pEnroll != null){
			System.err.println("Enrollment created for "+student.getUsername()
					+" in "+course.getCourseFullName());
			return enrollmentRepository.save(pEnroll);
		}
		else{
			System.err.println("Attempted to store null enrollment");
			return null;
		}
	}
	
	/**
	 * Manages the disabling of access to course resources
	 * for a student.
	 * 
	 * This shouldn't (and doesn't) remove the relationship
	 * itself, but 'disables' it, so any hard dependency
	 * between tables is not affected.
	 * 
	 * Will only throw exception on operation failure.
	 * No results will be returned on success.
	 * 
	 * @param username
	 * @param courseTag
	 */
	public void removeStudentFromCourse(int userPublicId, String courseTag)
		throws Exception{
		//We check the existence of the potential student.
		User student = null;
		try{
			student = userDataManager.findUserByPublicProfileId(userPublicId);
		}catch(UsernameNotFoundException ex){
			throw ex;
		}
		
		//We check the existence of the course.
		Course course = null;
		course = findCourse(courseTag);
		if (course == null){
			throw new Exception("Course not found.");
		}
		
		//We check if an enrollment combination for this student
		//and course already exists, and make sure it is activated
		//if so.
		Enrollment pEnroll = enrollmentRepository
				.findFirstByStudentAndCourse(student.getUserProfile(), course);
		if(pEnroll != null){
			pEnroll.setActive(false);
		}
	}
	
	
	/**
	 * Retrieves the list of enrollments for a given course.
	 * @throws Exception 
	 */
	public Collection<Enrollment> getAllEnrollmentsFromCourse(String courseTag) 
			throws Exception{
		Course course;
		course = findCourse(courseTag);
		if (course == null){
			throw new Exception("Course not found.");
		} 
		return enrollmentRepository
				.findByCourse(course);
	}
	
	
	/**
	 * Generates a collection of assistance control objects for
	 * a given class and date, based on the information stored
	 * in the database, and without data on whenever a user is
	 * active or not.
	 * Useful to send to requesting clients, so they do not have
	 * to process any complex student-course interactions.
	 */
	public Collection<AssistanceControl> generateAssistanceControlList(
			Calendar date, String courseTag) throws Exception{
		ArrayList<AssistanceControl> result = new ArrayList<AssistanceControl>();
		
		//The relevant date information for this assistance control
		//does only require day precision. We set to zero the rest
		//of fields to guarantee consistency on later queries.
		Calendar todayDate = new GregorianCalendar();
		todayDate.set(Calendar.HOUR_OF_DAY, 0);
		todayDate.set(Calendar.MINUTE, 0);
		todayDate.set(Calendar.SECOND, 0);
		todayDate.set(Calendar.MILLISECOND, 0);
		
		Course course;
		course = findCourse(courseTag);
		if (course == null){
			throw new Exception("Course not found.");
		} 
		Collection<Enrollment> enrollmentList = enrollmentRepository.findByCourse(course);
		
		
		for(Enrollment e : enrollmentList){
			result.add(new AssistanceControl(e, todayDate));
		}
		
		return result;
	}
	
	/***
	 * Stores the given assistance controls in the underlying
	 * database, after making sure their fields are proper
	 * and sound.
	 * 
	 * @param controls
	 * @return A copy of the list, containing only succesfully stored elements.
	 */
	public Collection<AssistanceControl> storeAssistanceControlInfo(
			Collection<AssistanceControl> controls,
			String submittingUserName){
		ArrayList<AssistanceControl> correctList = new ArrayList<AssistanceControl>();
		for(AssistanceControl ac : controls){
			if(AssistanceControl.isValid(ac)){
				//We will check if there is an AssistanceControl object
				//for this student and day. If there is, we will only
				//update it.
				AssistanceControl previousControl =
						assistanceControlRepository.findOneByEnrollmentReferenceAndDate(
						ac.getEnrollmentReference(), ac.getDate());
				if(previousControl != null){
					previousControl.setIsPresent(ac.getIsPresent());
					correctList.add(previousControl);
				}else{
					correctList.add(ac);
				}
			}
		}
		for(AssistanceControl item : correctList){
			System.err.println(item.getEnrollmentReference().getStudent().getNif()+"@"
					+item.getEnrollmentReference().getCourse().getCourseTag()+"__"
					+item.getIsPresent()); 
			
			//If the student wasn't present at class,
			//we will generate an alert.
			UserPublicProfile teacher = ((User) userDataManager
					.loadUserByUsername(submittingUserName)).getUserProfile();
			if(!item.getIsPresent())
			{
				String message = "Absent from class "
						+item.getEnrollmentReference().getCourse().getCourseFullName()
						+" on "
						+CustomDateDeserializerSerializer
							.dateFormatter.format(item.getDate().getTime());
						
				notifManager.generateNewNotification(
						item.getEnrollmentReference().getStudent().getId(),
						teacher.getId(),
						message,
						item.getDate());
			}
		}
		return Lists.newArrayList(assistanceControlRepository.save(correctList));
	}
	
	/**
	 * Retrieves and returns all the notifications generated for an user.
	 * 
	 * @param username
	 * @return
	 */
	public Collection<Notification> getAllNotificationsForUser(String username){
		UserPublicProfile user = ((User)userDataManager.loadUserByUsername(username)
				).getUserProfile();
		Collection<Notification> notificationsList = notifManager
				.getNotificationsDestinedToUser(user.getId());
		return notificationsList;
	}
}
