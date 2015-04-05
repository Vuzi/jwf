package fr.vuzi.webframework.renderer;

import fr.vuzi.webframework.context.IContext;

/**
 * The renderer is used to generate the string which will be send
 * in the response. The renderer will also call the dispatcher to
 * dispatch its actions the more effective way
 * 
 * @author Vuzi
 *
 */
public interface IRenderer {

	/**
	 * Main method, call to perform the rendering of the given action
	 * and context class
	 * @param action The action to proceed
	 * @param context The action's context
	 * @return
	 */
	public String render(IContext context) throws Exception;

	/**
	 * Return the mime-type used with this renderer
	 * @return The renderer mime-type
	 */
	public String getHttpType();
}
