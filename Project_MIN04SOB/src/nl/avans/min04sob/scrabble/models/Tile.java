package nl.avans.min04sob.scrabble.models;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import nl.avans.min04sob.scrabble.core.db.Db;
import nl.avans.min04sob.scrabble.core.db.Queries;
import nl.avans.min04sob.scrabble.core.db.Query;

public class Tile implements Transferable {
	private String letter;
	private int value;
	private boolean mutatable;
	private int tileId;
	public final static boolean MUTATABLE = true;
	public final static boolean NOT_MUTATABLE = false;
	private final static DataFlavor flavors[] = { new DataFlavor(Tile.class,
			"Tile") };

	@Deprecated
	public Tile() {
		letter = "";
		value = 0;
		mutatable = true;
		tileId = 0;
	}
	
	@Override
	public int hashCode() {
        return new HashCodeBuilder(17, 31).      
            append(letter).
            append(value).
            append(tileId).
            toHashCode();
    }

	@Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Tile))
            return false;

        Tile guest = (Tile) obj;
        return new EqualsBuilder().
            append(letter, guest.letter).
            append(value, guest.value).
            append(tileId, guest.tileId).
            isEquals();
    }

	public Tile(int gameId, int letterId) {

		Future<ResultSet> worker;
		ResultSet res;
		try {
			worker = Db.run(new Query(Queries.TILE).set(gameId).set(letterId));
			res = worker.get();
			if(res.next()){
				letter = res.getString("karakter");
				value = res.getInt("waarde");
				mutatable = Tile.MUTATABLE;
				tileId = res.getInt("id");
			}
		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	public Tile(String letter, int value, boolean mutatable, int id) {
		this.letter = letter;
		this.value = value;
		this.mutatable = mutatable;
		this.tileId = id;
	}

	public String getLetter() {
		if (!isEmpty()) {
			return letter;
		} else {
			return "";
		}
	}

	public int getValue() {
		return value;
	}

	public boolean isEmpty() {
		return letter.equals("");
	}

	public boolean isMutatable() {
		return mutatable;
	}

	public void lock() {
		mutatable = false;
	}

	public int getTileId() {
		return this.tileId;
	}

	public void setEmpty(boolean empty) {
		if (empty) {
			letter = "";
		}
	}

	public void setLetter(String letter) {
		this.letter = letter;
	}

	@Override
	public String toString() {
		return "<html>" + letter + "<sup>" + value + "</sup></html>";
	}

	@Override
	public Object getTransferData(DataFlavor flavor) {
		if (isDataFlavorSupported(flavor)) {
			return this;
		}
		return null;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor value) {
		for (DataFlavor flavor : flavors) {
			if (flavor == value || value != null && value.equals(flavor)) {
				return true;
			}
		}
		return false;
	}

}
