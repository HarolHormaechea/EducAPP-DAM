package com.educapp.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.Calendar;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.educapp.EducappAPI;
import com.educapp.EducappAPI.DEFAULT_USER_LEVELS;
import com.educapp.controllers.UserDataManager.UserCreationFailedException;
import com.educapp.model.AssistanceControl;
import com.educapp.model.Course;
import com.educapp.model.Enrollment;
import com.educapp.model.Notification;
import com.educapp.model.TeacherAssignment;
import com.educapp.model.User;
import com.educapp.model.UserPublicProfile;

/**
 * REST Controller for our Spring-based service.
 * 
 * Will handle pre-method execution authentication, delegate operation
 * executions to other handler classes, and manage the responses
 * sent back to clients.
 * 
 * @author Harold Hormaechea Garcia
 *
 */
@Controller
public class BaseController{
	
	@Autowired
	private PersistentDataManager persistenceManager;
	
	
	@PostConstruct
	public void defaultUserCreation(){
		persistenceManager.initDefaultUsers();
	}
	
	/**
	 * Retrieves the current list of authorities assigned to this user,
	 * and sends them back to the requesting client.
	 * 
	 * @param p
	 * @return
	 */
	@RequestMapping(value=EducappAPI.LOGGED_USER_GET_AUTHORITIES, method=RequestMethod.GET)
	public @ResponseBody String[] getLoggedUserAuthorities(Principal p){
		System.err.println("getLoggedUserAuthorities()"+p.getName());
		return persistenceManager.getAuthorities(p.getName());
	}
	
	/**
	 * Manages user creation requests.
	 */
	@RequestMapping(value=EducappAPI.SIGN_UP, method=RequestMethod.POST)
	public @ResponseBody boolean createUser(
			@PathVariable(EducappAPI.USER_PUBLIC_ID) String username,
			@PathVariable(EducappAPI.PASSWORD) String password,
			@PathVariable(EducappAPI.FIRST_NAME) String firstName,
			@PathVariable(EducappAPI.LAST_NAME) String lastName,
			@PathVariable(EducappAPI.NIF) String nif,
			@PathVariable(EducappAPI.USER_LEVEL) DEFAULT_USER_LEVELS userLevel,
			HttpServletResponse reply){
		
		try{
			if(!User.validateUsername(username)){
				reply.sendError(EducappAPI.HTTP_ERROR_INVALID_PARAMETER,
						EducappAPI.ERROR_SIGNUP_INVALID_USERNAME);
				return false;
				
			}else if(!User.validatePwd(password)){
				reply.sendError(EducappAPI.HTTP_ERROR_INVALID_PARAMETER,
						EducappAPI.ERROR_SIGNUP_INVALID_PASSWORD);
				return false;
				
			}else{
				User user = User.build(
						username, password, firstName,
						lastName, nif, userLevel.getValue());
				try{
					persistenceManager.createUser(user);
					reply.setStatus(HttpServletResponse.SC_OK);
					return true;
				}catch(UserCreationFailedException ex){
					reply.sendError(EducappAPI.HTTP_ERROR_INVALID_PARAMETER,
							EducappAPI.ERROR_SIGNUP_USER_ALREADY_EXISTS);
					return false;
				}
			}
		}catch(IOException ex){
			try {
				reply.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} catch (IOException e) {}
			return false;
		}
	}
	
	
	/**
	 * Retrieves the current profile of an user.
	 */
	@RequestMapping(value=EducappAPI.GET_PROFILES_SINGLE, method = RequestMethod.GET)
	@PreAuthorize(value = "hasRole('"+EducappAPI.AUTH_RETRIEVE_OTHERS_PROFILE+"')")
	public @ResponseBody UserPublicProfile getUserProfile(
			@PathVariable(EducappAPI.USER_PUBLIC_ID) String username,
			HttpServletResponse reply){
		try{
			User user = (User)persistenceManager.getUser(username);
			reply.setStatus(HttpServletResponse.SC_OK);
			return user.getUserProfile();
		}catch(UsernameNotFoundException ex){
			try {
				reply.sendError(HttpServletResponse.SC_NOT_FOUND,
						"The requested user was not found");
			} catch (IOException e) {}
			return null;
		}
	}
	
