package fr.vuzi.webframework.controller;

import java.io.File;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.vuzi.webframework.Configuration;
import fr.vuzi.webframework.action.IAction;
import fr.vuzi.webframework.context.Context;
import fr.vuzi.webframework.context.IContext;
import fr.vuzi.webframework.dispatcher.Dispatcher;
import fr.vuzi.webframework.dispatcher.IDispatcher;
import fr.vuzi.webframework.dispatcher.IRewriter;
import fr.vuzi.webframework.dispatcher.Rewriter;
import fr.vuzi.webframework.renderer.MainRenderer;


public abstract class AFrontController extends HttpServlet {

	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = -4921851913561526633L;

	/**
	 * Rewriter
	 */
	protected IRewriter rewriter;
	
	/**
	 * Dispatcher
	 */
	protected IDispatcher dispatcher;
	
	/**
	 * Main renderer
	 */
	protected MainRenderer renderer;
	
	@Override
	public void init() {
		// -- Configuration file --
		Configuration.root = new File(getRootDirectory());
		
		try {
			Configuration.initWithConfigurationFile(new File(Configuration.root + "/WEB-INF/conf/conf.json"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// -- Dispatcher --
		dispatcher = new Dispatcher();
		dispatcher.setPossibleActions(Configuration.actionClassnames);
		
		// -- Renderer --
		renderer = new MainRenderer(dispatcher);
		initRenderers();

		// -- Rewriter --
		rewriter = new Rewriter();
		initRewriterRules();

		// -- Thread safety --
		rewriter.lock();
		dispatcher.lock();
		renderer.lock();
	}
	
	/**
	 * Initialize the rewriter. This method should be overrided in order to add the rewrite rules.
	 * By default there is rewrite rules
	 */
	protected abstract void initRewriterRules();
	
	/**
	 * Initialize the main renderer. This method should be override in order to add renders to main
	 * renderer. By default, there is no renderers
	 */
	protected abstract void initRenderers();
	
	/**
	 * Return the root directory. This value is used to initialize the configuration and find
	 * resources and the configuration file
	 * @return The root directory path
	 */
	protected abstract String getRootDirectory();
	
	/**
	 * Called when an error occurred, and should handle the error
	 * @param context The context
	 * @param code The HTTP code
	 * @param message The error message
	 * @param cause The Exception that cause the error if any, null otherwise
	 */
	protected abstract void errorHandle(IContext context, int code, String message, Exception cause);
	
	protected void errorHandle(IContext context, int code, String message) {
		errorHandle(context, code, message, null);
	}
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) {
		handle(request, response);
	}

	/**
	 * Methods used to call the rewriter and select the action, check the credentials then send 
	 * the action to the dispatcher
	 * @param request The servlet request
	 * @param response The servlet response
	 */
	protected void handle(HttpServletRequest request, HttpServletResponse response) {
		//response.addHeader("Access-Control-Allow-Origin",  "*");
		IContext context = createContext(request, response); // Initialize the context
		
		try {
			context.init();
			IAction templateAction = rewriter.rewrite(context); // Find the action template
			
			if(checkClass(templateAction)) {
				if(checkCredentials(context, templateAction)) {
					renderer.render(context);
				} else {
					// 403
					errorHandle(context, 403, "you don't have the credentials to view this page");
				}
				
			} else {
				// 404
				errorHandle(context, 404, "the page doesn't exist");
			}
		} catch (Exception e) {
			e.printStackTrace(); // 500
			errorHandle(context, 500, "error while loading page", e);
		} finally {
			context.removeUploadedFiles();
		}
	}
	
	/**
	 * Check if an action is found for the current context. If the action is not in the
	 * template list and should, it may have failed to be instantiated in the initialization
	 * of the servlet
	 * @param classname The action classname
	 * @return True if an action is selected, false otherwise
	 */
	private boolean checkClass(IAction templateAction) {
		return templateAction != null;
	}
	
	/**
	 * Check if the user has the rights credentials to run the given action
	 * @param action The action to test
	 * @return True if the user can run the action, false otherwise
	 */
	protected boolean checkCredentials(IContext context, IAction action) {
		return action.hasCredentials(context.getUserCredentials());
	}
	
	/**
	 * Create the context for a request
	 * @param request The request
	 * @param response The response
	 * @return The created context
	 */
	protected IContext createContext(HttpServletRequest request, HttpServletResponse response) {
		return new Context(request, response);
	}
}
