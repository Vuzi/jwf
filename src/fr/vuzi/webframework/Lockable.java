package fr.vuzi.webframework;

/**
 * Lockable object. Lockable object are used with the servlet, and shall be locked at the
 * end of the initialization phase. Usually in the lock method, all collections and map will be set
 * immutable to assure thread safety
 * 
 * @author Vuzi
 *
 */
public interface Lockable {

	public void lock();
	
}
