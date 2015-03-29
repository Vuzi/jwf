package fr.vuzi.webframework.renderer;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import fr.vuzi.webframework.context.IContext;

/**
 * JSON renderer. The rendered object is taken in the <b>model</b> field in the context
 * attributes. The object is displayed by its public fields using reflexivity
 * 
 * @author Vuzi
 *
 */
public class RendererJSON implements IRenderer {

	@Override
	public String render(IContext context) throws Exception {
		try {
			Object model = context.getAttribute("model");

			if (model != null)
				return renderObject(model);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * Render array using reflexivity
	 * @param model The object to display
	 * @return The rendered object
	 * @throws Exception
	 */
	public String renderArray(Object model) throws Exception {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		
		if (model != null) {
			sb.append(" [ ");
			
			int length = Array.getLength(model);
		    for (int i = 0; i < length; i ++) {
		    	
				if(first)
					first = false;
				else
					sb.append(", ");
				
		        sb.append(renderObject(Array.get(model, i)));
		    }
			
			
			sb.append(" ] ");
		}
		
		return sb.toString();
	}
	
	/**
	 * Render object using reflexivity
	 * @param model The object to display
	 * @return The rendered object
	 * @throws Exception
	 */
	public String renderObject(Object model) throws Exception {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		
		if (model != null) {
			if(model.getClass().isPrimitive()) {
				// Primitive
				sb.append(" " + model + " ");
			} else if (model.getClass().equals(String.class)) {
				// String
				sb.append("\"" + model + "\"");
			} else if (model.getClass().equals(Integer.class) || model.getClass().equals(Double.class) || model.getClass().equals(Boolean.class)) {
				// Numerical value
				sb.append(" " + model + " ");
			} else if(model.getClass().isArray()) {
				return renderArray(model);
			} else {
				// Other object
				sb.append("{ ");
	
				for (Field field : model.getClass().getFields()) {
					String key = "\"" + field.getName() + "\"";
					String value = renderObject(field.get(model));
	
					// Display field values
					if(first)
						first = false;
					else
						sb.append(",");
	
					sb.append(" " + key + " : " + value);
				}
	
				sb.append(" }");
			}
		} else {
			return "null";
		}

		return sb.toString();
	}

	@Override
	public String getHttpType() {
		return "application/json";
	}
	
}
