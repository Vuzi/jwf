package jwf.user;

public class User {
	
	public int ID;
	
	public String login;
	public String password;
	
	public String[] credentials;
	
	public User(int id, String login, String password, String[] credentials) {
		super();
		this.ID = id;
		this.login = login;
		this.password = password;
		this.credentials = credentials;
	}
	
}
