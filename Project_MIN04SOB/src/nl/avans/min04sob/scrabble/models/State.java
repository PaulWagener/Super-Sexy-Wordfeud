package nl.avans.min04sob.scrabble.models;

public enum State {
	FINISH ("finished"),
	PLAY ("playing"),
	REQUEST ("request"),
	RESIGN ("resigned");
	
	private final String type;       

    private State(String s) {
        type = s;
    }
    
    public String toString(){
       return type;
    }
}
