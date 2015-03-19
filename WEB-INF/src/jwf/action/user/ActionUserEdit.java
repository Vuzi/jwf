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

public class ActionUserEdit extends AActionCredential {


	public static String[] getCredentials() {
		return new String[] { "user", "admin" } ;
	}

	@Override
	public void proceed(IContext context) {
		String ID = ((Context) context).getParameterUnique("user-id");
		String login = ((Context) context).getParameterUnique("login");
		String pass = ((Context) context).getParameterUnique("password");
		String[] creds = (String[]) context.getParameter("credentials");
		User user;
		
		if(ID != null && ID.length() > 0) {
			user = DbUser.getInstance().getUserById(new Integer(ID));
		} else {
			user = DbUser.getInstance().getUserById((int) context.getSessionAttribute("user-id"));
		}
		
		if(user != null) {
			try {
				if(login != null && login.length() > 0)
					user.login = login;
				
				if(pass != null && pass.length() > 0)
					user.password = pass;
				
				if(creds != null)
					user.credentials = creds;
				
				// Display changes
				context.setAttribute("model", new JwfMessage(user));
				context._getResponse().setContentType("text/html");
				context._getResponse().getWriter().write(new Renderer().render(context));
				return;
			} catch (IOException e) {
				JwfErrorHandler.displayError(context, 500, "error while writting response : " + e.getMessage());
				e.printStackTrace();
			}
		} else
			JwfErrorHandler.displayError(context, 404, "error : no user with id '" + ID + "'");
	}


}
