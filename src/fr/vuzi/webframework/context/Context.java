package fr.vuzi.webframework.context;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

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
	}

	// ============ Context initialization ============
	
	/**
	 * Initialization main function
	 */
	public void init() throws Exception {
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		fragments = new HashMap<String, String>();
		properties = new HashMap<String, String[]>();
		files = new HashMap<String, File>();

		initProperties();
		initRendererType();
	}
	
	/**
	 * Initialize the properties according to all the given values (get, post, multipart)
	 * and upload all the given files
	 * @throws Exception 
	 * @throws IOException 
	 */
	private void initProperties() throws Exception {
		long unixTime = System.currentTimeMillis() / 1000L;
		
		if(ServletFileUpload.isMultipartContent(request)) {
		    ServletFileUpload uploader = new ServletFileUpload(new DiskFileItemFactory());
		    List<FileItem> items = uploader.parseRequest(request);
		      
		    for(FileItem item : items) {
		    	if(!item.isFormField()) {
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
		} else {
			// Ignore values, and get the values from the payload
			getPropertiesFromJSON(IOUtils.toString(request.getReader()));
		}
	}
	
	private void getPropertiesFromJSON(String br) throws JsonParseException, IOException {
		
		if(br.trim().isEmpty())
			return; // Ignore empty value
		
		ObjectMapper mapper = new ObjectMapper();
	    mapper.configure(Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
	    
	    HashMap<String, String[]> tmp = mapper.readValue(br, new TypeReference<HashMap<String, String[]>>(){});

	    // Merge both arrays
		for(Entry<String, String[]> entry : tmp.entrySet()) {
			properties.put(entry.getKey(), Utils.appendToArray(properties.get(entry.getKey()), entry.getValue()));
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
		request.getSession().invalidate();
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
		String[] credentials = (String[]) getSessionAttribute("user-cr");
		
		if(credentials == null)
			return new String[0];
		else
			return credentials;
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
