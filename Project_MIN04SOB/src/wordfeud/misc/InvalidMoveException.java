package wordfeud.misc;

import wordfeud.misc.constants.Error;

@SuppressWarnings("serial")
public class InvalidMoveException extends Exception {

	private Error error;

	public InvalidMoveException(Error e) {
		error = e;
	}

	public String getMessage() {
		return error.toString();
	}
}
