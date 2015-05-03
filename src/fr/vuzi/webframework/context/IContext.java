package fr.vuzi.webframework.context;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The context represent the context of a single request, and all its informations in
 * an unified and easy-to-access way
 * 
 * @author Vuzi
 *
 */
public interface IContext {

	public void init() throws Exception;
	
	// ============ Request context and values ============
	
	/**
	 * Get the raw request object
	 * @return The raw HTTP servlet request
	 */
	public HttpServletRequest getRequest();

	/**
	 * Get the raw response object
	 * @return The raw HTTP servlet response
	 */
	public HttpServletResponse getResponse();
	
	/**
	 * Get the parameter for a given key. Note that
	 * multiple parameter can be defined for a single
	 * key, as they are return in a string array. If no
	 * value are found, a null value is returned
	 * @param key The parameter key
	 * @return A string array containing the value(s), or null if not found
	 */
	public String[] getParameter(String key);
	
	/**
	 * Do the same things than getParameter, but only return the
	 * first value. If any more values are available for the given key,
	 * they'll be ignored and just the first will be returned. If no value is
	 * found, null is returned
	 * @param key The parameter key
	 * @return A string containing the value, or null if not found
	 */
	public String getParameterUnique(String key);

	/**
	 * Set a parameter value for a given key. If value are already present
	 * with this key, they will be overwritten
	 * @param key The parameter key
	 * @param values The parameter values
	 */
	public void setParamater(String key, String[] values);

	/**
	 * Add parameter value(s) for the given key
	 * @param key The parameter key
	 * @param values The values
	 */
	public void addParameter(String key, String[] values);
	
	/**
	 * Add parameter value for the given key
	 * @param key The parameter key
	 * @param value The value
	 */
	public void addParameter(String key, String value);
	
	/**
	 * Store an attribute
	 * @param key The attribute key
	 * @param value The attribute value
	 */
	public void setAttribute(String key, Object value);

	/**
	 * Get an attribute by its key
	 * @param key The attribute key
	 * @return The found attribute, or null if not found
	 */
	public Object getAttribute(String key);
	
	// ============ Uploaded files handling ============
	
	/**
	 * Return the uploaded files within this request context
	 * @return The uploaded file list
	 */
	public Map<String, File> getUploadedFiles();
	
	/**
	 * Remove the uploaded files
	 */
	public void removeUploadedFiles();
	
	// ============ User session & credentials ============

	/**
	 * Reset the current session
	 */
	public void resetSession();
	
	/**
	 * Set a value to the session attribute map. This value will
	 * be accessible as long as the user remains connected
	 * 
	 * @param key The attribute key
	 * @param value The value to store
	 */
	public void setSessionAttribute(String key, Object value);

	/**
	 * Get a value contained in the session attribute map
	 * 
	 * @param key The attribute key
	 * @return The found value, or null if not found
	 */
	public Object getSessionAttribute(String key);
	
	/**
	 * Get the connected user its credentials
	 * @return The user credentials
	 */
	public String[] getUserCredentials();
	
	// ============ Response informations ============
	
	/**
	 * Get a response fragment by id key. A response fragment is
	 * a string of a part of the final rendered page
	 * @param key The fragment key
	 * @return The fragment, or null if not found
	 */
	public String getFragment(String key);
	
	/**
	 * Return all the fragments
	 * @return All the fragments
	 */
	public Map<String, String> getFragmenst();	
	
	/**
	 * Set a response fragment for the given idA response fragment is
	 * a string of a part of the final rendered page
	 * @param key The fragment key
	 * @param fragment The page fragment
	 */
	public void setFragment(String key, String fragment);
	
	/**
	 * Return the response writer. This is where the response page
	 * must be written
	 * @return The response writer
	 * @throws IOException Thrown if an error occurred
	 */
	public Writer getResponseWriter() throws IOException;
	
	/**
	 * Get the renderer type
	 * @return The renderer type
	 */
	public String getRendererType();

	/**
	 * Set the renderer type.
	 * @param type The renderer type.
	 */
	public void setRenderType(String type);
	
	/**
	 * Set the action classname. This classname is used to instantiate the action class used
	 * to perform the action of the selected end-point
	 * @param classname The classname
	 */
	public void setActionClassname(String classname);
	
	/**
	 * Return the action classname, or null if never defined
	 * @return The action classname, or null if never defined
	 */
	public String getActionClassname();
	
	/**
	 * This method is called if the basic authentication is used on the server
	 * @param login
	 * @param password
	 */
	public void authentificate(String login, String password) throws Exception;

	/**
	 * Get the HTTP response status
	 * @return The HTTP response status
	 */
	public int getStatus();
	
	/**
	 * Set the HTTP response status
	 * @param status
	 */
	public void setStatus(int status);
}

