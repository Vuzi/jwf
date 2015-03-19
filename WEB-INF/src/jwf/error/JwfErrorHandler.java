package jwf.error;

import java.io.IOException;

import jwf.message.JwfMessage;
import jwf.renderer.Renderer;

import org.esgi.web.framework.context.interfaces.IContext;

/**
 * Error handler.
 * 
 * @author Vuzi
 *
 */
public class JwfErrorHandler {

	public static void displayError(IContext context, int errorNumber, String message) {
		try {
			context.setAttribute("model", new JwfMessage(true, new JwfError(errorNumber, "Error : " + message), null));
			context._getResponse().setContentType("text/html");
			context._getResponse().setStatus(errorNumber);
			context._getResponse().getWriter().write(new Renderer().render(context));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
