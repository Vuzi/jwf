package fr.vuzi.webframework.dispatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.vuzi.webframework.action.IAction;
import fr.vuzi.webframework.context.IContext;

/**
 * Default implement of the rewriter, using a list
 * 
 * @author Vuzi
 *
 */
public class Rewriter implements IRewriter {

	/**
	 * List of rules
	 */
	private List<IRewriteRule> rules;

	/**
	 * Templates of all the actions loaded in the rewriter
	 */
	private Map<String, IAction> actionTemplates;
	
	/**
	 * Rewriter constructor
	 */
	public Rewriter() {
		init();
	}
	
	/**
	 * Initialization method
	 */
	public void init() {
		rules = new ArrayList<IRewriteRule>();
		actionTemplates = new HashMap<String, IAction>();
	}
	
	@Override
	public void addRule(IRewriteRule rule) {
		rules.add(rule);
		updateActionTemplates();
	}

	@Override
	public void addRules(Collection<IRewriteRule> rules) {
		rules.addAll(rules);
		updateActionTemplates();
	}

	@Override
	public void clearRules() {
		rules.clear();
		actionTemplates.clear();
	}
	
	@Override
	public void lock() {
		actionTemplates = (Map<String, IAction>) Collections.unmodifiableMap(actionTemplates);
		rules = (List<IRewriteRule>) Collections.unmodifiableList(rules);
	}
	
	/**
	 * Update the action template map, used to quickly retrieve classes instances by classname,
	 * because Java's reflexivity is usually slower than a clone
	 */
	private void updateActionTemplates() {
		actionTemplates.clear();
		
		for(IRewriteRule rule : rules) { // For each rule
			String actionClassname = rule.getActionClassname();
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
	public Collection<IRewriteRule> getRules() {
		return rules;
	}

	@Override
	public IAction rewrite(IContext context) {
		for(IRewriteRule rule : rules) {
			if(rule.matches(context)) {
				rule.rewrite(context);
				context.setActionClassname(rule.getActionClassname()); // Save classname
				return actionTemplates.get(rule.getActionClassname()); // Return action
			}
		}
		
		return null;
	}

}
