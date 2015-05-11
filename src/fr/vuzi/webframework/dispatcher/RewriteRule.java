package fr.vuzi.webframework.dispatcher;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.vuzi.webframework.context.IContext;

/**
 * Default rewrite rule implementation. Additional checks
 * can be done by overriding the checkContext method
 * 
 * @author Vuzi
 *
 */
public class RewriteRule implements IRewriteRule {

	/**
	 * The URI regex
	 */
	private Pattern regex;
	
	/**
	 * The action classname
	 */
	private String className;
	
	/**
	 * The access method possible, separated by "|", i.e. "GET|POST"
	 */
	private String method;
	
	/**
	 * Substitutions keys, used with regex capturing groups
	 */
	private String[] substitutions;

	/**
	 * Default render type of the rule, usually to force velocity rendering
	 */
	private String rendererType;

	/**
	 * Rule constructor
	 * @param regex URI regex
	 * @param className Action class name
	 */
	public RewriteRule(String regex, String className) {
		init(regex, "*", className, new String[0], null);
	}

	/**
	 * Rule constructor
	 * @param regex URI regex
	 * @param className Action class name
	 * @param substitutions Substitutions
	 */
	public RewriteRule(String regex, String className, String[] substitutions) {
		init(regex, "*", className, substitutions, null);
	}
	
	/**
	 * Rule constructor
	 * @param regex URI regex
	 * @param method Access method(s)
	 * @param className Action class name
	 */
	public RewriteRule(String regex, String method, String className) {
		init(regex, method, className, new String[0], null);
	}

	/**
	 * Rule constructor
	 * @param regex URI regex
	 * @param method Access method(s)
	 * @param className Action class name
	 * @param substitutions Substitutions
	 */
	public RewriteRule(String regex, String method, String className, String[] substitutions) {
		init(regex, method, className, substitutions, null);
	}

	/**
	 * Rule constructor
	 * @param regex URI regex
	 * @param method Access method(s)
	 * @param className Action class name
	 * @param rendererType Renderer type override
	 */
	public RewriteRule(String regex, String method, String className, String rendererType) {
		init(regex, method, className, new String[0], rendererType);
	}


	/**
	 * Rule constructor
	 * @param regex URI regex
	 * @param method Access method(s)
	 * @param className Action class name
	 * @param substitutions Substitutions
	 * @param rendererType Renderer type override
	 */
	public RewriteRule(String regex, String method, String className, String[] substitutions, String rendererType) {
		init(regex, method, className, substitutions, rendererType);
	}

	/**
	 * Initialization method
	 * @param regex URI regex
	 * @param method Access method(s)
	 * @param className Action class name
	 * @param substitutions Substitutions
	 * @param rendererType Renderer type override
	 */
	private void init(String regex, String method, String className, String[] substitutions, String rendererType) {
		this.regex =  Pattern.compile(regex);
		this.method = method;
		this.className = className;
		this.substitutions = substitutions;
		this.rendererType = rendererType;
	}
	
	@Override
	public boolean matches(IContext context) {
		Matcher m = regex.matcher(context.getRequest().getRequestURI()); // Only perform regex test on the URI, not the URL
		
		try {
			if(m.find() && checkMethod(context)) { // Check URI and access method
				for(int i = 0; i < substitutions.length && i < m.groupCount(); i++) { // Substitutions
					if(m.group(i + 1) != null)
						context.setParamater(substitutions[i], new String[] { URLDecoder.decode(m.group(i + 1), "UTF-8").toString() });
				}
				return checkContext(context); // Check context last
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public void rewrite(IContext context) {
		context.setActionClassname(className); // Action classname
		if(rendererType != null)
			context.setRenderType(rendererType); // Force renderer
	}

	/**
	 * Overridable method, used to do additional verification
	 * for the context for match test
	 * @param context The request context
	 * @return True if the check is successful, false otherwise
	 */
	protected boolean checkContext(IContext context) {
		return true; // Default : no verifications
	}

	/**
	 * Overridable method, used to check the method used to
	 * access the page (post, get, put, etc...) and perform a check
	 * on it
	 * @param context The request context
	 * @return True if the check is successful, false otherwise
	 */
	protected boolean checkMethod(IContext context) {
		if(method == null || method.equals("*"))
			return true;
		
		String[] methods = method.split("\\|");
		
		for(String m : methods)
			if(m.equals(context.getRequest().getMethod()))
				return true;
		
		return false;
	}

	@Override
	public String getActionClassname() {
		return className;
	}
	
	
}
