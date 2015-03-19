package jwf.renderer;

import jwf.context.Context;

import org.esgi.web.framework.context.interfaces.IContext;
import org.esgi.web.framework.renderer.interfaces.IRenderer;

public class Renderer implements IRenderer {

	@Override
	public String render(IContext context) {
		switch(((Context)context).getRendererType()) {
		case JSON:
			return new RendererJSON().render(context);
		case XML:
			return new RendererXML().render(context);
		case HTML:
		default:
			return new RendererHTML().render(context);
		}
	}
	
}
