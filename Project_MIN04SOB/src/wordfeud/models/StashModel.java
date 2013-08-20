package wordfeud.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import wordfeud.core.database.Db;
import wordfeud.core.database.Queries;
import wordfeud.core.database.Query;
import wordfeud.core.mvc.CoreModel;


public class StashModel extends CoreModel {
	private final String letterfrompot = "SELECT L.Spel_ID AS Spel_ID, L.ID AS Letter_ID, L.LetterType_karakter AS Karakter FROM Letter L WHERE L.ID NOT IN (  SELECT Letter_ID  FROM gelegdeletter GL WHERE GL.Spel_ID = L.Spel_ID )AND L.ID NOT IN( SELECT Letter_ID   FROM letterbakjeletter LB WHERE LB.Spel_ID = L.Spel_ID AND LB.Beurt_ID IN  (SELECT MAX(Beurt_ID)  FROM `letterbakjeletter` LX JOIN `beurt` BX ON LX.`Beurt_ID` = BX.`ID` WHERE LX.`Spel_ID` = L.Spel_ID GROUP BY BX.`Account_naam`  ))and L.Spel_ID = ? ORDER BY L.Spel_ID, L.ID ";

	private GameModel game;
	private AccountModel owner;
	public final static int STASH_SIZE = 7;

	/**
	 * Fill the playerTiles with the tiles for the player from his last turn
	 * 
	 * @param user
	 *            AccountModel for the user
	 * @param game
	 *            The GameModel for which the tiles should be fetched
	 */
	public StashModel(AccountModel owner, GameModel game) {
		this.owner = owner;
		this.game = game;
	}

	public String[] getAllAvailableLetters() {
		String[] letters = null;
		String q = "SELECT `karakter` FROM `pot`";
		try {
			Future<ResultSet> worker = Db.run(new Query(q));
			ResultSet res = worker.get();
			int numRows = Query.getNumRows(res);

			letters = new String[numRows];
			int i = 0;
			while (res.next()) {
				letters[i] = res.getString(1);
				i++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return letters;
	}

	@Deprecated
	public Tile[] getPlayerTiles(AccountModel user, GameModel game,
			int currentTurnId) {

		int gameId = game.getGameId();
		int numRows;
		try {

			Future<ResultSet> worker;

			boolean foundletters = false;
			ResultSet res = null;
			while (!foundletters) {

				worker = Db
						.run(new Query(Queries.CURRENT_TILES)
								.set(user.getUsername()).set(gameId)
								.set(currentTurnId));
				res = worker.get();
				if (res.next() || currentTurnId == 0) {
					foundletters = true;
				} else {
					currentTurnId--;
				}

			}
			System.out.println("Getting letters for: " + user.getUsername()
					+ " for game: " + gameId + " with turnId:" + currentTurnId);

			numRows = Query.getNumRows(res);

			Tile[] tiles = new Tile[numRows];
			int i = 0;
			while (res.next()) {

				String getTileValue = "Select waarde FROM lettertype WHERE karakter = ? AND LetterSet_code = ?";
				Future<ResultSet> worker1 = Db.run(new Query(getTileValue).set(
						res.getString(5)).set("NL"));
				ResultSet tilewaarde = worker1.get();
				tilewaarde.next();
				tiles[i] = new Tile(res.getString(5), tilewaarde.getInt(1),
						Tile.MUTATABLE, res.getInt(4));
				i++;
			}

			return tiles;
		} catch (SQLException | InterruptedException | ExecutionException sql) {
			sql.printStackTrace();
		}
		return null;
	}

	private Tile getRandomTile() {
		int gameId = game.getGameId();
		String q = "SELECT * FROM `pot` WHERE `spel_id` = ? ORDER BY RAND() LIMIT 1";
		try {
			Future<ResultSet> worker = Db.run(new Query(q).set(gameId));
			ResultSet res = worker.get();
			if (res.next()) {
				int letterId = res.getInt("letter_id");
				Tile returnTile = new Tile(gameId, letterId);
				return returnTile;
			}
			int numRows = Query.getNumRows(res);
			System.out.println(numRows);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void addTile(Tile t) {
		int turnId = game.getLastTurn(owner);
		String q = "INSERT INTO `letterbakjeletter` (`Spel_ID` ,`Letter_ID` ,`Beurt_ID`) VALUES (?, ?, ?)";
		try {
			Db.run(new Query(q).set(game.getGameId()).set(t.getTileId())
					.set(turnId));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addRandomTiles() {
		while (getStashSize() < STASH_SIZE) {
			Tile randomTile = getRandomTile();
			if (randomTile != null) {
				addTile(randomTile);
			} else {
				break;
			}
		}
	}

	private int getStashSize() {
		int turnId = game.getLastTurn(owner);
		
		String q = "SELECT COUNT(*) FROM `letterbakjeletter` WHERE `Spel_ID` = ? AND `Beurt_ID` = ?";
		try {
			Future<ResultSet> worker = Db.run(new Query(q)
					.set(game.getGameId()).set(turnId));

			ResultSet res = worker.get();
			if (res.next()) {
				int size = res.getInt(1);
				return size;
			}
		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		//On error, return the max size
		return STASH_SIZE;
	}

	/**
	 * 
	 * @return Tile array with player tiles from last turn
	 */
	public Tile[] getPlayerTiles() {
		int turnId = game.getLastTurn(owner);
		return getPlayerTiles(turnId);
	}

	/**
	 * Get playerTiles from the turn with turnId
	 * 
	 * @param turnId
	 * @return Tile array with player tiles for specified turn
	 */
	public Tile[] getPlayerTiles(int turnId) {
		Tile[] playerTiles = new Tile[STASH_SIZE];

		try {

			Future<ResultSet> worker = Db
					.run(new Query(Queries.TURN_TILES).set(game.getGameId())
							.set(owner.getUsername()).set(turnId));
			ResultSet res = worker.get();

			// Fill the tile array
			int index = 0;
			while (res.next()) {
				playerTiles[index] = new Tile(game.getGameId(),
						res.getInt("id"));

				index++;
			}
		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		return playerTiles;
	}

	public boolean letterleft(int game_id) {

		try {

			Future<ResultSet> worker = Db.run(new Query(letterfrompot)
					.set(game_id));

			ResultSet res = worker.get();

			int numRows = Query.getNumRows(res);
			System.out.println(numRows);
			if (numRows == 0) {

				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void update() {
	}
}
