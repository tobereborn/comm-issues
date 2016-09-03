package com.github.tbr.common.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

/**
 * The Tree Model prepares an in-memory tree representation of a JSON document.
 * It is quite similar to DOM parser in XML.
 */
public class TreeModel {
	private ObjectMapper mapper;

	public TreeModel() {
		this.mapper = new ObjectMapper();
		this.mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
	}

	public SimplePojo readValue(String json) {
		JsonNode root = null;
		try {
			root = mapper.readTree(json);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (root == null) {
			return null;
		}

		SimplePojo pojo = new SimplePojo();

		String strField = root.path("strField").asText();
		pojo.setStrField(strField);
		int intField = root.path("intField").asInt();
		pojo.setIntField(intField);
		List<Integer> list = new ArrayList<Integer>();
		Iterator<JsonNode> it = root.path("intArray").getElements();
		while (it.hasNext()) {
			list.add(it.next().asInt());
		}
		Integer[] intArray = list.toArray(new Integer[] {});
		int[] copy = new int[intArray.length];
        for(int i=0;i<intArray.length;i++){
        	copy[i]=intArray[i];
        }
		pojo.setIntArray(copy);

		return pojo;
	}

	public String writeValue(SimplePojo pojo) {
		ObjectNode root = mapper.createObjectNode();
		ArrayNode arrayNode = mapper.createArrayNode();
		int[] array = pojo.getIntArray();
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				arrayNode.add(array[i]);
			}
		}

		root.put("strField", pojo.getStrField());
		root.put("intField", pojo.getIntField());
		root.put("intArray", arrayNode);

		try {
			return mapper.writeValueAsString(root);
		} catch (JsonGenerationException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		TreeModel tree = new TreeModel();
		SimplePojo pojo = new SimplePojo();
		pojo.setStrField("simple pojo");
		pojo.setIntField(1);
		pojo.setIntArray(new int[] { 1, 2, 3 });
		pojo.setBooleanField(true);
		String json = tree.writeValue(pojo);
		System.out.println(json);
		SimplePojo decPojo = tree.readValue(json);
		System.out.println(decPojo);
	}

}
