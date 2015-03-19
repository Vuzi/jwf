package jwf.context;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jwf.error.JwfErrorHandler;
import jwf.renderer.RendererTypes;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.esgi.web.framework.context.interfaces.IContext;
import org.esgi.web.framework.context.interfaces.IHtmlContext;

public class Context implements IContext {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private String actionClass;
	private RendererTypes renderedType = RendererTypes.HTML;

	private Map<String, String[]> properties;
	private Map<String, File> files;
	
	private static File tmpFolder = new File("../tmp");
	public static File root = new File("../res");
	
	/**
	 * Constructor.
	 * 
	 * @param request Request.
	 * @param response Response.
	 * @throws IOException
	 */
	public Context(HttpServletRequest request, HttpServletResponse response) throws IOException {
		this.request = request;
		this.response = response;
		
		init();
	}
	
	private void init() {

		// If the file doesn't exist
		if (!tmpFolder.exists()) {
			try {
				tmpFolder.mkdir();
			} catch (SecurityException se) {}

		}

		// If the file doesn't exist
		if (!root.exists()) {
			try {
				root.mkdir();
			} catch (SecurityException se) {}
		}
		
		try {

			// Normal
			properties = new HashMap<String, String[]>(request.getParameterMap());
			files = new HashMap<String, File>();
			
			// Multi-part
			if(ServletFileUpload.isMultipartContent(request)) {
			    ServletFileUpload uploader = new ServletFileUpload(new DiskFileItemFactory());
			    List<FileItem> items = uploader.parseRequest(request);
			      
			    for(FileItem item : items) {
			    	if(item.isFormField()) {
			    		// Form item
			    		properties.put(item.getFieldName(), appendToArray(properties.get(item.getFieldName()), item.getString()));
			    	} else {
			    		if(item.getName() != null && !item.getName().isEmpty()) {
				    		// File
				    		File f = new File(tmpFolder.getAbsolutePath() + "/" + item.getName());
							
				    		if(f.exists() || f.createNewFile()) {
					    		item.write(f);
					    		files.put(item.getName(), f);
				    		}
			    		}
			    	}
			    }
			}
			
			// Rendered type
			initRendererType();
			
		} catch (FileUploadException e) {
			e.printStackTrace();
			JwfErrorHandler.displayError(this, 500, "Error while uploading file : " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			JwfErrorHandler.displayError(this, 500, "Error while retreiving sended values : " + e.getMessage());
		}
	}
	
	private void initRendererType() {
		if(properties.containsKey("type")) {
			String type = getParameterUnique("type");
			
			if(type != null) {
				switch(type) {
				case "JSON" :
				case "json" :
					this.renderedType = RendererTypes.JSON;
					break;
				case "XML" :
				case "xml" :
					this.renderedType = RendererTypes.XML;
					break;
				case "HTML" :
				case "html" :
					this.renderedType = RendererTypes.HTML;
					break;
				}
			}
		}
	}
	
	/**
	 * Print debug informations.
	 * 
	 * @throws IOException
	 */
	public void printDebugInfos() throws IOException {
		
		response.getWriter().println("Context debug:");
		
		for(Entry<String, String[]> entry : properties.entrySet()) {
			for(int i= 0; i < entry.getValue().length; i++)
				response.getWriter().println(entry.getKey() + "[" + i + "]=" + entry.getValue()[i]);
		}
		
		for(File f : files.values()) {
			response.getWriter().println("file " + f.getName() + " uploaded (" + f.length() + ")");
		}
	}
	
	/**
	 * Relocate an array.
	 * 
	 * @param tab Tab to reallocate
	 * @param toAdd String to add.
	 * @return The reallocated string.
	 */
	private String[] appendToArray(String[] tab, String toAdd) {
		
		if(tab == null)
			return new String[] { toAdd };
		
		String[] newtab = new String[tab.length + 1];
		int i = 0;
		
		for(; i < tab.length; i++)
			newtab[i] = tab[i];
		newtab[i] = toAdd;
		
		return newtab;
	}
	
	@Override
	public void setActionClass(String actionClass) {
		this.actionClass = actionClass;
	}

	@Override
	public String getActionClass() {
		return actionClass;
	}

	@Override
	public HttpServletRequest _getRequest() {
		return request;
	}

	@Override
	public HttpServletResponse _getResponse() {
		return response;
	}

	@Override
	public Object getParameter(String key) {
		return properties.get(key);
	}
	
	public String getParameterUnique(String key) {
		String[] tmp = properties.get(key);
		
		if(tmp != null && tmp.length > 0)
			return tmp[0];
		
		return null;
	}
	
	public void setParamater(String key, String[] value) {
		properties.put(key, value);
	}

	@Override
	public String[] getParameterKeys() {
		String[] tmp = new String[properties.keySet().size()];
		int i = 0;
		
		for(String key : properties.keySet()) {
			tmp[i++] = key;
		}
		
		return tmp;
	}

	@Override
	public void setAttribute(String key, Object value) {
		request.setAttribute(key, value);
	}

	@Override
	public Object getAttribute(String key) {
		return request.getAttribute(key);
	}
	
	@Override
	public boolean resetSession() {
		request.getSession().invalidate();
		return true;
	}

	@Override
	public IHtmlContext toHtmlContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File[] getUploadedFiles() {
		File[] tmp = new File[files.size()];
		int i = 0;
		
		for(File f : files.values()) {
			tmp[i++] = f;
		}
		return tmp;
	}
	
	/**
	 * Remove all temporary files.
	 */
	public void removeUploadedFiles() {
		for(File f : files.values()) {
			f.delete();
		}
		
		files = new HashMap<String, File>();
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
	
	/**
	 * Get the renderer type.
	 * 
	 * @return The renderer type.
	 */
	public RendererTypes getRendererType() {
		return renderedType;
	}

}
