package jwf.action.user;

import java.io.IOException;

import jwf.action.AActionCredential;
import jwf.context.Context;
import jwf.error.JwfErrorHandler;
import jwf.message.JwfMessage;
import jwf.renderer.Renderer;
import jwf.user.DbUser;
import jwf.user.User;

import org.esgi.web.framework.context.interfaces.IContext;

public class ActionUserSearch extends AActionCredential {


	public static String[] getCredentials() {
		return new String[] { "user" } ;
	}

	@Override
	public void proceed(IContext context) {
		String login = ((Context) context).getParameterUnique("user-login");
		User[] users = new User[0];
		
		if(login != null && login.length() > 0) {
			users = DbUser.getInstance().getUsersByLogin(login);
		}
		
		try {
			// Display changes
			context.setAttribute("model", new JwfMessage(users));
			context._getResponse().setContentType("text/html");
			context._getResponse().getWriter().write(new Renderer().render(context));
			return;
		} catch (IOException e) {
			JwfErrorHandler.displayError(context, 500, "error while writting response : " + e.getMessage());
			e.printStackTrace();
		}
	}


}
