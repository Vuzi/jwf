package jwf.controller;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jwf.action.AActionCredential;
import jwf.context.Context;
import jwf.error.JwfErrorHandler;
import jwf.router.Dispatcher;
import jwf.router.RewriteRule;
import jwf.router.Rewriter;

import org.esgi.web.framework.core.interfaces.IFrontController;
import org.esgi.web.framework.router.interfaces.IDispatcher;
import org.esgi.web.framework.router.interfaces.IRewriter;


public class FrontController extends HttpServlet implements IFrontController {

	private static final long serialVersionUID = 1L;
	
	public static String URIroot = "/jwf";
	
	private IRewriter rewriter;
	private IDispatcher dispatcher;
	private Context c;
	
	public void init() {
		rewriter = new Rewriter();
		dispatcher = new Dispatcher();

		// Login form
		rewriter.addRule(new RewriteRule(URIroot + "/user/login", "GET|POST", "jwf.action.user.ActionUserLogin"));
		
		// Logout page
		rewriter.addRule(new RewriteRule(URIroot + "/user/logout", "GET|POST", "jwf.action.user.ActionUserLogout"));

		// Add an user
		rewriter.addRule(new RewriteRule(URIroot + "/user/add", "POST", "jwf.action.user.ActionUserAdd"));

		// Search an user
		rewriter.addRule(new RewriteRule(URIroot + "/user/search/(.+)", "POST", "jwf.action.user.ActionUserSearch", new String[] { "user-login" }));
		
		// Delete an user
		rewriter.addRule(new RewriteRule(URIroot + "/user/delete/([0-9]+)", "POST", "jwf.action.user.ActionUserDelete", new String[] { "user-id" }));
		
		// List all 
		rewriter.addRule(new RewriteRule(URIroot + "/user/list", "GET", "jwf.action.user.ActionUserDisplayAll"));

		// List specific user (or self)
		rewriter.addRule(new RewriteRule(URIroot + "/user/?([0-9]+)?", "GET", "jwf.action.user.ActionUserDisplay", new String[] { "user-id" }));
		
		// Edit specific user (or self)
		rewriter.addRule(new RewriteRule(URIroot + "/user/?([0-9]+)?", "POST", "jwf.action.user.ActionUserEdit", new String[] { "user-id" }));
		
		
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) {
		handle(request, response);
	}
	
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) {
		try {
			rewriter.rewrite(c = new Context(request, response));
			
			if(checkClass(c)) {
				if(checkRights(c)) {
					dispatcher.dispatch(c);
				} else {
					// 403
					JwfErrorHandler.displayError(c, 403, "you doesn't have the rights to view this page");
				}
			} else {
				// 404
				JwfErrorHandler.displayError(c, 404, "the page doesn't exist");
			}
			
			c.removeUploadedFiles();
		} catch (Exception e) {
			e.printStackTrace(); // 500
			JwfErrorHandler.displayError(c, 500, "error while loading page : " + e);
		}
	}
	
	private boolean checkClass(Context c) {
		return c.getActionClass() != null && c.getActionClass().length() > 0;
	}
	
	private boolean checkRights(Context c) throws Exception {
		String[] credentialsNeeded = (String[]) Class.forName(c.getActionClass()).getMethod("getCredentials", null).invoke(null, null);
		return AActionCredential.hasCredential(credentialsNeeded, c.getUserCredentials());
	}
	
}
