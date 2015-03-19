package jwf.renderer;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import org.esgi.web.framework.context.interfaces.IContext;
import org.esgi.web.framework.renderer.interfaces.IRenderer;

public class RendererXML implements IRenderer {

	@Override
	public String render(IContext context) {
		try {
			Object model = context.getAttribute("model");

			if (model != null)
				return renderObject(model);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
	
	public String renderArray(Object model) throws Exception {
		StringBuilder sb = new StringBuilder();
		
		if (model != null) {
			sb.append(" <array> ");
			
			int length = Array.getLength(model);
		    for (int i = 0; i < length; i ++) {
		        sb.append("<value>" + renderObject(Array.get(model, i)) + "</value>");
		    }
			
			
			sb.append(" </array> ");
		}
		
		return sb.toString();
	}

	public String renderObject(Object model) throws Exception {
		StringBuilder sb = new StringBuilder();
		
		if (model != null) {
			if(model.getClass().isPrimitive()) {
				// Primitive
				sb.append(model);
			} else if (model.getClass().equals(String.class)) {
				// String
				sb.append(model);
			} else if (model.getClass().equals(Integer.class) || model.getClass().equals(Double.class) || model.getClass().equals(Boolean.class)) {
				// Numerical value
				sb.append(model);
			} else if(model.getClass().isArray()) {
				return renderArray(model);
			} else {
				// Other object
				sb.append("<object>");
	
				for (Field field : model.getClass().getFields()) {
					String key = field.getName();
					String value = renderObject(field.get(model));
	
					// Display field values
					sb.append("<field><name>" + key + "</name><value>" + value + "</value></field>");
				}
	
				sb.append("</object>");
			}
		} else {
			return "null";
		}

		return sb.toString();
	}
}
