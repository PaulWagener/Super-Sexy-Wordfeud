package nl.avans.min04sob.scrabble.misc;

import org.apache.commons.lang3.ArrayUtils;

@SuppressWarnings("serial")
public class InvalidMoveException extends Exception {
	public static final int NOT_ALIGNED = 1;
	public static final int NOT_CONNECTED = 2;
	public static final int NOT_ON_START = 3;
	public static final int NO_LETTERS_PUT = 4;
	public static final int TO_SHORT = 5;
	public static final int STATE_DENIED = 6;
	public static final int STATE_PENDING = 7;
	public static final int STATE_SETPENDING = 8;
	private int errorType;

	public InvalidMoveException(int error) {
		if (ArrayUtils.contains(new int[] { NOT_ALIGNED, NOT_CONNECTED,
				NOT_ON_START, NO_LETTERS_PUT, TO_SHORT, STATE_DENIED, STATE_PENDING, STATE_SETPENDING}, error)) {
			errorType = error;
			return;
		}
		throw new IllegalArgumentException(
				"Not a valid error value for InvalidMoveException");
	}

	public String getMessage() {
		switch (errorType) {
		case NOT_ALIGNED:
			return "De gelegde letters liggen niet op een lijn.";
		case NOT_CONNECTED:
			return "Niet alle letters zijn aaneengesloten.";
		case NOT_ON_START:
			return "Er ligt geen letter op de start positie.";
		case TO_SHORT :
			return "Het gelegde woord is te kort";
		case STATE_DENIED :
			return "Woord is geweigerd";
		case STATE_PENDING :
			return "Woord is al voorgesteld";
		case STATE_SETPENDING :
			return "Woord wordt voorgesteld";
		case NO_LETTERS_PUT:
		default:
			return "Er zijn geen letters neergelegd.";
		}
	}
}
