package fr.vuzi.webframework.renderer;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import fr.vuzi.webframework.Lockable;
import fr.vuzi.webframework.action.IAction;
import fr.vuzi.webframework.context.IContext;
import fr.vuzi.webframework.dispatcher.IDispatcher;

/**
 * Main renderer which dispatch rendering to other renderer using the context renderer type selected
 * @author Vuzi
 *
 */
public class MainRenderer implements Lockable {

	/**
	 * Dispatcher used to dispatch values.
	 */
	private IDispatcher dispatcher;

	/**
	 * Renderers by types. Note that the framework is delivered with ready-to-use
	 * renderers for JSON, xml, html and Velocity
	 */
	private Map<String, IRenderer> renderers;
	
	/**
	 * Default renderer type, used when no type could be selected
	 */
	private String defaultType;
	
	/**
	 * Charset used.
	 */
	private Charset charset = Charset.forName("UTF-8");
	
	/**
	 * Constructor of the main renderer
	 * 
	 * @param dispatcher The dispatcher that will be used by the renderer to perform its actions
	 *                   before rendering the result
	 */
	public MainRenderer(IDispatcher dispatcher) {
		this.dispatcher = dispatcher;
		this.renderers = new HashMap<String, IRenderer>();
	}

	/**
	 * Set the default type. This type is used when no type where explicitly selected nor
	 * in the rewrite rule nor in the request itself
	 * @param defaultType The default type
	 */
	public void setDefaultType(String defaultType) {
		this.defaultType = defaultType;
	}
	
	/**
	 * Return the default type
	 * @return The default type
	 */
	public String getDefaultType() {
		return defaultType;
	}
	
	/**
	 * Add a renderer to the contained renderer map, using the given type name. Note that
	 * any renderer with the same type will be overwritten
	 * @param type The renderer type
	 * @param renderer The renderer class
	 */
	public void addRenderer(String type, IRenderer renderer) {
		renderers.put(type, renderer);
	}
	
	/**
	 * Delete all the previous set renderers
	 */
	public void clearRenderer() {
		renderers.clear();
	}
	
	/**
	 * Return all the renderers
	 * @return All the renderers, by types
	 */
	public Map<String, IRenderer> getRenderers() {
		return renderers;
	}
	
	/**
	 * Return the renderer according to the selected rendering type, or by the default
	 * rendering type if no type is selected
	 * @param context The context
	 * @return The renderer instance to use
	 * @throws Exception Thrown if no renderer could be selected
	 */
	private IRenderer getRenderer(IContext context) throws Exception {
		
		IRenderer renderer = null;
		String rendererType = context.getRendererType();
		
		if(rendererType == null) {
			if(defaultType != null)
				renderer = renderers.get(defaultType);
		} else
			renderer = renderers.get(rendererType);
		
		if(renderer == null) { // No renderer could be selected
			context.setRenderType(defaultType); // Try to force default
			throw new Exception("No renderer type could be selected for this request");
		} else
			return renderer;
	}
	
	/**
	 * Render for the given context. This methods will dispatch all the actions which needs dispatching,
	 * and write the right mime-type and content according to the response renderer type selected
	 * @param context The context
	 * @throws Exception Thrown if an error occurred during the rendering
	 */
	public void render(IContext context) throws Exception {
		
		// Renderer to use
		IRenderer renderer = getRenderer(context);
		
		// Action to perform
		IAction action = dispatcher.instantiate(context.getActionClassname(), context);

		// Always dispatch the first action
		if(context.getActionClassname() != null)
			dispatcher.dispatch(context, action);
		
		// Display in the response
		if(action == null || (action != null && action.needRenderer())) {
			context.getResponse().setStatus(context.getStatus());
			context.getResponse().setCharacterEncoding(charset.displayName());
			context.getResponse().setContentType(renderer.getHttpType());

			String encoding = context.getRequest().getHeader("Accept-Encoding");

			OutputStream out = context.getResponse().getOutputStream();
			
			// Compress if possible
			if(encoding != null && encoding.contains("gzip")) {
				context.getResponse().addHeader("Content-Encoding", "gzip");
				out = new GZIPOutputStream(out);
			}

			// Write to the output
			out.write(renderer.render(context).getBytes(charset));
			out.flush();
			out.close();
		}
	}
	
	@Override
	public void lock() {
		renderers = Collections.unmodifiableMap(renderers); // Lock renderers map
	}

}
