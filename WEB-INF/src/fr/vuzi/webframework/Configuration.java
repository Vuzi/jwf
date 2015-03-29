package fr.vuzi.webframework;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

public class Configuration {

	/**
	 * Velocity current define
	 */
	public static String CURRENT = "__CURRENT__";
	
	/**
	 * URI root
	 */
	public static String URIroot = "/default/";
	
	/**
	 * Root path
	 */
	public static File root = new File("default");
	
	/**
	 * Velocity rendering order
	 */
	public static Map<Integer, List<String>> velocityRenderingOrder;
	
	public static List<String> actionClassnames;
	
	/**
	 * Configuration constructor
	 */
	private Configuration() {}
	
	public static void initWithConfigurationFile(File configurationFile) throws Exception {

		JsonFactory jfactory = new JsonFactory();
		JsonParser jParser = jfactory.createJsonParser(configurationFile);
		JsonToken token;
		
		while((token = jParser.nextToken()) != null) {
			switch(token) {
				case FIELD_NAME:
					String fieldName = jParser.getCurrentName();
					
					if(fieldName.equals("actions"))
						initActionClassnames(jParser);
					else if(fieldName.equals("velocity_priority"))
						initVelocityRenderingOrder(jParser);
					else if(fieldName.equals("URI"))
						initURI(jParser);
					
				case START_OBJECT:
				case END_OBJECT:
				case VALUE_FALSE:
				case VALUE_NULL:
				case VALUE_NUMBER_FLOAT:
				case VALUE_NUMBER_INT:
				case VALUE_STRING:
				case VALUE_TRUE:
				case NOT_AVAILABLE:
				case END_ARRAY:
				case START_ARRAY:
				case VALUE_EMBEDDED_OBJECT:
				default:
					break;
			}
		}
	}
	
	private static void initActionClassnames(JsonParser jParser) throws Exception {

		actionClassnames = new ArrayList<String>();
		JsonToken token = jParser.nextToken();
		
		if(token != JsonToken.START_ARRAY)
			throw new Exception("Invalid token in JSON configuration file"); /// TODO Only arrays

		token = jParser.nextToken();
		
		while(token == JsonToken.VALUE_STRING) {
			actionClassnames.add(jParser.getText());
			token = jParser.nextToken();
		}
		
		if(token == JsonToken.END_ARRAY)
			return;
		else
			throw new Exception("Invalid token in JSON configuration file");
	}
	
	private static void initURI(JsonParser jParser) throws Exception {

		JsonToken token = jParser.nextToken();
		
		switch(token) {
			case VALUE_STRING:
				URIroot = jParser.getText();
				return;
			default:
				throw new Exception("Invalid token in JSON configuration file"); // Only string
		}
	}
	
	private static void initVelocityRenderingOrder(JsonParser jParser) throws Exception {

		ArrayList<Object[]> renderOrder = new ArrayList<Object[]>();
		Stack<String> stack = new Stack<String>();
		int returned = 1;

		JsonToken token = jParser.nextToken();
		
		do {
			switch(token) {
			case START_OBJECT: // Start object, level up
				if((token = jParser.nextToken()) == JsonToken.END_OBJECT)
					returned = 0; // Empty object, return to 0
				break;
			case FIELD_NAME: {
					String fieldName = jParser.getCurrentName();
					JsonToken nextToken = jParser.nextToken();
					
					if(nextToken == JsonToken.START_OBJECT) {
						stack.push(fieldName);
						token = nextToken;
						returned = 1;
						break;
					} else if(nextToken == JsonToken.START_ARRAY)
						break;

					// Leaf that can be rendered at level 'level'
					if(!CURRENT.equals(fieldName)) // Ignore "__CURRENT__"
						renderOrder.add(new Object[] { fieldName, 1 });
					
					token = jParser.nextToken();
				}
				break;
			case END_OBJECT:
				if(stack.size() > 0) // If anything to render
					renderOrder.add(new Object[] { stack.pop(), ++returned });
				else
					returned = 0;
				token = jParser.nextToken();
				break;
			case VALUE_FALSE:
			case VALUE_NULL:
			case VALUE_NUMBER_FLOAT:
			case VALUE_NUMBER_INT:
			case VALUE_STRING:
			case VALUE_TRUE:
				break;
			case NOT_AVAILABLE:
			case END_ARRAY:
			case START_ARRAY:
			case VALUE_EMBEDDED_OBJECT:
			default:
				throw new Exception("Invalid token in JSON configuration file");
			}
		} while (returned > 0);
		
		// Set values
		velocityRenderingOrder = new HashMap<Integer, List<String>>();
		for(Object[] element : renderOrder) {
			ArrayList<String> currentOrder = (ArrayList<String>) velocityRenderingOrder.get(element[1]);
			
			if(currentOrder == null) {
				currentOrder = new ArrayList<String>();
				velocityRenderingOrder.put((Integer) element[1], currentOrder);
			}
			
			currentOrder.add((String) element[0]);
		}
		
		velocityRenderingOrder = Collections.unmodifiableMap(velocityRenderingOrder);
	}
}
