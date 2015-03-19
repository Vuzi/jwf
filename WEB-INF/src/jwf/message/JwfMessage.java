package jwf.message;

import jwf.error.JwfError;


public class JwfMessage {

	public boolean error = false;
	public JwfError error_detail = null;
	public Object data = null;

	public JwfMessage(Object data) {
		super();
		this.data = data;
	}

	public JwfMessage(boolean error, JwfError error_detail, Object data) {
		super();
		this.error = error;
		this.error_detail = error_detail;
		this.data = data;
	}
	
}
