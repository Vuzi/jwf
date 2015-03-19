package jwf.action.user;

import java.io.IOException;

import jwf.action.AActionCredential;
import jwf.error.JwfErrorHandler;
import jwf.message.JwfMessage;
import jwf.renderer.Renderer;
import jwf.user.DbUser;

import org.esgi.web.framework.context.interfaces.IContext;

public class ActionUserDisplayAll extends AActionCredential {

	public static String[] getCredentials() {
		return new String[] { "user" } ;
	}

	@Override
	public void proceed(IContext context) {
		
		try {
			context.setAttribute("model", new JwfMessage(DbUser.getInstance().getAllUsers()));
			context._getResponse().setContentType("text/html");
			context._getResponse().getWriter().write(new Renderer().render(context));
			return;
		} catch (IOException e) {
			JwfErrorHandler.displayError(context, 500, "error while writting response : " + e.getMessage());
			e.printStackTrace();
		}
			
	}

}
