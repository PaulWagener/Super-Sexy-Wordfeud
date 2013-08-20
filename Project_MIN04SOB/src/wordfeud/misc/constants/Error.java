package wordfeud.misc.constants;

public enum Error {

	NOT_ALIGNED("De gelegde letters liggen niet op een lijn."), NOT_CONNECTED(
			"Niet alle letters zijn aaneengesloten."), NOT_ON_START(
			"Er ligt geen letter op de start positie."), NO_LETTERS_PUT(
			"Er zijn geen letters neergelegd."), NOT_ATTACHED(
			"Het gelegde woord zit nergens aan vast"), DENIED(
			"Woord is geweigerd"), ALREADY_PENDING("Woord is al voorgesteld"), PENDING(
			"Woord wordt voorgesteld"), NOTYOURTURN("Het is uw beurt niet"), TOSHORT(
			"Er zijn te weinig letters gelegd");

	private final String message;

	private Error(String e) {
		message = e;
	}

	public String toString() {
		return message;
	}

}
