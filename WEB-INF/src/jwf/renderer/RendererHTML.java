package jwf.renderer;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import org.esgi.web.framework.context.interfaces.IContext;
import org.esgi.web.framework.renderer.interfaces.IRenderer;

public class RendererHTML implements IRenderer {

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
			sb.append(" <table border=1 colspan=0> ");
			
			int length = Array.getLength(model);
		    for (int i = 0; i < length; i ++) {
		        sb.append("<tr><td>" + renderObject(Array.get(model, i)) + "</td></tr>");
		    }
			
			
			sb.append(" </table> ");
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
				sb.append("<ul>");
	
				for (Field field : model.getClass().getFields()) {
					String key = field.getName();
					String value = renderObject(field.get(model));
	
					// Display field values
					sb.append("<li><b>" + key + "</b> : " + value + "</li>");
				}
	
				sb.append("</ul>");
			}
		} else {
			return "null";
		}

		return sb.toString();
	}
}
