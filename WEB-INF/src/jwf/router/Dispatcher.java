package jwf.router;

import jwf.error.JwfErrorHandler;
import org.esgi.web.framework.action.interfaces.IAction;
import org.esgi.web.framework.context.interfaces.IContext;
import org.esgi.web.framework.router.interfaces.IDispatcher;

public class Dispatcher implements IDispatcher {
	
	/**
	 * Constructor.
	 */
	public Dispatcher() {}

	@Override
	public void dispatch(IContext context) {
		String className = context.getActionClass();
		
		if(className != null) {
			try {
				IAction action = (IAction) Class.forName(className).newInstance();
				action.proceed(context);
			} catch (InstantiationException e) {
				JwfErrorHandler.displayError(context, 500, "Could not instanciate the class : " + className);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				JwfErrorHandler.displayError(context, 500, "Could not access the class : " + className);
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				JwfErrorHandler.displayError(context, 500, "Could not find the class : " + className);
				e.printStackTrace();
			}
		} else // Error 404
			JwfErrorHandler.displayError(context, 404, "Could not locate the page to load");
	}

}
