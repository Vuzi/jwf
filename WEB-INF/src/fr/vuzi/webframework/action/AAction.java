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
}
