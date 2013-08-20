package wordfeud.misc.constants;

public enum Role {
	OBSERVER("observer"), PLAYER("player"), ADMINISTRATOR("administrator"), MODERATOR(
			"moderator");

	private String role;

	Role(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return role;
	}
}
