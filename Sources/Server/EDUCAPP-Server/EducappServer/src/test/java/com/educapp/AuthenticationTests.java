package com.educapp;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.ApacheClient;
import retrofit.converter.GsonConverter;

import com.educapp.common.SecuredRestBuilder;
import com.educapp.common.UnsafeHttpsClient;
import com.educapp.model.AssistanceControl;
import com.educapp.model.Course;
import com.educapp.model.Enrollment;
import com.educapp.model.Notification;
import com.educapp.model.UserPublicProfile;
import com.educapp.utilities.CustomDateDeserializerSerializer;
import com.educapp.utilities.CustomDateDeserializerSerializer.CustomRetrofitCaldendarDeSerializer;
import com.educapp.utilities.CustomDateDeserializerSerializer.CustomRetrofitGregorianCalendarSerializer;
import com.educapp.utilities.CustomDateDeserializerSerializer.CustomRetrofitSerializer;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Test class for authentication features in the server.
 * 
 * @author Harold Hormaechea Garcia
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AuthenticationTests {
	private String TEST_URL = "https://localhost:8443";
	
	

	@Test
	public void AtestExistingUserLogin() {
		EducappAPI educappUser1 = new SecuredRestBuilder()
		.setLoginEndpoint(TEST_URL + EducappAPI.TOKEN_PATH)
		.setUsername("pepe")
		.setPassword("pass")
		.setClientId("mobile")
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		.create(EducappAPI.class);
		
		String[] authorities = educappUser1.getLoggedUserAuthorities();
		System.out.println(authorities[0]);
		assertTrue(authorities.length > 0);
	}
	
	@Test
	public void BtestNonExistingUserLogin(){
		EducappAPI educappUser1 = new SecuredRestBuilder()
		.setLoginEndpoint(TEST_URL + EducappAPI.TOKEN_PATH)
		.setUsername("I_DO_NOT_EXIST_WEEEEE")
		.setPassword("pass")
		.setClientId("mobile")
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		.create(EducappAPI.class);
		
		try{
			String[] authorities = educappUser1.getLoggedUserAuthorities();
			System.out.println(authorities[0]);
		}catch (RetrofitError ex){
			assertTrue(ex.getMessage().contains("401"));//Contains unauthorized HTTP code
			return;
		}
		fail();
	}
	
	@Test
	public void CcreateNewInvalidUserTest(){
		String username = UUID.randomUUID().toString();
		
		RestAdapter notLoggedAdapter = new RestAdapter.Builder()
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .setEndpoint(TEST_URL)
        .setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
        .build();
		
		EducappAPI api = notLoggedAdapter.create(EducappAPI.class);
		try{
		api.signUp(username, "2", "Ibh-salam", "Orruba-Em", "00000000H",
				EducappAPI.DEFAULT_USER_LEVELS.STUDENT);
		fail();
		}catch(RetrofitError e){
			assertTrue(e.getResponse().getStatus() 
					== EducappAPI.HTTP_ERROR_INVALID_PARAMETER);
		}
	}
	
	@Test
	public void DmodifySelfProfile(){
		String setDescription = UUID.randomUUID().toString();
		
		EducappAPI educappUser1 = new SecuredRestBuilder()
		.setLoginEndpoint(TEST_URL + EducappAPI.TOKEN_PATH)
		.setUsername("pepe")
		.setPassword("pass")
		.setClientId("mobile")
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		.create(EducappAPI.class);
		
		educappUser1.postUserProfile(setDescription);
		
		assertTrue(educappUser1.getUserProfile("pepe")
				.getProfileDescription()
				.equals(setDescription));
	}
	
	@Test
	public void EAccessToOthersProfiles(){
		EducappAPI educappUser1 = new SecuredRestBuilder()
		.setLoginEndpoint(TEST_URL + EducappAPI.TOKEN_PATH)
		.setUsername("god")
		.setPassword("pass")
		.setClientId("mobile")
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		.create(EducappAPI.class);
		
		try{
			educappUser1.getUserProfile("pepe").getProfileDescription();
			
		}catch(RetrofitError e){
			fail();
		}
	}
	
	@Test
	public void FaddNewCourseWithoutAuthorization(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(GregorianCalendar.class, new CustomRetrofitSerializer());
		
		EducappAPI educappUser1 = new SecuredRestBuilder()
		.setConverter(new GsonConverter(gsonBuilder.create()))
		.setLoginEndpoint(TEST_URL + EducappAPI.TOKEN_PATH)
		.setUsername("God")
		.setPassword("pass")
		.setClientId("mobile")
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		.create(EducappAPI.class);
		
		try{
			
			
			educappUser1.createCourse(UUID.randomUUID().toString(),
					UUID.randomUUID().toString(),
					"This is a sample description. Woah! I'm so cute.",
					100,
					CustomDateDeserializerSerializer.dateFormatter.format(Calendar.getInstance().getTime()),
					CustomDateDeserializerSerializer.dateFormatter.format(Calendar.getInstance().getTime())//"2017-06-06"
					);
			fail();
		}catch(RetrofitError e){
			assertTrue(e.getResponse().getStatus() 
					== HttpServletResponse.SC_FORBIDDEN);
		}
	}

	@Test
	public void GaddNewCourseWithAuthorization(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Calendar.class, new CustomRetrofitCaldendarDeSerializer());
		Gson gson = gsonBuilder.create() ;
		EducappAPI educappUser1 = new SecuredRestBuilder()
		.setConverter(new GsonConverter(gson))
		.setLoginEndpoint(TEST_URL + EducappAPI.TOKEN_PATH)
		.setUsername("pepe")
		.setPassword("pass")
		.setClientId("mobile")
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		.create(EducappAPI.class);
		boolean result = false;
		try{
			result = educappUser1.createCourse(
					"ESCHINC020162017"+UUID.randomUUID().getLeastSignificantBits(),
					"Estudios Superiores sobre la Chinchilla en España",
					"Chinchilla es un género de roedores histricomorfos de la"
					+ " familia Chinchillidae conocidos vulgarmente como "
					+ "chinchillas. Las chinchillas son muy apreciadas "
					+ "en peletería y han sido cazadas en gran cantidad, lo que "
					+ "las ha llevado a su escasez.\n"
					+ "En este curso de cien años aprenderemos a apreciarlas y a "
					+ "mimarlas adecuadamente",
					100,
					CustomDateDeserializerSerializer.dateFormatter.format(Calendar.getInstance().getTime()),
					"2117-06-25"
					);
		}catch(RetrofitError e){
			assertTrue(result == true);
		}
	}
	
	@Test
	public void HretrieveCourseListWithoutAuthorization(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Calendar.class, new CustomRetrofitCaldendarDeSerializer());
		
		
		EducappAPI educappUser1 = new SecuredRestBuilder()
		.setConverter(new GsonConverter(gsonBuilder.create()))
		.setLoginEndpoint(TEST_URL + EducappAPI.TOKEN_PATH)
		.setUsername("God")
		.setPassword("pass")
		.setClientId("mobile")
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		.create(EducappAPI.class);
		try{
			educappUser1.fetchCourseList();
		}catch(RetrofitError e){
			assertTrue(e.getResponse().getStatus() 
					== HttpServletResponse.SC_FORBIDDEN);
		}
	}
	
	@Test
	public void IretrieveCourseListWithAuthorization(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Calendar.class, new CustomRetrofitCaldendarDeSerializer());
		
		EducappAPI educappUser1 = new SecuredRestBuilder()
		.setConverter(new GsonConverter(gsonBuilder.create()))
		.setLoginEndpoint(TEST_URL + EducappAPI.TOKEN_PATH)
		.setUsername("estudiante1")
		.setPassword("pass")
		.setClientId("mobile")
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		.create(EducappAPI.class);
		assertTrue(educappUser1.fetchCourseList().size() > 0);
	}
	
	@Test
	public void JalterCourseDescriptionWithAuthorization(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Calendar.class, new CustomRetrofitCaldendarDeSerializer());
		
		EducappAPI educappUser1 = new SecuredRestBuilder()
		.setConverter(new GsonConverter(gsonBuilder.create()))
		.setLoginEndpoint(TEST_URL + EducappAPI.TOKEN_PATH)
		.setUsername("pepe")
		.setPassword("pass")
		.setClientId("mobile")
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		.create(EducappAPI.class);
		ArrayList<Course> courses = Lists.newArrayList(educappUser1.fetchCourseList());
		educappUser1.modifyCourse(courses.get(0).getCourseTag(),
				courses.get(0).getCourseFullName(),
				"UDPATE\n"+courses.get(0).getCourseDescription(),
				courses.get(0).getMaxAttendants()+3,
				CustomDateDeserializerSerializer.dateFormatter.format(courses.get(0).getBeginDate().getTime()),
				CustomDateDeserializerSerializer.dateFormatter.format(courses.get(0).getEndDate().getTime()));
	}
	
	@Test
	public void KretrieveFullUserListWithAuthorization(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Calendar.class, new CustomRetrofitCaldendarDeSerializer());
		
		EducappAPI educappUser1 = new SecuredRestBuilder()
		.setConverter(new GsonConverter(gsonBuilder.create()))
		.setLoginEndpoint(TEST_URL + EducappAPI.TOKEN_PATH)
		.setUsername("pepe")
		.setPassword("pass")
		.setClientId("mobile")
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		.create(EducappAPI.class);
		
		Collection<UserPublicProfile> users = educappUser1.getAllProfiles();
		assertTrue((users != null) && (users.size() > 0));
	}
	
	@Test
	public void LassignTeacherToCourse(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Calendar.class, new CustomRetrofitCaldendarDeSerializer());
		
		EducappAPI educappUser1 = new SecuredRestBuilder()
		.setConverter(new GsonConverter(gsonBuilder.create()))
		.setLoginEndpoint(TEST_URL + EducappAPI.TOKEN_PATH)
		.setUsername("pepe")
		.setPassword("pass")
		.setClientId("mobile")
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		.create(EducappAPI.class);
		ArrayList<Course> courses = Lists.newArrayList(educappUser1.fetchCourseList());
		
		//1 is the public profile ID of the DEFAULT teacher
		boolean result = 
				educappUser1.assignTeacherToCourse(2, courses.get(0).getCourseTag());
		assertTrue(result);
		
	}
	
	@Test
	public void MremoveTeacherFromCourse(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Calendar.class, new CustomRetrofitCaldendarDeSerializer());
		
		EducappAPI educappUser1 = new SecuredRestBuilder()
		.setConverter(new GsonConverter(gsonBuilder.create()))
		.setLoginEndpoint(TEST_URL + EducappAPI.TOKEN_PATH)
		.setUsername("pepe")
		.setPassword("pass")
		.setClientId("mobile")
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		.create(EducappAPI.class);
		ArrayList<Course> courses = Lists.newArrayList(educappUser1.fetchCourseList());
		
		boolean result = 
				educappUser1.removeTeacherFromCourse(2, courses.get(0).getCourseTag());
		assertTrue(result);
		
	}
	
	
	@Test
	public void NassignStudentToCourse(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Calendar.class, new CustomRetrofitCaldendarDeSerializer());
		
		EducappAPI educappUser1 = new SecuredRestBuilder()
		.setConverter(new GsonConverter(gsonBuilder.create()))
		.setLoginEndpoint(TEST_URL + EducappAPI.TOKEN_PATH)
		.setUsername("pepe")
		.setPassword("pass")
		.setClientId("mobile")
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		.create(EducappAPI.class);
		ArrayList<Course> courses = Lists.newArrayList(educappUser1.fetchCourseList());
		
		Enrollment result = 
				educappUser1.assignStudentToCourse(3, courses.get(0).getCourseTag());
		assertTrue(result.isActive());
		
	}
	
	@Test
	public void OremoveStudentFromCourse(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Calendar.class, new CustomRetrofitCaldendarDeSerializer());
		
		EducappAPI educappUser1 = new SecuredRestBuilder()
		.setConverter(new GsonConverter(gsonBuilder.create()))
		.setLoginEndpoint(TEST_URL + EducappAPI.TOKEN_PATH)
		.setUsername("pepe")
		.setPassword("pass")
		.setClientId("mobile")
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		.create(EducappAPI.class);
		
		
		ArrayList<Course> courses = Lists.newArrayList(educappUser1.fetchCourseList());
		
		boolean result = 
				educappUser1.removeStudentFromCourse(3, courses.get(0).getCourseTag());
		assertTrue(result);
		
	}
	
	@Test
	public void PManageAssistanceControls(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Calendar.class, new CustomRetrofitCaldendarDeSerializer());
		gsonBuilder.registerTypeAdapter(GregorianCalendar.class, new CustomRetrofitGregorianCalendarSerializer());
		
		EducappAPI userManager = new SecuredRestBuilder()
		.setConverter(new GsonConverter(gsonBuilder.create()))
		.setLoginEndpoint(TEST_URL + EducappAPI.TOKEN_PATH)
		.setUsername("pepe")
		.setPassword("pass")
		.setClientId("mobile")
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		.create(EducappAPI.class);
		
		EducappAPI userTeacher = new SecuredRestBuilder()
		.setConverter(new GsonConverter(gsonBuilder.create()))
		.setLoginEndpoint(TEST_URL + EducappAPI.TOKEN_PATH)
		.setUsername("mihail")
		.setPassword("pass")
		.setClientId("mobile")
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		.create(EducappAPI.class);
		
		//We add two students to a course.
		String courseTag = "METEO0402017"+UUID.randomUUID().getLeastSignificantBits();
		userManager.createCourse(
				courseTag,
				"Análisis de los efectos de la lluvia sobre la fosa de las Marianas.",
				"La lluvia (del lat. pluvia) es un fenómeno atmosférico de "
				+ "tipo acuático que se inicia con la condensación del "
				+ "vapor de agua contenido en las nubes.\n"
				+ "En este curso, analizaremos como el aguilla ese extraño "
				+ "que cae del cielo acaba en la Fosa de las Marianas, y por"
				+ "qué demonios le mola tanto a Chtulu.",
				100,
				CustomDateDeserializerSerializer.dateFormatter.format(Calendar.getInstance().getTime()),
				"2017-12-12");
		
		userManager.assignStudentToCourse(5, courseTag);
		userManager.assignStudentToCourse(3, courseTag);
		userManager.assignStudentToCourse(4, courseTag);
		
		//We get a basic assistance control list to fill:
		Collection<AssistanceControl> list = userTeacher
				.getNewAssistanceControlForCourse(courseTag, "2017-12-12");
		for(AssistanceControl a : list){
			System.out.println(a.getEnrollmentReference().getStudent().getNif()+"@"
					+a.getEnrollmentReference().getCourse().getCourseTag()+"__"
					+a.getIsPresent());
			a.setIsPresent(true);
		}
		Collection<AssistanceControl> finalResult = userTeacher
				.addNewAssistanceControlForCourse(list);
		
		
		for(AssistanceControl item : finalResult){
			System.out.println(item.getEnrollmentReference().getStudent().getNif()+"@"
					+item.getEnrollmentReference().getCourse().getCourseTag()+"__"
					+item.getIsPresent());
			assertTrue(item.getIsPresent());
		}
		assertTrue(list.size() == finalResult.size());
		
	}
	
	@Test
	public void QnotificationGenerationAndRetrieval(){
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Calendar.class, new CustomRetrofitCaldendarDeSerializer());
		gsonBuilder.registerTypeAdapter(GregorianCalendar.class, new CustomRetrofitGregorianCalendarSerializer());
		
		EducappAPI userManager = new SecuredRestBuilder()
		.setConverter(new GsonConverter(gsonBuilder.create()))
		.setLoginEndpoint(TEST_URL + EducappAPI.TOKEN_PATH)
		.setUsername("pepe")
		.setPassword("pass")
		.setClientId("mobile")
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		.create(EducappAPI.class);
		
		EducappAPI userFollower = new SecuredRestBuilder()
		.setConverter(new GsonConverter(gsonBuilder.create()))
		.setLoginEndpoint(TEST_URL + EducappAPI.TOKEN_PATH)
		.setUsername("god")
		.setPassword("pass")
		.setClientId("mobile")
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		.create(EducappAPI.class);
		
		EducappAPI userTeacher = new SecuredRestBuilder()
		.setConverter(new GsonConverter(gsonBuilder.create()))
		.setLoginEndpoint(TEST_URL + EducappAPI.TOKEN_PATH)
		.setUsername("mihail")
		.setPassword("pass")
		.setClientId("mobile")
		.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
		.setEndpoint(TEST_URL).setLogLevel(LogLevel.FULL).build()
		.create(EducappAPI.class);
		
		//We add two students to a course.
		String courseTag = UUID.randomUUID().toString();
		userManager.createCourse(courseTag,
				UUID.randomUUID().toString(),
				"This is a sample description. Woah! I'm so cute.",
				100,
				CustomDateDeserializerSerializer.dateFormatter.format(Calendar.getInstance().getTime()),
				"2035-12-12");
		
		userManager.assignStudentToCourse(5, courseTag);
		userManager.assignStudentToCourse(3, courseTag);
		userManager.assignStudentToCourse(4, courseTag);
		
		//We get a basic assistance control list to fill:
		Collection<AssistanceControl> list = userTeacher
				.getNewAssistanceControlForCourse(courseTag, 
						CustomDateDeserializerSerializer.dateFormatter.format(
								Calendar.getInstance().getTime()));
		
		for(AssistanceControl a : list){
			System.out.println(a.getEnrollmentReference().getStudent().getNif()+"@"
					+a.getEnrollmentReference().getCourse().getCourseTag()+"__"
					+a.getIsPresent());
			a.setIsPresent(false);
		}
		Collection<AssistanceControl> finalResult = userTeacher
				.addNewAssistanceControlForCourse(list);
		
		//We check just in case, this assistance control submission
		//actually worked.
		for(AssistanceControl item : finalResult){
			System.out.println(item.getEnrollmentReference().getStudent().getNif()+"@"
					+item.getEnrollmentReference().getCourse().getCourseTag()+"__"
					+item.getIsPresent());
			assertTrue(!item.getIsPresent());
		}
		assertTrue(list.size() == finalResult.size());
		
		//Now, we will test the notifications themselves.
		//We should have at least two, as the default God
		//user is following two students who missed their
		//classes.
		
		Collection<Notification> notifs = userFollower
				.getAllNotificationsDestinedToUser();
		assertTrue(list.size() >= 2);
		Iterator<Notification> it = notifs.iterator();
		while(it.hasNext()){
			Notification n = it.next();
			System.out.println("Notification "+n.getId()+" for "
					+n.getUserToReceiveNotification().getFirstName() + " "
					+n.getUserToReceiveNotification().getLastName()
					+" because mister "
					+n.getCausingUser().getFirstName()+ " "
					+n.getCausingUser().getLastName()
					+" did something evil: "+n.getText());
		}
	}
}