	/**
	 * Retrieves the current profile of an user.
	 * 
	 * The returned entity is an User object, with empty
	 * password and NIF fields for obvious security reasons.
	 */
	@RequestMapping(value=EducappAPI.GET_PUBLIC_PROFILE, method = RequestMethod.GET)
	@PreAuthorize(value = "hasRole('"+EducappAPI.AUTH_RETRIEVE_OTHERS_PROFILE+"')")
	public @ResponseBody UserPublicProfile getUserProfile(
			@PathVariable(EducappAPI.USER_PUBLIC_ID) int profileId,
			HttpServletResponse reply){
		try{
			UserPublicProfile user = persistenceManager.getUserProfile(profileId);
			reply.setStatus(HttpServletResponse.SC_OK);
			return user;
		}catch(UsernameNotFoundException ex){
			try {
				reply.sendError(HttpServletResponse.SC_NOT_FOUND, "The requested user was not found");
			} catch (IOException e) {}
			return null;
		}
	}
	
	/**
	 * Retrieves all the users currently registered in the service,
	 * and sends them to authorized users.
	 * 
	 * Useful method to get a preliminary low-detail list of users,
	 * to later access someone's profile.
	 */
	@RequestMapping(value=EducappAPI.GET_PROFILES, method = RequestMethod.GET)
	@PreAuthorize(value = "hasRole('"+EducappAPI.AUTH_USERS_LIST+"')")
	public @ResponseBody Collection<UserPublicProfile> getUserList(HttpServletResponse reply){
		Collection<UserPublicProfile> users = persistenceManager.getUserList();
		if(users == null || users.isEmpty()){
			//Should not happen, otherwise no user
			//could request this method execution.
			try {
				reply.sendError(HttpServletResponse.SC_NO_CONTENT, "No users found.");
			} catch (IOException e) {}
		}							
		else{
			reply.setStatus(HttpServletResponse.SC_OK);
		}
		return users;
	}
	
	/**
	 * Edits the profile description for the logged in user.
	 */
	@RequestMapping(value=EducappAPI.POST_USER_PROFILE, method=RequestMethod.POST)
	public @ResponseBody boolean editUserProfile(
			@PathVariable(EducappAPI.DESCRIPTION) String description,
			Principal loggedUser,
			HttpServletResponse reply){
		try{
			persistenceManager.setUserProfileDescription(
					loggedUser.getName(), description);
			return true;
		}catch(UsernameNotFoundException ex){
			try {
				reply.sendError(HttpServletResponse.SC_NOT_FOUND, "The user does not exist");
			} catch (IOException e) {}
			return false;
		}
	}
	
	/**
	 * Returns the list of courses currently stored in the database.
	 */
	@RequestMapping(value=EducappAPI.COURSE_LIST, method=RequestMethod.GET)
	@PreAuthorize(value = "hasRole('"+EducappAPI.AUTH_COURSES_LIST+"')")
	public @ResponseBody Collection<Course> fetchCourseList(){
		return persistenceManager.fetchCompleteCourseList();
	}
	
