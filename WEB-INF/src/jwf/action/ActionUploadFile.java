package jwf.action;

import java.io.File;
import java.io.IOException;

import jwf.context.Context;
import jwf.error.JwfErrorHandler;

import org.apache.commons.io.FileUtils;
import org.esgi.web.framework.action.interfaces.IAction;
import org.esgi.web.framework.context.interfaces.IContext;

public class ActionUploadFile implements IAction {

	@Override
	public void proceed(IContext context) {
		String requestedPath = ((String[])context.getParameter("path"))[0]; // Raw path
		File file = new File(Context.root.getPath() + requestedPath);       // File for that path
		
		if(file.exists() && file.isDirectory()) {
			for(File f : context.getUploadedFiles()) {
				try {
					FileUtils.moveFile(f, new File(file + "/" + f.getName()));
				} catch (IOException e) {
					e.printStackTrace();
					JwfErrorHandler.displayError(context, 500, "Could not upload file " + f.getName());
					return;
				}
			}
		}
		
		new ActionDisplayFolder().proceed(context);
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
