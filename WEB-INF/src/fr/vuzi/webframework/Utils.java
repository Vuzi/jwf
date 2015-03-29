package fr.vuzi.webframework;


/**
 * Utils class, used to store useful methods
 * 
 * @author Vuzi
 *
 */
public class Utils {
	
	/**
	 * Utils class : private constructor
	 */
	private Utils() {}
	
	/**
	 * Append a String to a String array
	 * 
	 * @param tab The source array
	 * @param toAdd The string to add
	 * @return The new array
	 */
	public static String[] appendToArray(String[] tab, String toAdd) {
		
		if(tab == null)
			return new String[] { toAdd };
		
		String[] newtab = new String[tab.length + 1];
		int i = 0;
		
		for(; i < tab.length; i++)
			newtab[i] = tab[i];
		newtab[i] = toAdd;
		
		return newtab;
	}
	
	/**
	 * Append a String array to a String array
	 * 
	 * @param tab The source array
	 * @param toAdd The string array to add
	 * @return The new array
	 */
	public static String[] appendToArray(String[] tab, String[] toAdd) {

		if(toAdd == null)
			return tab;
		
		if(tab == null)
			return toAdd.clone();
		
		String[] newtab = new String[tab.length + toAdd.length];
		int i = 0;
		
		for(; i < tab.length; i++)
			newtab[i] = tab[i];
		for(int j = 0; j < toAdd.length; j++)
			newtab[i + j] = toAdd[j];
		
		return newtab;
	}
}
