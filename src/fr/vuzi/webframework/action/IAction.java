package fr.vuzi.webframework.action;

import java.util.concurrent.Callable;

import fr.vuzi.webframework.context.IContext;

/**
 * An Action is usually linked to an end-point of the site. If the credentials can be validated,
 * then the action is put in the proceed queue according to its priority
 * 
 * Every action <b>must</b> have an empty public constructor, because this constructor may be use
 * to instantiate new actions

 * @author Vuzi
 *
 */
public interface IAction extends Callable<Void>, Cloneable {
	
	/**
	 * Return the priority value of the action. 0 is the default priority
	 * @return The priority of the action
	 */
	public int getPriority();
	
	/**
	 * Return the credentials needed to execute this actions. Credentials
	 * are simply strings.
	 * @return The credentials of the action.
	 */
	public String[] getCredentials();
	
	/**
	 * Return true if the action needs credentials to be executed. If no
	 * credentials are needed, then anybody can proceed this action
	 * @return True if credentials are needed, false otherwise
	 */
	public boolean needsCredentials();
	
	/**
	 * Compare the given credentials with those needed for the action,
	 * and return true if all the needed one are contained in the given
	 * credentials. Note that all the credentials must be used
	 * @param roles The string array of the user credentials
	 * @return True if the use can proceed the action, false otherwise
	 */
	public boolean hasCredentials(String[] roles);
	
	/**
	 * The action to proceed. This method should be implemented in every different
	 * action because this is the action in itself
	 */
	public void proceed() throws Exception;
	
	/**
	 * Set the action context
	 * @param context The action context
	 */
	public void setActionContext(IContext context);
	
	/**
	 * Get the action context
	 * @return The action context
	 */
	public IContext getActionContext();
	
	/**
	 * Clone method, used to clone the action from its template instance
	 * @param context The action new context
	 * @return A new action instance
	 */
	public IAction cloneAction(IContext context);
}
