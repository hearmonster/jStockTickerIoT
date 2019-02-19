package commons.init;

import commons.model.Authentication;

public class AuthenticationX extends Authentication {

	private String p12;

	public AuthenticationX() {
		// No real constructor, actually
		super();
	}

	public String getP12() {
		return p12;
	}


}
