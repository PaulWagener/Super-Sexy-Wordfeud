package wordfeud.misc.constants;

public enum Turn {
	BEGIN("begin"), END("end"), PASS("pass"), RESIGN("resign"), SWAP("swap"), WORD(
			"word");

	private final String type;

	private Turn(String s) {
		type = s;
	}

	public String toString() {
		return type;
	}
}
