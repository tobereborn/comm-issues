package com.github.tbr.common.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

/**
 * Streaming API reads and writes JSON content as discrete events. JsonParser
 * reads the data, whereas JsonGenerator writes the data. It is analogous to
 * Stax parser for XML.
 */
public class Streaming {
	private JsonFactory factory;

	public Streaming() {
		this.factory = new JsonFactory();
	}

	public String writeValue(SimplePojo pojo) {
		Writer writer = null;
		try {
			writer = new StringWriter();
			JsonGenerator gen = null;
			try {
				gen = factory.createJsonGenerator(writer);
				gen.writeStartObject();
				gen.writeStringField("strField", pojo.getStrField());
				gen.writeNumberField("intField", pojo.getIntField());
				gen.writeBooleanField("booleanField", pojo.isBooleanField());
				gen.writeFieldName("intArray");
				int[] array = pojo.getIntArray();
				if (array != null) {
					gen.writeStartArray();
					for (int i = 0; i < array.length; i++) {
						gen.writeNumber(array[i]);
					}
					gen.writeEndArray();
				}
				gen.writeEndObject();
				gen.close();

			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				closeGeneratorQuietly(gen);
			}

			return writer.toString();
		} finally {
			closeWriterQuietly(writer);
		}
	}

	public SimplePojo readValue(String json) {
		JsonParser parser = null;
		try {
			parser = factory.createJsonParser(json);
			SimplePojo pojo = new SimplePojo();
			while (parser.nextToken() != JsonToken.END_OBJECT) {
				String fieldName = parser.getCurrentName();
				if ("strField".equals(fieldName)) {
					parser.nextToken();
					pojo.setStrField(parser.getText());
				}
				if ("intField".equals(fieldName)) {
					parser.nextToken();
					pojo.setIntField(parser.getIntValue());
				}
				if ("booleanField".equals(fieldName)) {
					parser.nextToken();
					pojo.setBooleanField(parser.getBooleanValue());
				}
				if ("intArray".equals(fieldName)) {
					parser.nextToken();
					List<Integer> list = new ArrayList<Integer>();
					while (parser.nextToken() != JsonToken.END_ARRAY) {
						list.add(parser.getIntValue());
					}
					if (!list.isEmpty()) {
						int[] intArray = new int[list.size()];
						for (int i = 0; i < intArray.length; i++) {
							intArray[i] = list.get(i).intValue();
						}
						pojo.setIntArray(intArray);
					}
				}
			}

			return pojo;

		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			closeParserQuietly(parser);
		}

	}

	private void closeWriterQuietly(Writer writer) {
		if (writer != null) {
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
			}
		}
	}

	private void closeGeneratorQuietly(JsonGenerator gen) {
		if (gen != null) {
			try {
				gen.close();
			} catch (IOException e) {
			}
		}
	}

	private void closeParserQuietly(JsonParser parser) {
		if (parser != null) {
			try {
				parser.close();
			} catch (IOException e) {
			}
		}

	}

	public static void main(String[] args) {
		Streaming streaming = new Streaming();
		SimplePojo pojo = new SimplePojo();
		pojo.setStrField("simple pojo");
		pojo.setIntField(1);
		pojo.setBooleanField(true);
		pojo.setIntArray(new int[] { 1, 2, 3 });
		String json = streaming.writeValue(pojo);
		System.out.println(json);
		SimplePojo decPojo = streaming.readValue(json);
		System.out.println(decPojo);
	}

}