	/**
	 * Creates a new course.
	 * 
	 * Date transmission issues resolved using:
	 * http://www.petrikainulainen.net/programming/spring-framework/spring-from-the-trenches-parsing-date-and-time-information-from-a-request-parameter/
	 * http://stackoverflow.com/questions/28540224/retrofit-gson-serialize-date-from-json-string-into-long-or-java-lang-long
	 * And converters stored in CustomDateDeserializerSerializer.class
	 * 
	 * Retrofit client must send the date in String
	 * format, YYYY-MM-DD, and @DateTimeFormat will
	 * be formatted automatically into a calendar object,
	 * because of issues found when attempting to attach
	 * a serializer and deserializer to the retrofit
	 * instance (which may be fixed using another retrofit
	 * client, anyway, we'll have to check that out)
	 */
	@RequestMapping(value=EducappAPI.COURSE_CREATE, method=RequestMethod.POST)
	@PreAuthorize(value = "hasRole('"+EducappAPI.AUTH_COURSES_CREATE+"')")
	public @ResponseBody boolean createNewCourse(
			@PathVariable(EducappAPI.COURSE_TAG) String courseTag,
			@PathVariable(EducappAPI.COURSE_NAME) String courseName,
			@RequestBody String courseDesc,
			@PathVariable(EducappAPI.COURSE_MAX_ATT) int courseMaxStudents,
			@PathVariable(EducappAPI.COURSE_DATE_BEGIN)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)Calendar beginDate,
			@PathVariable(EducappAPI.COURSE_DATE_END) 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)Calendar endDate,
			HttpServletResponse reply){
		boolean result = persistenceManager.createCourse(
				courseTag, courseName,
				courseDesc, courseMaxStudents,
				beginDate, endDate);
		
		if(!result){
			try {
				reply.sendError(418, "I'm a teapot. The course could not be brewed.");
			} catch (IOException e) {}
		}else{
			reply.setStatus(HttpServletResponse.SC_OK);
		}
		return result;
	}
	
	/**
	 * Creates a new course.
	 * 
	 * Date transmission issues resolved using:
	 * http://www.petrikainulainen.net/programming/spring-framework/spring-from-the-trenches-parsing-date-and-time-information-from-a-request-parameter/
	 * http://stackoverflow.com/questions/28540224/retrofit-gson-serialize-date-from-json-string-into-long-or-java-lang-long
	 * And converters stored in CustomDateDeserializerSerializer.class
	 * 
	 * Retrofit client must send the date in String
	 * format, YYYY-MM-DD, and @DateTimeFormat will
	 * be formatted automatically into a calendar object,
	 * because of issues found when attempting to attach
	 * a serializer and deserializer to the retrofit
	 * instance (which may be fixed using another retrofit
	 * client, anyway, we'll have to check that out)
	 */
	@RequestMapping(value=EducappAPI.COURSE_MODIFY, method=RequestMethod.POST)
	@PreAuthorize(value = "hasRole('"+EducappAPI.AUTH_COURSES_CREATE+"')")
	public @ResponseBody Course modifyExistingCourse(
			@PathVariable(EducappAPI.COURSE_TAG) String courseTag,
			@PathVariable(EducappAPI.COURSE_NAME) String courseName,
			@RequestBody String courseDesc,
			@PathVariable(EducappAPI.COURSE_MAX_ATT) int courseMaxStudents,
			@PathVariable(EducappAPI.COURSE_DATE_BEGIN)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)Calendar beginDate,
			@PathVariable(EducappAPI.COURSE_DATE_END) 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)Calendar endDate,
			HttpServletResponse reply){
		Course result = persistenceManager.modifyCourse(
				courseTag, courseName, courseDesc, 
				courseMaxStudents, beginDate, endDate);
		
		if(result == null){
			try {
				reply.sendError(404, "I'm a teapot. The requested beverage was not found"
						+ " inside me.");
			} catch (IOException e) {}
		}else{
			reply.setStatus(HttpServletResponse.SC_OK);
		}
		return result;
	}
	
	/**
	 * Handles the assignment of teachers to courses for
	 * users authorized to do so.
	 * 
	 * @return
	 */
	@RequestMapping(value=EducappAPI.COURSE_ADD_TEACHER, method=RequestMethod.POST)
	@PreAuthorize(value = "hasRole('"+EducappAPI.AUTH_COURSES_CREATE+"')")
	public @ResponseBody boolean assignTeacherToCourse(
			@PathVariable(EducappAPI.USER_PUBLIC_ID) int userPublicId,
			@PathVariable(EducappAPI.COURSE_TAG) String courseTag,
			HttpServletResponse reply){
		
		try{
			TeacherAssignment data = 
					persistenceManager.assignTeacherToCourse(userPublicId, courseTag);
			if(data == null){
				try {
					reply.sendError(HttpServletResponse.SC_CONFLICT, "Conflict: The course "
							+ "does not exist or the requested teacher has not got the required "
							+ "privileges.");
				} catch (IOException e) {}
			}else{
				reply.setStatus(HttpServletResponse.SC_OK);
				return true;
			}
		}catch(UsernameNotFoundException ex){

			try {
				reply.sendError(HttpServletResponse.SC_NOT_FOUND, "The teacher was not found.");
			} catch (IOException e) {}
		}
		return false;
	}
	
	/**
	 * Handles the removal of teachers from courses for
	 * users authorized to do so.
	 * 
	 * 
	 * @return
	 */
	@RequestMapping(value=EducappAPI.COURSE_REMOVE_TEACHER, method=RequestMethod.POST)
	@PreAuthorize(value = "hasRole('"+EducappAPI.AUTH_COURSES_CREATE+"')")
	public @ResponseBody boolean removeTeacherFromCourse(
			@PathVariable(EducappAPI.USER_PUBLIC_ID) int userPublicId,
			@PathVariable(EducappAPI.COURSE_TAG) String courseTag,
			HttpServletResponse reply){
	
		boolean result = persistenceManager.removeTeacherFromCourse(userPublicId, courseTag);
		if(!result){
			//TODO: Divide this error more for meaningfulness.
			try {
				reply.sendError(HttpServletResponse.SC_NOT_FOUND,
						"Teacher, course or assignmentn not found.");
			} catch (IOException e) {}
		}else{
			reply.setStatus(HttpServletResponse.SC_OK);
		}
		return result;
	}
	
	
	
	/**
	 * Handles the assignment of students to courses for
	 * users authorized to do so.
	 * 
	 * @return
	 */
	@RequestMapping(value=EducappAPI.COURSE_ADD_STUDENT, method=RequestMethod.POST)
	@PreAuthorize(value = "hasRole('"+EducappAPI.AUTH_CREATE_ENROLLMENTS+"')")
	public @ResponseBody Enrollment assignStudentToCourse(
			@PathVariable(EducappAPI.USER_PUBLIC_ID) int userPublicId,
			@PathVariable(EducappAPI.COURSE_TAG) String courseTag,
			HttpServletResponse reply){
		
		Enrollment data = null;
		try{
			 data = persistenceManager.assignStudentToCourse(userPublicId, courseTag);
			if(data == null){
				try {
					reply.sendError(HttpServletResponse.SC_CONFLICT, "Conflict: The course "
							+ "does not exist or the requested potential student has +"
							+ "not got the required privileges.");
					System.err.println("SC_CONFLICT");
				} catch (IOException e) {}
			}else{
				reply.setStatus(HttpServletResponse.SC_OK);
				System.err.println("SC_OK");
			}
		}catch(Exception ex){
			ex.printStackTrace();
			try {
				reply.sendError(HttpServletResponse.SC_NOT_FOUND,
						"The student or course was not found.");
				System.err.println("SC_NOT_FOUND");
			} catch (IOException e) {}
		}
		return data;
	}
	
	/**
	 * Handles the removal/disabling of students from courses for
	 * users authorized to do so.
	 * 
	 * @return
	 */
	@RequestMapping(value=EducappAPI.COURSE_REMOVE_STUDENT, method=RequestMethod.POST)
	@PreAuthorize(value = "hasRole('"+EducappAPI.AUTH_CREATE_ENROLLMENTS+"')")
	public @ResponseBody boolean removeStudentFromCourse(
			@PathVariable(EducappAPI.USER_PUBLIC_ID) int userPublicId,
			@PathVariable(EducappAPI.COURSE_TAG) String courseTag,
			HttpServletResponse reply){
		boolean result = false;
		try{
			persistenceManager.removeStudentFromCourse(userPublicId, courseTag);
			result = true;
		}catch(Exception ex){
			try {
				reply.sendError(HttpServletResponse.SC_NOT_FOUND,
						"Student, course or assignment not found.");
			} catch (IOException e) {}
		}
		reply.setStatus(HttpServletResponse.SC_OK);
		return result;
	}
	
	@RequestMapping(value=EducappAPI.ASSISTANCE_CONTROL_GET_EMPTY_LIST, method=RequestMethod.GET)
	@PreAuthorize(value = "hasRole('"+EducappAPI.AUTH_CREATE_ASSISTANCE_CONTROL+"')")
	public @ResponseBody Collection<AssistanceControl> getNewControlListForToday(
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
			@PathVariable(EducappAPI.DATE) Calendar date, 
			@PathVariable(EducappAPI.COURSE_TAG) String courseTag,
			HttpServletResponse reply){
		Collection<AssistanceControl> result = null;
		System.err.println(courseTag);
		try {
			result = persistenceManager.generateAssistanceControlList(date, courseTag);
		} catch (Exception e) {
			try {
				reply.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
			} catch (IOException e1) {}
			e.printStackTrace();
		}
		return result;
	}
	
	@RequestMapping(value=EducappAPI.ASSISTANCE_CONTROL_ADD_LIST, method=RequestMethod.POST)
	@PreAuthorize(value = "hasRole('"+EducappAPI.AUTH_CREATE_ASSISTANCE_CONTROL+"')")
	public @ResponseBody Collection<AssistanceControl> uploadFilledControlList(
			@RequestBody Collection<AssistanceControl> list,
			Principal principal,
			HttpServletResponse reply){
		for(AssistanceControl item : list){
			System.out.println(item.getEnrollmentReference().getStudent().getNif()+"@"
					+item.getEnrollmentReference().getCourse().getCourseTag()+"__"
					+item.getIsPresent());
		}
		return persistenceManager
				.storeAssistanceControlInfo(list, principal.getName());
	}
	
	@RequestMapping(value=EducappAPI.NOTIFICATIONS, method=RequestMethod.GET)
	@PreAuthorize(value = "hasRole('"+EducappAPI.AUTH_RETRIEVE_SELF_ALERTS+"')")
	public @ResponseBody Collection<Notification> getAllNotificationsDestinedToUser(
			Principal p,
			HttpServletResponse reply){
		System.out.println(p.getName()+" is requesting his notifications.");
		Collection<Notification> notifs = persistenceManager
				.getAllNotificationsForUser(p.getName());
		System.out.println(p.getName()+" will receive "+notifs.size()+" notifications.");
		if(notifs == null || notifs.isEmpty()){
			try {
				reply.sendError(404, "No pending notifications");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			reply.setStatus(200);
		}
		return notifs;
	}
	
}
