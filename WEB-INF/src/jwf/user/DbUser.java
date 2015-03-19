package jwf.user;

import java.util.ArrayList;
import java.util.List;

public class DbUser {

	public List<User> users = new ArrayList<User>();
	public static DbUser self;

	private DbUser() {
		users.add(new User(1, "admin", "admin", new String[] { "admin", "user" } ));
		users.add(new User(2, "Vuzi", "1234", new String[] { "user" }));
		users.add(new User(3, "Reda", "1234", new String[] { "user" }));
		users.add(new User(4, "MrThomas", "1234", new String[] { "user" }));
	}
	
	public static DbUser getInstance() {
		if(self == null)
			self = new DbUser();
		
		return self;
	}

	public User getUserByLoginAndPassword(String login, String password) {
		for(User user : users) {
			if(user.login.equals(login) && user.password.equals(password))
				return user;
		}
		
		return null;
	}

	public User getUserById(int ID) {
		for(User user : users) {
			if(user.ID == ID)
				return user;
		}
		
		return null;
	}
	
	public User[] getAllUsers() {
		return users.toArray(new User[0]);
	}

	public User createUser(String login, String pass, String[] creds) {
		User u = new User(users.get(users.size() - 1).ID + 1, login, pass, creds);
		users.add(u);
		return u;
	}

	public void deleteUser(User user) {
		users.remove(user);
	}

	public User[] getUsersByLogin(String login) {
		ArrayList<User> search = new ArrayList<User>();
		login = login.toLowerCase();
		
		for(User user : users) {
			if(user.login.toLowerCase().contains(login))
				search.add(user);
		}
		
		return search.toArray(new User[0]);
	}
	
}
