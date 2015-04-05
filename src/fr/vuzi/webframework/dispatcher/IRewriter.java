package fr.vuzi.webframework.dispatcher;

import java.util.Collection;

import fr.vuzi.webframework.Lockable;
import fr.vuzi.webframework.action.IAction;
import fr.vuzi.webframework.context.IContext;

/**
 * Rewriter class. This class contains all the rules, and act as a filter:
 * every request is tested, and the first rule to match define the action
 * to proceed for the request. If no action is selected, then its a 404 error
 * 
 * Note that the <b>rewrite</b> method should be thread safe, unlike the other
 * methods as they will only be called in the thread-safe servlet initialization phase
 * 
 * @author Vuzi
 *
 */
public interface IRewriter extends Lockable {

	/**
	 * Add a rule to the rewriter
	 * @param rule The rule to add
	 */
	public void addRule(IRewriteRule rule);
	
	/**
	 * Add a collection of rules to the rewriter
	 * @param rules Collection of rules to add
	 */
	public void addRules(Collection<IRewriteRule> rules);
	
	/**
	 * Remove all the rules.
	 */
	public void clearRules();
	
	/**
	 * Return all the contained rules
	 * @return A collection of all the contained rules
	 */
	public Collection<IRewriteRule> getRules();
	
	/**
	 * Rewrite for the given request context. All the rules are tested, and the first to match is
	 * used to retrieve the action class name to proceed in the dispatcher
	 * 
	 * This method should be <b>thread safe</b> because only on instance of this rewriter will be shared
	 * between all the client connections
	 * @param context The request context
	 * @return The action template class to be proceed. Note that this class should be cloned before usage,
	 *         because the returned instance is only a template value
	 */
	public IAction rewrite(IContext context);
	
}
