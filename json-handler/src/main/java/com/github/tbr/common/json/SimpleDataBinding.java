package com.github.tbr.common.json;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 * Simple data binding refers to mapping of JSON to JAVA core data types in
 * means of map.
 */
public final class SimpleDataBinding {
	private static ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
		mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
	}

	private SimpleDataBinding() {
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> readValue(String json) {
		try {
			return mapper.readValue(json, Map.class);
		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String writeValue(Map<String, Object> map) {
		try {
			return mapper.writeValueAsString(map);
		} catch (JsonGenerationException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		SimplePojo pojo = new SimplePojo();
		pojo.setStrField("simple");
		pojo.setIntField(1);
		pojo.setBooleanField(true);
		pojo.setIntArray(new int[] { 1, 2, 3 });
		map.put("pojo", pojo);
		map.put("string", "map");
		map.put("int", 1);
		map.put("boolean", true);
		map.put("array", new int[] { 1, 2, 3 });
		String json = SimpleDataBinding.writeValue(map);
		System.out.println(json);
		Map<String, Object> decMap = SimpleDataBinding.readValue(json);
		System.out.println(decMap);
	}
}
