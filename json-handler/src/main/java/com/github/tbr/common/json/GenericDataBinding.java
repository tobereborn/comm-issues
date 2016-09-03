package com.github.tbr.common.json;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.type.TypeReference;

/**
 * Generic data binding refers to mapping of JSON to any Java Object in means of
 * type reference.
 */
public final class GenericDataBinding {
	private ObjectMapper mapper;
	private TypeReference<ComplexPojo> ref;

	public GenericDataBinding() {
		this.mapper = new ObjectMapper();
		this.mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
		this.ref = new TypeReference<ComplexPojo>() {
		};
	}

	public ComplexPojo readValue(String json) {
		try {
			return mapper.readValue(json, ref);
		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String writeValue(ComplexPojo pojo) {
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
		GenericDataBinding gdb = new GenericDataBinding();
		SimplePojo simplePojo = new SimplePojo();
		simplePojo.setStrField("simple");
		simplePojo.setIntField(1);
		simplePojo.setBooleanField(true);
		ComplexPojo complexPojo = new ComplexPojo();
		complexPojo.setSimplePojo(simplePojo);
		complexPojo.setStrField("complex pojo");
		complexPojo.setIntField(1);
		complexPojo.setBooleanField(true);
		complexPojo.setIntArray(new int[] { 1, 2, 3 });
		String json = gdb.writeValue(complexPojo);
		System.out.println(json);
		ComplexPojo decPojo = gdb.readValue(json);
		System.out.println(decPojo);
	}
}
