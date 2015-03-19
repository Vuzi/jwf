package jwf.router;

import java.util.ArrayList;
import java.util.List;

import org.esgi.web.framework.context.interfaces.IContext;
import org.esgi.web.framework.router.interfaces.IRewriteRule;
import org.esgi.web.framework.router.interfaces.IRewriter;

public class Rewriter implements IRewriter {
	
	private List<IRewriteRule> rules;
	
	/**
	 * Constructor.
	 */
	public Rewriter() {
		init();
	}
	
	public void init() {
		rules = new ArrayList<IRewriteRule>();
	}

	@Override
	public void addRule(IRewriteRule rule) {
		rules.add(rule);
	}

	@Override
	public void rewrite(IContext context) {
		for(IRewriteRule rule : rules) {
			if(rule.matches(context)) {
				rule.rewrite(context);
				return; // First to match
			}
		}
	}

}
