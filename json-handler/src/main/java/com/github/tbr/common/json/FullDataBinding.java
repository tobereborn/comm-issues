package com.github.tbr.common.json;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 * Full data binding refers to mapping of JSON to any Java Object in means of
 * class type.
 */
public final class FullDataBinding {
	private ObjectMapper mapper;

	public FullDataBinding() {
		this.mapper = new ObjectMapper();
		this.mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
	}

	public SimplePojo readValue(String json) {
		try {
			return mapper.readValue(json, SimplePojo.class);
		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String writeValue(SimplePojo pojo) {
		try {
			return mapper.writeValueAsString(pojo);
		} catch (JsonGenerationException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		FullDataBinding sdb = new FullDataBinding();
		SimplePojo pojo = new SimplePojo();
		pojo.setStrField("simple");
		pojo.setIntField(1);
		pojo.setBooleanField(true);
		pojo.setIntArray(new int[] { 1, 2, 3 });
		String json = sdb.writeValue(pojo);
		System.out.println(json);
		SimplePojo decPojo = sdb.readValue(json);
		System.out.println(decPojo);
	}
}
