package nl.avans.min04sob.scrabble.misc;

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
