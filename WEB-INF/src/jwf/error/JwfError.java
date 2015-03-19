package jwf.error;

public class JwfError {

	public int status;
	public String message;
	
	public JwfError(int status, String message) {
		super();
		this.status = status;
		this.message = message;
	}
	
}
