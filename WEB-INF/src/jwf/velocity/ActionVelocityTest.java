package jwf.velocity;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jwf.action.AActionCredential;
import jwf.error.JwfErrorHandler;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.esgi.web.framework.context.interfaces.IContext;

public class ActionVelocityTest extends AActionCredential {

	/*
	public static String[] getCredentials() {
		return new String[] { "user" } ;
	}*/

	@Override
	public void proceed(IContext context) {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, context._getRequest().getRealPath("/")+"/WEB-INF/templates");
        ve.init();
		
		VelocityContext vcontext = new VelocityContext();
		List<String> cities = new ArrayList<String>();
		cities.add("paris");
		cities.add("londres");
		cities.add("chicago");
		cities.add("tokyo");

		vcontext.put("name", "Velocityyy");
		vcontext.put("cities", cities);
		
		Template t = null;

		try {
			t = ve.getTemplate("test.vm");
						
			StringWriter sw = new StringWriter();
			t.merge(vcontext, sw);
			
			context._getResponse().getWriter().write(sw.toString());
		} catch(Exception e) {
			JwfErrorHandler.displayError(context, 500, "error while writting response : " + e.getMessage());
			e.printStackTrace();
		}
	}


}
