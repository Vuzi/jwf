package fr.vuzi.webframework.action;

/**
 * Similar action implementation, extending AAction, but with no credentials. Usually
 * this class will be extended by actions not needing credentials
 * 
 * @author Vuzi
 *
 */
public abstract class AActionNoCredentials extends AAction {

	@Override
	public String[] getCredentials() {
		return null;
	}

	@Override
	public boolean needsCredentials() {
		return false;
	}

	@Override
	public boolean hasCredentials(String[] roles) {
		return true;
	}

}
