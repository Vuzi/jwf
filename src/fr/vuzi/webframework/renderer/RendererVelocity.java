package fr.vuzi.webframework.renderer;

import java.io.StringWriter;
import java.util.List;
import java.util.Map.Entry;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import fr.vuzi.webframework.Configuration;
import fr.vuzi.webframework.context.IContext;
import fr.vuzi.webframework.dispatcher.IDispatcher;

/**
 * Velocity renderer. This renderer needs to use the dispatcher to generate its fragments
 * 
 * @author Vuzi
 *
 */
public class RendererVelocity implements IRenderer {

	private IDispatcher dispatcher;

	/**
	 * Velocity renderer
	 * @param dispatcher The dispatcher
	 */
	public RendererVelocity(IDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
	
	@Override
	public String render(IContext context) throws Exception {
		// First, generate the main HTML element of the page
		context.setFragment(Configuration.CURRENT, renderTemplate(context, context.getActionClassname()));

		// For each element, dispatch then render (by priority)
		for(Entry<Integer, List<String>> entry : Configuration.velocityRenderingOrder.entrySet()) {
			dispatcher.dispatch(context, dispatcher.instantiate(entry.getValue(), context)); // Do the actions
			renderTemplates(context, entry.getValue()); // Render the templates
		}

		// Reunite everything in the main view
		return renderTemplate(context, "main");
	}
	
	/**
	 * Render a list of template, and put them in the context
	 * @param context The context
	 * @param templatesNames The list of templates
	 * @throws Exception
	 */
	public void renderTemplates(IContext context, List<String> templatesNames) throws Exception {
		for(String templateName : templatesNames)
			context.setFragment(templateName, renderTemplate(context, templateName));
	}

	/**
	 * Generate the string result for a given template
	 * @param context The context
	 * @param templateName The template name
	 * @return The string result
	 */
	public String renderTemplate(IContext context, String templateName) {

		// TODO : Singleton
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, Configuration.root + "/WEB-INF/templates/");
        ve.setProperty("input.encoding", "UTF-8");
        ve.setProperty("output.encoding", "UTF-8");
        ve.init();
        
        // Add our context to the velocity context
		VelocityContext vcontext = new VelocityContext();
		vcontext.put("context", context);
		
		// Get the template
		Template t = ve.getTemplate(templateName + ".vm");
		StringWriter sw = new StringWriter();
		t.merge(vcontext, sw);
		
		return sw.toString();
	}
	
	@Override
	public String getHttpType() {
		return "text/html; charset=UTF-8";
	}
}
