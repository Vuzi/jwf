package fr.vuzi.webframework.dispatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.vuzi.webframework.action.IAction;
import fr.vuzi.webframework.context.IContext;

/**
 * Dispatcher implementation using ExecutorService to create a pool thread
 * 
 * @author Vuzi
 *
 */
public class Dispatcher implements IDispatcher {
	
	private Map<String, IAction> actionTemplates;

	/**
	 * Thread pool
	 */
	private ExecutorService threadPool;
	
	/**
	 * Thread pool constructor
	 */
	public Dispatcher() {
		threadPool = Executors.newCachedThreadPool();
		actionTemplates = new HashMap<String, IAction>();
	}

	@Override
	public void setPossibleActions(Collection<String> actionClassnames) {
		if(actionClassnames == null)
			return;
		
		for(String actionClassname : actionClassnames) { // For each rule
			if(actionClassname != null && !actionTemplates.containsKey(actionClassname)) { // If the action class is not already in the templates
				try {
					// Add to the template list
					actionTemplates.put(actionClassname, (IAction) Class.forName(actionClassname).newInstance());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void dispatch(IContext context, IAction action) throws Exception {
		action.proceed();
		//threadPool.submit(action); // Submit the only action
		// TODO handle exceptions
	}
	
	@Override
	public void dispatch(IContext context, Collection<IAction> actions) throws Exception {
		try {
			threadPool.invokeAll(actions); // Launch all the actions
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// TODO handle exceptions
	}

	@Override
	public void lock() {
		actionTemplates = Collections.unmodifiableMap(actionTemplates);
	}

	@Override
	public IAction instantiate(String actionClassname, IContext context) {
		IAction actionTemplate = actionTemplates.get(actionClassname);
		
		if(actionTemplate == null)
			return null;
		else {
			return actionTemplate.cloneAction(context);
		}
	}

	@Override
	public Collection<IAction> instantiate(Collection<String> actionClassnames, IContext context) {
		ArrayList<IAction> actions = new ArrayList<IAction>();
		
		for(String actionClassname : actionClassnames) {
			IAction action = instantiate(actionClassname, context);
			
			if(action != null)
				actions.add(action);
		}
		
		return actions;
	}
}
