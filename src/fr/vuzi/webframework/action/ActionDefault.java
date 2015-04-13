package fr.vuzi.webframework.action;

import fr.vuzi.webframework.context.IContext;

public class ActionDefault extends AActionNoCredentials {

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public void proceed() {
		System.out.println("Default action proceeded");
		System.out.println("Payload : " + getActionContext().getParameterUnique("payload"));
	}

	@Override
	public IAction cloneAction(IContext context) {
		ActionDefault ad = new ActionDefault();
		ad.setActionContext(context);
		return ad;
	}

}
