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

public class ActionUserAdd extends AActionCredential {

	public static String[] getCredentials() {
		return new String[] { "user", "admin" } ;
	}

	@Override
	public void proceed(IContext context) {
		String login = ((Context) context).getParameterUnique("login");
		String pass = ((Context) context).getParameterUnique("password");
		String[] creds = (String[]) context.getParameter("credentials");
		
		if(login != null && login.length() > 0 &&
		   pass != null && pass.length() > 0 &&
		   creds != null) {
			try {
				User user = DbUser.getInstance().createUser(login, pass, creds);
				
				context.setAttribute("model", new JwfMessage(user));
				context._getResponse().setContentType("text/html");
				context._getResponse().getWriter().write(new Renderer().render(context));
			} catch (IOException e) {
				JwfErrorHandler.displayError(context, 500, "error while writting response : " + e.getMessage());
				e.printStackTrace();
			}
		} else
			JwfErrorHandler.displayError(context, 200, "one of the required field is empty");
	}

}
