package fr.vuzi.webframework.action;

import fr.vuzi.webframework.context.IContext;

/**
 * Abstract implementation handling call method and context setter/getter. Usually actions will
 * extends this class rather than implementing the IAction interface
 * 
 * @author Vuzi
 *
 */
public abstract class AAction implements IAction {
	
	private IContext context;

	@Override
	public boolean needsCredentials() {
		return getCredentials().length > 0;
	}

	@Override
	public boolean hasCredentials(String[] roles) {
		String[] credentials = getCredentials();

		// Every element in roles must be in credentials
		for(String credential : credentials) {
			boolean found = false;
			
			for(String role : roles) {
				if(role.equals(credential)) {
					found = true;
					break;
				}
			}
			
			if(!found) // Not found : not the right
				return false;
		}
		
		return true;
	}
	
	@Override
	public Void call() throws Exception {
		proceed();  // Only call proceed here
		return null;
	}

	@Override
	public void setActionContext(IContext context) {
		this.context = context;
	}

	@Override
	public IContext getActionContext() {
		return context;
	}

	@Override
	public boolean needRenderer() {
		return true;
	}
}
