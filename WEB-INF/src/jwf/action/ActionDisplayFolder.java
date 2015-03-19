package jwf.action;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import jwf.context.Context;
import jwf.controller.FrontController;
import jwf.error.JwfErrorHandler;

import org.esgi.web.framework.action.interfaces.IAction;
import org.esgi.web.framework.context.interfaces.IContext;

public class ActionDisplayFolder implements IAction {

	private String createHeader(String baseURL, String requestedPath) {
		baseURL = baseURL.substring(0, baseURL.length() - requestedPath.length()) + "/";
		
		String result = "<a href=\"" + baseURL + "\">/</a>";
		String path = "";
		
		for(String part : requestedPath.split("/")) {
			if(part.length() > 0) {
				path += part + "/";
				result += "<a href=\"" + baseURL + path + "\">" + part + "</a>/";
			}
		}
		
		return result;
	}
	
	@Override
	public void proceed(IContext context) {
		
		String requestedPath = ((String[])context.getParameter("path"))[0]; // Raw path
		File file = new File(Context.root.getPath() + requestedPath);       // File for that path
		
		// Detect if exist
		try {
			if(file.exists() && file.isDirectory()) {
				HttpServletResponse response = context._getResponse();
				String requestedURI = FrontController.URIroot + file.getAbsolutePath().substring(Context.root.getAbsolutePath().length()) + "/";
				String requestedURL = context._getRequest().getRequestURL().toString();
				
				System.out.println(requestedURI);
				
				response.setContentType("text/html");
				
				// List the directory
				PrintWriter out = response.getWriter();
				out.print("<h1>Directory " + createHeader(requestedURL, requestedPath) + "</h1>");
				
				out.print("<ul>");
				
				// Directories
				if(!file.equals(Context.root))
					out.print("<li><a href=\"" + requestedURI + "..\">..</a></li>");
				
				for(File f : file.listFiles(new FileFilter() {
					@Override
					public boolean accept(File f) {	return f.isDirectory(); }
				})) {
					out.print("<li><a href=\"" + requestedURI + f.getName() + "\">" + f.getName() + "</a> (directory)</li>");
				}
				
				// Files
				for(File f : file.listFiles(new FileFilter() {
					@Override
					public boolean accept(File f) {	return !f.isDirectory(); }
				})) {
					out.print("<li><a href=\"" + requestedURI + f.getName() + "\">" + f.getName() + "</a> (" + f.length() + " bytes)</li>");
				}
				
				out.print("</ul>");
				out.print("<form method=\"POST\" enctype=\"multipart/form-data\"> <input type=\"submit\" value=\"upload\" /> <input type=\"file\" name=\"file\" /></form>");
				
			} else
				JwfErrorHandler.displayError(context, 404, "The ressource " + requestedPath + " does not exist");
			
		} catch (IOException e) {
			JwfErrorHandler.displayError(context, 500, "Error while walking in the path " + requestedPath + " : " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public int setPriority(int priority) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addCredential(String role) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean needsCredentials() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasCredential(String[] roles) {
		// TODO Auto-generated method stub
		return false;
	}

}
