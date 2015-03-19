package jwf.action;

import org.esgi.web.framework.action.interfaces.IAction;
import org.esgi.web.framework.context.interfaces.IContext;

public abstract class AActionCredential implements IAction {

	@Override
	public int setPriority(int priority) {
		return 0;
	}

	@Override
	public int getPriority() {
		return 0;
	}
	
	public static String[] getCredentials() {
		return new String[0];
	}

	public static boolean hasCredential(String[] credentials, String[] roles) {
		
		// No credentials for the user
		if(roles == null) {
			if(credentials == null || credentials.length <= 0)
				return true;
			else
				return false;
		}
		
		boolean found = false;
		
		// Need to check the credentials
		for(String credential : credentials)  {
			for(String role : roles) {
				if(role.equals(credential)) {
					found = true;
					break;
				}
			}
			if(!found)
				return false;
			found = false;
		}
		
		return true;
	}

	@Override
	public abstract void proceed(IContext context);
	


	@Override
	public void addCredential(String role) {
		// Not used
	}

	@Override
	public boolean needsCredentials() {
		// Not used
		return false;
	}

	@Override
	public boolean hasCredential(String[] roles) {
		// Not used
		return false;
	}

}
