package pl.bwohs.kainos.utilities;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class Json {
	
	public static <T> String objectToJson(T object) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
		
		try {
			String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
			return json;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "Error converting object to Json String";
		}
	}
	
	public static JsonNode stringToJsonObject(String jsonString) {
	    ObjectMapper objectMapper = new ObjectMapper();
	    try {
			JsonNode actualObj = objectMapper.readTree(jsonString);
			return actualObj;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	

}
