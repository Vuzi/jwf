package jwf.router;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jwf.context.Context;

import org.esgi.web.framework.context.interfaces.IContext;
import org.esgi.web.framework.router.interfaces.IRewriteRule;

public class RewriteRule implements IRewriteRule {
	
	private Pattern regex;
	private String className;
	private String method;
	private String[] substitutions;
	private Matcher m;

	public RewriteRule(String regex, String method, String className, String[] substitutions) {
		init(regex, method, className, substitutions);
	}

	public RewriteRule(String regex, String className, String[] substitutions) {
		init(regex, "*", className, substitutions);
	}

	public RewriteRule(String regex, String className) {
		init(regex, "*", className, new String[0]);
	}

	public RewriteRule(String regex, String method, String className) {
		init(regex, method, className, new String[0]);
	}
	
	private void init(String regex, String method, String className, String[] substitutions) {
		this.regex =  Pattern.compile(regex);
		this.method = method;
		this.className = className;
		this.substitutions = substitutions;
	}
	
	protected boolean checkContext(IContext context) {
		return true;
	}

	protected boolean checkMethod(IContext context) {
		if(method == null || method.equals("*"))
			return true;
		
		String[] methods = method.split("\\|");
		
		for(String m : methods)
			if(m.equals(context._getRequest().getMethod()))
				return true;
		
		return false;
	}
	
	@Override
	public boolean matches(IContext context) {
		m = regex.matcher(context._getRequest().getRequestURI());
		
		try {
			if(m.find() && checkMethod(context)) {
				for(int i = 0; i < substitutions.length && i < m.groupCount(); i++) {
					if(m.group(i + 1) != null)
						((Context)context).setParamater(substitutions[i], new String[] { URLDecoder.decode(m.group(i + 1), "UTF-8").toString() });
				}
				return checkContext(context);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public void rewrite(IContext context) {
		context.setActionClass(className);
	}

}
