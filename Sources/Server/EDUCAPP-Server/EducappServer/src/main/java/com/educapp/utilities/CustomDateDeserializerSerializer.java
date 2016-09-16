package com.educapp.utilities;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

/**
 * Custom serializer and deserializer classes
 * for calendar objects.
 * 
 * @author Harold Hormaechea Garcia
 *
 */
public class CustomDateDeserializerSerializer{
	public static final SimpleDateFormat dateFormatter= new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * Custom date serializer for jackson.
	 * 
	 * Used in the server, as jackson is the 
	 * JSON serializer.
	 * 
	 * @author Harold Hormaechea Garcia
	 *
	 */
	 public static class CustomDateSerializer extends JsonSerializer<Calendar> {
		 	@Override
		 	public Class<Calendar> handledType(){
		 		return Calendar.class;
		 	}
			
			@Override
			public void serialize(Calendar value,
					com.fasterxml.jackson.core.JsonGenerator jgen,
					com.fasterxml.jackson.databind.SerializerProvider provider)
					throws IOException, JsonProcessingException {
				jgen.writeStartObject();
				jgen.writeStringField("timeValue", (dateFormatter.format(value.getTime())));
				jgen.writeEndObject();
			}
	 }
	 
	 /**
	  * Custom date serializer/deserializer for RetroFit.
	  * 
	  * Used in clients which use RetroFit as the REST
	  * interface.
	  * 
	  * TODO: Make the friggin' serializer work. It works
	  * as stand-alone but won't work through retrofit.
	  * 
	  * @author Harold Hormaechea Garcia
	  *
	  */
	 public static class CustomRetrofitCaldendarDeSerializer implements
	    com.google.gson.JsonDeserializer<Calendar>
	 	{

		@Override
		public Calendar deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			Date date;
			JsonObject jsonObject = json.getAsJsonObject();
			try {
				date = dateFormatter.parse(jsonObject.get("timeValue").getAsString());
				Calendar cal=Calendar.getInstance();
				cal.setTime(date);
				return cal;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
	 }
	 
	 public static class CustomRetrofitGregorianCalendarSerializer implements
	    com.google.gson.JsonSerializer<GregorianCalendar>{

			@Override
			public JsonElement serialize(GregorianCalendar src, Type typeOfSrc,
					JsonSerializationContext context) {
				return new JsonPrimitive(dateFormatter.format(src.getTime()));
			}
	 }
	 
	 
	 

///////////////////////////////////////////
/////////////////////////////////////////// TEST
///////////////////////////////////////////
///////////////////////////////////////////
	 public static class CustomRetrofitSerializer implements com.google.gson.JsonSerializer<GregorianCalendar>{
		 @Override
			public JsonElement serialize(GregorianCalendar src, Type typeOfSrc,
					JsonSerializationContext context) {
				return new JsonPrimitive(dateFormatter.format(src.getTime()));
			}
	 }
	 
	 public static void main(String[] args){
		 Calendar cal = Calendar.getInstance();
		 GsonBuilder builder = new GsonBuilder();
	    builder.registerTypeAdapter(GregorianCalendar.class, new CustomRetrofitSerializer());
	    Gson gson = builder.create();
	    String json = gson.toJson(cal);
	    System.out.println("JSON: "+json);	
	 }
 }
