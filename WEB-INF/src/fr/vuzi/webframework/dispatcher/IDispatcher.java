package fr.vuzi.webframework.dispatcher;

import java.util.Collection;

import fr.vuzi.webframework.Lockable;
import fr.vuzi.webframework.action.IAction;
import fr.vuzi.webframework.context.IContext;

/**
 * Action dispatcher. This class will dispatch action asynchronously, and then return once
 * every action performed
 * 
 * Note that the methods dispatch must be thread safe, because the same instance
 * of the dispatcher will be used with multiple client connections.
 * 
 * @author Vuzi
 *
 */
public interface IDispatcher extends Lockable {
	
	/**
	 * Called at the initialization of the dispatcher, use to instantiate a template action
	 * for each possible action
	 * @param actionClassnames The collection of template class names
	 */
	public void setPossibleActions(Collection<String> actionClassnames);
	
	/**
	 * Dispatch asynchronously a single action. This method must be <b>thread safe</b>
	 * @param context The action's context
	 * @param action The action
	 */
	public void dispatch(IContext context, IAction action) throws Exception;
	
	/**
	 * Dispatch asynchronously multiple actions. Those actions must be independent between themselves.
	 * This method must be <b>thread safe</b>
	 * @param context The action's context
	 * @param actions The action
	 */
	public void dispatch(IContext context, Collection<IAction> actions) throws Exception;
	
	/**
	 * Instantiate the action using the given classname
	 * @param actionClassname The class to instantiate
	 * @return The newly instantiated class
	 */
	public IAction instantiate(String actionClassname, IContext context);
	
	/**
	 * Instantiate the collection of actions using the given class names
	 * @param actionClassnames The collections of class names
	 * @return A collection of newly instantiated actions
	 */
	public Collection<IAction> instantiate(Collection<String> actionClassnames, IContext context);
	
}
