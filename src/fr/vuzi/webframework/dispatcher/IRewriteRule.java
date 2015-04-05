package fr.vuzi.webframework.dispatcher;

import fr.vuzi.webframework.context.IContext;

public interface IRewriteRule {

	/**
	 * Method called by the dispatcher to check if the rewrite rule apply
	 * for the given request context. If it apply, then the rewrite method
	 * is called
	 * @param context The request context
	 * @return True if the rule apply, false otherwise
	 */
	public boolean matches(IContext context);
	
	/**
	 * Additional actions, called after the successful rule match
	 * @param context The request context
	 */
	public void rewrite(IContext context);
	
	/**
	 * Return the action classname of the rule
	 * @return The action classname
	 */
	public String getActionClassname();
	
}
