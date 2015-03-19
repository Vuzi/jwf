package jwf.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import jwf.context.Context;

import org.esgi.web.framework.action.interfaces.IAction;
import org.esgi.web.framework.context.interfaces.IContext;

public class ActionDownloadFile implements IAction {

	@Override
	public void proceed(IContext context) {
		HttpServletResponse response = context._getResponse();
	
		String requestedPath = ((String[])context.getParameter("path"))[0]; // Raw path
		File file = new File(Context.root.getPath() + requestedPath);       // File for that path
		
		// Detect if exist
		try {
			if(file.exists() && file.isFile()) {
				// Send the file
				OutputStream out = response.getOutputStream();
				FileInputStream in = new FileInputStream(file);
				byte[] buffer = new byte[4096];
				int length;
				while ((length = in.read(buffer)) > 0){
				    out.write(buffer, 0, length);
				}
				in.close();
				out.flush();
			} else {
				//JwfErrorHandler.displayError(response, 500, "Could not download file " + file.getName());
			}
		} catch (IOException e) {
			//JwfErrorHandler.displayError(response, 500, "Could not download file " + file.getName() + " : " + e.getMessage());
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
