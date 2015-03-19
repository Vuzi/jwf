package jwf.action.user;

import java.io.IOException;
import java.io.PrintWriter;

import jwf.action.AActionCredential;
import jwf.context.Context;
import jwf.error.JwfErrorHandler;
import jwf.message.JwfMessage;
import jwf.renderer.Renderer;
import jwf.user.DbUser;
import jwf.user.User;

import org.esgi.web.framework.context.interfaces.IContext;

public class ActionUserLogin extends AActionCredential {

	@Override
	public void proceed(IContext context) {
		// Get login and password
		String login = ((Context)context).getParameterUnique("login");
		String pass  = ((Context)context).getParameterUnique("password");
		
		context._getResponse().setContentType("text/html");

		if(login != null && !login.isEmpty() && pass != null && !pass.isEmpty()) {
			// The user wants to connect
			User user = DbUser.getInstance().getUserByLoginAndPassword(login, pass);
			if(user != null) {
				
				// Save session
				context.setSessionAttribute("user-id", user.ID);
				context.setSessionAttribute("user-cr", user.credentials);
				
				// Display message
				try {
					context.setAttribute("model", new JwfMessage("Connected with user " + login));
					context._getResponse().getWriter().write(new Renderer().render(context));
				} catch (IOException e) {
					JwfErrorHandler.displayError(context, 500, "error while writting response : " + e.getMessage());
					e.printStackTrace();
				}
				return;
				
			} else
				JwfErrorHandler.displayError(context, 403, "no user with the login/password provided");
		}
	}

	public void displayLoginPage(PrintWriter out) {
		out.print("<form method='POST' >");
		out.print("<input type'text' name='login' placeholder='login' /><br/>");
		out.print("<input type'password' name='password' placeholder='password' /><br/>");
		out.print("<input type='submit' value='Connect' >");
		out.print("</form>");
	}
	
}
