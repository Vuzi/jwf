package jwf.action.user;

import java.io.IOException;

import jwf.action.AActionCredential;
import jwf.error.JwfErrorHandler;
import jwf.message.JwfMessage;
import jwf.renderer.Renderer;

import org.esgi.web.framework.context.interfaces.IContext;

public class ActionUserLogout extends AActionCredential {

	@Override
	public void proceed(IContext context) {
		
		context.resetSession();
				
		try {
			context.setAttribute("model", new JwfMessage("You are now disconnected"));
			context._getResponse().setContentType("text/html");
			context._getResponse().getWriter().write(new Renderer().render(context));
		} catch (IOException e) {
			JwfErrorHandler.displayError(context, 500, "error while writting response : " + e.getMessage());
			e.printStackTrace();
		}
	}
}
