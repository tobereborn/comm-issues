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
	private static ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
		mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
	}

	private FullDataBinding() {
	}

	public static SimplePojo readValue(String json) {
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

	public static String writeValue(SimplePojo pojo) {
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
		SimplePojo pojo = new SimplePojo();
		pojo.setStrField("simple");
		pojo.setIntField(1);
		pojo.setBooleanField(true);
		pojo.setIntArray(new int[] { 1, 2, 3 });
		String json = FullDataBinding.writeValue(pojo);
		System.out.println(json);
		SimplePojo decPojo = FullDataBinding.readValue(json);
		System.out.println(decPojo);
	}
}
