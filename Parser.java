
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;

public class Parser {

	ArrayList<String> keys = new ArrayList();

	public JsonObject parseJson(JsonObject obj) {
		StringWriter stWriter = new StringWriter();
		JsonWriter jsonWriter = Json.createWriter(stWriter);
		jsonWriter.writeObject(obj);
		jsonWriter.close();

		String jsonData = stWriter.toString();

		JsonParser parser = Json.createParser(new StringReader(jsonData));

		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonObject model = null;

		while (parser.hasNext()) {
			JsonParser.Event event = parser.next();
			switch (event) {
			case START_OBJECT:
				break;
			case END_OBJECT:
				if (parser.hasNext()) // or you can use if(!key.isEmpty());
					keys.remove(keys.size() - 1);
				break;
			case VALUE_FALSE:
				builder.add(combineKeys(), false);
				keys.remove(keys.size() - 1);
				break;
			case VALUE_NULL:
				builder.addNull(combineKeys());
				keys.remove(keys.size() - 1);
				break;
			case VALUE_TRUE:
				builder.add(combineKeys(), true);
				keys.remove(keys.size() - 1);
				break;
			case KEY_NAME:
				keys.add(parser.getString());
				break;
			case VALUE_STRING:
				builder.add(combineKeys(), parser.getString());
				keys.remove(keys.size() - 1);
				break;
			case VALUE_NUMBER:
				builder.add(combineKeys(), parser.getInt());
				keys.remove(keys.size() - 1);
				break;
			}
		}

		return builder.build();

	}

	private String combineKeys() {
		Iterator it = keys.iterator();
		StringBuffer finalKey = new StringBuffer("" + it.next());

		while (it.hasNext()) {
			finalKey.append(".");
			finalKey.append(it.next());
		}

		return finalKey.toString();
	}

	public static void main(String[] args) {

		StringBuilder sb = new StringBuilder();

		BufferedReader br = null;
		try {

			br = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Type parse on the next line after providing json \n");
			System.out.println("Please enter a valid JSON now: ");
			while (true) {

				String input = br.readLine().trim();

				if ("parse".equals(input)) {
					break;
				}

				sb.append(input.trim());

			}
			System.out.println("Input JSON: " + sb + "\n");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		InputStream stream = new ByteArrayInputStream(sb.toString().getBytes());

		JsonReader reader = Json.createReader(stream);
		JsonObject model = null;
		try {
			model = reader.readObject();
		} catch (JsonException j) {
			throw new Error("please enter a valid json");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Parser parser = new Parser();
		JsonObject parsedModel = parser.parseJson(model);

		StringWriter stWriter = new StringWriter();
		Map<String, Object> properties = new HashMap<String, Object>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);

		JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
		JsonWriter jsonWriter = writerFactory.createWriter(stWriter);

		jsonWriter.writeObject(parsedModel);
		jsonWriter.close();

		String jsonData = stWriter.toString();
		System.out.println("Output JSON: \n" + jsonData);
	}
}
