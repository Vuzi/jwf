package fr.vuzi.webframework.context;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import fr.vuzi.webframework.Utils;

/**
 * Default context implementation, using the tomcat session handling
 * 
 * @author Vuzi
 *
 */
public class Context implements IContext {

	/**
	 * The servlet request
	 */
	private HttpServletRequest request;
	
	/**
	 * The servlet response
	 */
	private HttpServletResponse response;

	/**
	 * Properties hash map
	 */
	private Map<String, String[]> properties;
	
	/**
	 * Uploaded files hash map
	 */
	private Map<String, File> files;
	
	/**
	 * Fragments hash map
	 */
	private Map<String, String> fragments;
	
	/**
	 * Folder used to store files
	 */
	private static File tmpFolder = new File("../tmp");
	
	/**
	 * Response writer
	 */
	private PrintWriter writer;

	/**
	 * Renderer type
	 */
	private String renderedType;

	/**
	 * Action classname
	 */
	private String actionClassname;

	/**
	 * Context default implementation constructor
	 * @param request The request
	 * @param response The response
	 */
	public Context(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		
		init();
	}

	// ============ Context initialization ============
	
	/**
	 * Initialization main function
	 */
	private void init() {
		fragments = new HashMap<String, String>();
		properties = new HashMap<String, String[]>(request.getParameterMap());
		files = new HashMap<String, File>();

		initProperties();
		initRendererType();
	}
	
	/**
	 * Initialize the properties according to all the given values (get, post, multipart)
	 * and upload all the given files
	 */
	private void initProperties() {
		long unixTime = System.currentTimeMillis() / 1000L;
		
		try {
			if(ServletFileUpload.isMultipartContent(request)) {
			    ServletFileUpload uploader = new ServletFileUpload(new DiskFileItemFactory());
			    List<FileItem> items = uploader.parseRequest(request);
			      
			    for(FileItem item : items) {
			    	if(item.isFormField()) {
			    		// Form item
			    		properties.put(item.getFieldName(), Utils.appendToArray(properties.get(item.getFieldName()), item.getString()));
			    	} else {
			    		if(item.getName() != null && !item.getName().isEmpty()) {
				    		// File
				    		File f = new File(tmpFolder.getAbsolutePath() + "/" + item.getName() + unixTime);
							
				    		if(f.exists() || f.createNewFile()) {
					    		item.write(f);
					    		files.put(item.getName(), f);
				    		}
			    		}
			    	}
			    }
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
			// TODO
			//JwfErrorHandler.displayError(this, 500, "Error while uploading file : " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			// TODO
			//JwfErrorHandler.displayError(this, 500, "Error while retrieving sand values : " + e.getMessage());
		}
	}
	
	/**
	 * Initialize the renderer type of the request. The default type is HTML, but
	 * if the value "type" is set in the properties (either manually in the
	 * request or by the action) then the renderer type is modified according to
	 * the new given value
	 */
	private void initRendererType() {
		if(properties.containsKey("type")) {
			this.renderedType = getParameterUnique("type");
		}
	}

	// ============ Request context and values ============
	
	@Override
	public HttpServletRequest getRequest() {
		return request;
	}

	@Override
	public HttpServletResponse getResponse() {
		return response;
	}

	@Override
	public String[] getParameter(String key) {
		return properties.get(key);
	}

	@Override
	public String getParameterUnique(String key) {
		String[] tmp = properties.get(key);
		
		if(tmp != null && tmp.length > 0)
			return tmp[0];
		else
			return null;
	}

	@Override
	public void setParamater(String key, String[] values) {
		properties.put(key, values);
	}

	@Override
	public void addParameter(String key, String[] values) {
		setParamater(key, Utils.appendToArray(properties.get(key), values));
	}

	@Override
	public void addParameter(String key, String value) {
		setParamater(key, Utils.appendToArray(properties.get(key), value));
	}

	@Override
	public void setAttribute(String key, Object value) {
		request.setAttribute(key, value);
	}

	@Override
	public Object getAttribute(String key) {
		return request.getAttribute(key);
	}

	// ============ Uploaded files handling ============
	
	@Override
	public File[] getUploadedFiles() {
		File[] tmp = new File[files.size()];
		int i = 0;
		
		for(File f : files.values()) {
			tmp[i++] = f;
		}
		return tmp;
	}

	@Override
	public void removeUploadedFiles() {
		for(File f : files.values()) {
			f.delete();
		}
		
		files.clear();
	}
	
	// ============ User session & credentials ============

	@Override
	public void resetSession() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSessionAttribute(String key, Object value) {
		request.getSession().setAttribute(key, value);
	}

	@Override
	public Object getSessionAttribute(String key) {
		return request.getSession().getAttribute(key);
	}

	@Override
	public String[] getUserCredentials() {
		return (String[]) getSessionAttribute("user-cr");
	}
	
	// ============ Response informations ============

	@Override
	public String getFragment(String key) {
		return fragments.get(key);
	}
	
	@Override
	public Map<String, String> getFragmenst() {
		return fragments;
	}
	
	@Override
	public void setFragment(String key, String fragment) {
		fragments.put(key, fragment);
	}

	@Override
	public Writer getResponseWriter() throws IOException {
		if(writer == null)
			writer = response.getWriter();
		
		return writer;
	}

	@Override
	public String getRendererType() {
		return renderedType;
	}

	@Override
	public void setRenderType(String renderedType) {
		this.renderedType = renderedType;
	}

	@Override
	public void setActionClassname(String classname) {
		this.actionClassname = classname;
	}

	@Override
	public String getActionClassname() {
		return actionClassname;
	}

}
