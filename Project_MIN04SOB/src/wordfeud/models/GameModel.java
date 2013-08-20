package wordfeud.models;

import java.awt.Point;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import wordfeud.core.Event;
import wordfeud.core.database.Db;
import wordfeud.core.database.Queries;
import wordfeud.core.database.Query;
import wordfeud.core.mvc.CoreModel;
import wordfeud.misc.InvalidMoveException;
import wordfeud.misc.MatrixUtils;
import wordfeud.misc.constants.Error;
import wordfeud.misc.constants.State;
import wordfeud.misc.constants.Turn;
import wordfeud.views.BoardPanel;


public class GameModel extends CoreModel {

	private CompetitionModel competition;
	private AccountModel opponent;
	private AccountModel challenger;
	private AccountModel currentUser;
	private int gameId;
	private String state;
	private String boardName;
	private String letterSet;
	private boolean iamchallenger;
	private BoardPanel boardPanel;
	private int currentobserveturn;
	private StashModel playerStash;
	private BoardModel boardModel;

	private final String getGameQuery = "SELECT * FROM `spel` WHERE `ID` = ?";
	private final String getOpenQuery = "SELECT * FROM `gelegdeletter` WHERE Tegel_Y =? AND Tegel_X = ? AND Letter_Spel_ID = ?";
	private final String getScoreQuery = "SELECT `totaalscore` FROM `score` WHERE `Spel_ID` = ? AND `Account_Naam` != ?";
	private final String getTurnQuery = "SELECT LetterType_karakter, Tegel_X, Tegel_Y, BlancoLetterKarakter, beurt_ID, ID FROM gelegdeletter, letter WHERE gelegdeletter.Spel_ID = ? AND gelegdeletter.Letter_ID = letter.ID AND gelegdeletter.beurt_ID = ? ORDER BY beurt_ID ASC;;";
	private final String getLastTurnQuery = "SELECT Account_naam, ID FROM beurt WHERE Spel_ID = ? ORDER BY ID DESC LIMIT 0, 1";

	private final String getBoardQuery = "SELECT  `gl`.`Spel_ID` ,  `gl`.`Beurt_ID` ,  `l`.`LetterType_karakter` ,  `gl`.`Tegel_X` ,  `gl`.`Tegel_Y` ,  `gl`.`BlancoLetterKarakter`,`l`.`ID` FROM  `gelegdeletter` AS  `gl` JOIN  `letter` AS  `l` ON ( (`l`.`Spel_ID` =  `gl`.`Spel_ID`)AND(`l`.`ID` =  `gl`.`Letter_ID`) ) JOIN  `spel`  `s` ON  `s`.`id` =  `gl`.`Spel_ID` JOIN  `letterset` AS  `ls` ON  `ls`.`code` =  `s`.`LetterSet_naam` WHERE gl.Spel_ID =?";
	private final String getTileValue = "Select waarde FROM lettertype WHERE karakter = ? AND LetterSet_code = ?";
	private final String whosTurnAtTurn = "SELECT account_naam, ID FROM `beurt` WHERE `spel_id` = ? AND ID = ?";

	private final String scoreQuery = "SELECT ID , score FROM beurt WHERE score IS NOT NULL AND score != 0 AND Account_naam = ?";
	private String getWordMoveCount = "SELECT COUNT(*) FROM `beurt` WHERE `Aktie_Type` = 'word' AND `spel_id` = ?";

	private final String getnumberofturns = "SELECT max(ID) FROM beurt   WHERE Spel_ID = ?";
	private final boolean observer;
	private boolean hasTurn = true;
	private boolean hasButtons = false;

	public GameModel(int gameId, AccountModel user, BoardModel boardModel,
			BoardPanel boardPanel, boolean observer) {
		this.observer = observer;
		this.boardModel = boardModel;
		this.boardPanel = boardPanel;
		currentUser = user;

		try {
			Future<ResultSet> worker = Db.run(new Query(getGameQuery)
					.set(gameId));

			ResultSet dbResult = worker.get();
			int numRows = Query.getNumRows(dbResult);

			if (numRows == 1) {
				dbResult.next();
				this.gameId = gameId;
				updatelastturn();
				competition = new CompetitionModel(
						dbResult.getInt("competitie_id"));
				state = dbResult.getString("toestand_type");
				String challengerName = dbResult
						.getString("account_naam_uitdager");

				String challengeeName = dbResult
						.getString("account_naam_tegenstander");

				boardName = dbResult.getString(9);
				letterSet = dbResult.getString(10);
				if (!(observer)) {
					if (challengerName.equals(currentUser.getUsername())) {
						opponent = new AccountModel(challengeeName);
						challenger = new AccountModel(challengerName);
						iamchallenger = true;
					} else {
						opponent = new AccountModel(challengerName);
						challenger = new AccountModel(challengeeName);
						iamchallenger = false;
					}
				} else {
					opponent = new AccountModel(challengeeName);
					challenger = new AccountModel(challengerName);
					iamchallenger = false;

				}

				playerStash = new StashModel(currentUser, this);

			}

		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	public boolean hasButtons() {
		return hasButtons;
	}

	public void setButtons(boolean hasbuttons) {
		this.hasButtons = hasbuttons;
	}

	private void createTurn(Turn action, int score) throws SQLException,
			InterruptedException, ExecutionException {
		int newTurnId = getNextTurnId();
		String username = currentUser.getUsername();
		Query turn = new Query(
				"INSERT INTO beurt(ID, Spel_ID, Account_naam, score ,Aktie_type) VALUES(?, ?, ?, ?, ?)");

		switch (action) {
		case BEGIN:
		case END:
		case WORD:
			turn.set((newTurnId)).set(gameId).set(username).set(score)
					.set(action);
			break;

		// No score
		case PASS:
		case RESIGN:
		case SWAP:
			turn.set((newTurnId)).set(gameId).set(username).set(0).set(action);
			break;

		default:
			throw new IllegalArgumentException("Invalid Turn action");
		}

		// Notify the views that we no longer have the current turn
		firePropertyChange(Event.MOVE, true, false);
		Db.run(turn);
	}

	public BoardModel getBoardFromDatabase() {
		BoardModel oldBoard = new BoardModel();
		try {
			Future<ResultSet> worker = Db.run(new Query(getBoardQuery)
					.set(gameId));

			ResultSet rs = worker.get();
			while (rs.next()) {
				int x = rs.getInt(4) - 1;// x
				int y = rs.getInt(5) - 1;// y
				if (x > -1 && y > -1) {
					String character = rs.getString("LetterType_karakter");
					if (character.equalsIgnoreCase("?")) {

						oldBoard.setValueAt(
								new Tile(rs.getString("BlancoLetterKarakter"),
										0, Tile.NOT_MUTATABLE, rs.getInt("ID")),
								y, x);
					} else {

						Future<ResultSet> worker1 = Db.run(new Query(
								getTileValue).set(character).set(letterSet));
						ResultSet tilewaarde = worker1.get();
						tilewaarde.next();
						oldBoard.setValueAt(
								new Tile(character, tilewaarde.getInt(1),
										Tile.NOT_MUTATABLE, rs.getInt("ID")),
								y, x);
					}
				}
			}

		} catch (SQLException | InterruptedException | ExecutionException sql) {
			sql.printStackTrace();
		}
		return oldBoard;
	}

	public void setPlayerTiles() {
		int turnId = getLastTurn(currentUser);
		setPlayerTiles(turnId);
	}

	public void setPlayerTiles(int turnId) {
		playerStash.addRandomTiles();
		Tile[] tiles = playerStash.getPlayerTiles(turnId);
		boardPanel.setPlayerTiles(tiles);
	}

	public BoardModel getBoardModel() {
		return boardModel;
	}

	public String getBoardName() {
		return boardName;
	}

	public AccountModel getChallenger() {
		return challenger;
	}

	public CompetitionModel getCompetition() {
		return competition;
	}

	public int getCurrentobserveturn() {
		return currentobserveturn;
	}

	public int getLastTurn(AccountModel player) {
		Future<ResultSet> worker;
		try {
			worker = Db.run(new Query(Queries.LATEST_TURN).set(gameId).set(
					player.getUsername()));

			ResultSet res = worker.get();
			// Should always have a result
			res.next();
			return res.getInt(1);
		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		// Should never happen
		return 0;
	}

	public void setBoardModel(BoardModel model) {
		boardModel = model;
	}

	public int getGameId() {
		return gameId;
	}

	public String getLetterSet() {
		return letterSet;
	}

	public int getNumberOfTotalTurns() {

		try {
			Future<ResultSet> worker = Db.run(new Query(getnumberofturns)
					.set(gameId));
			ResultSet dbResultamountofturns = worker.get();
			dbResultamountofturns.next();
			return dbResultamountofturns.getInt(1);
		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public AccountModel getOpponent() {
		return opponent;
	}

	public String[] getRequestedWords() {
		String[] words = null;
		String query = "SELECT `woord` FROM `nieuwwoord` WHERE `status` = `pending`";
		try {
			Future<ResultSet> worker = Db.run(new Query(query));
			ResultSet res = worker.get();
			int numRows = Query.getNumRows(res);

			words = new String[numRows];
			int i = 0;
			while (res.next()) {
				words[i] = res.getString(1);
				i++;
			}
		} catch (SQLException | InterruptedException | ExecutionException sql) {
			sql.printStackTrace();
		}

		return words;
	}

	public int getScore(String userName) {
		try {
			Future<ResultSet> worker = Db.run(new Query(getScoreQuery).set(
					gameId).set(userName));

			ResultSet rs = worker.get();
			rs.next();
			return rs.getInt(1);
		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public String getState() {
		return state;
	}

	public Tile[] getPlayerTiles() {
		return playerStash.getPlayerTiles();
	}

	public boolean isFree(int x, int y) {
		try {
			Future<ResultSet> worker = Db.run(new Query(getOpenQuery)
					.set(y + 1).set(x).set(gameId));
			ResultSet rs = worker.get();
			if (Query.getNumRows(rs) == 0) {
				return true;
			}
		} catch (SQLException | InterruptedException | ExecutionException sql) {
			sql.printStackTrace();
		}
		return false;
	}

	public boolean isIamchallenger() {
		return iamchallenger;
	}

	public boolean isObserver() {
		return observer;
	}

	public void requestWord(String word) {
		String status = "pending";

		String query = "INSERT INTO `woordenboek` (`woord`, `status`) VALUES (?, ?)";
		try {
			Db.run(new Query(query).set(word).set(status));
		} catch (SQLException sql) {
			sql.printStackTrace();
		}
	}

	/**
	 * Method to resign the game
	 * 
	 */
	public void resign() {
		int penalty = getStashPenalty();
		try {
			createTurn(Turn.RESIGN, penalty);
			updateGameState(State.RESIGN);
		} catch (SQLException | InterruptedException | ExecutionException sql) {
			sql.printStackTrace();
		}
	}

	/**
	 * Method to end the game
	 * 
	 */
	public void end() {
		int penalty = getStashPenalty();
		try {
			createTurn(Turn.END, penalty);
			updateGameState(State.FINISH);
		} catch (SQLException | InterruptedException | ExecutionException sql) {
			sql.printStackTrace();
		}
	}

	/**
	 * Get the number of negative number of points which are left on a player's
	 * stash
	 * 
	 * @return the number of points that should be penaltilized for left over
	 *         Tiles
	 */
	private int getStashPenalty() {
		int penalty = 0;
		Tile[] remainingTiles = playerStash.getPlayerTiles();
		for (Tile tile : remainingTiles) {
			penalty += tile.getValue();
		}

		// Make the penalty negative
		penalty *= -1;

		return penalty;
	}

	private void updateGameState(State newState) throws SQLException {
		if (newState == State.RESIGN) {
			firePropertyChange(Event.RESIGN, null, Event.RESIGN);
		}
		Query q = new Query(
				"UPDATE `spel` SET `Toestand_type` = ? WHERE `ID` = ?");
		q.set(newState).set(gameId);
		Db.run(q);
	}

	public int getScoreChallenger(int toTurn){
		int score = 0;
		try {
			Future<ResultSet> databaseTotalScore = Db.run(new Query("SELECT SUM(score) from beurt where spel_id = ? and account_naam=? and ID <= ?;").set(this.gameId).set(this.challenger.getUsername()).set(toTurn));
			ResultSet res = databaseTotalScore.get();
			res.first();
			score = res.getInt(1);
		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return score;
	}
	
	public int getScoreOpponent(int toTurn){
		int score = 0;
		try {
			Future<ResultSet> databaseTotalScore = Db.run(new Query("SELECT SUM(score) from beurt where spel_id = ? and account_naam=? and ID <= ?;").set(this.gameId).set(this.opponent.getUsername()).set(toTurn));
			ResultSet res = databaseTotalScore.get();
			res.first();
			score = res.getInt(1);
		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return score;
	}
	

	public void setCurrentobserveturn(int currentobserveturn) {
		System.out.println(currentobserveturn);
		this.currentobserveturn = currentobserveturn;
	}


	@Override
	public String toString() {
		// return gameId + "";
		return "(" + gameId + ") " + competition.getDesc() + " : "
				+ challenger.getUsername() + " - " + opponent.getUsername();
	}

	@Override
	public void update() {
		boolean oldHasTurn = hasTurn;
		hasTurn = yourturn();

		firePropertyChange(Event.MOVE, oldHasTurn, hasTurn);
	}

	public void updateboardfromdatabasetoturn(int turn_id) {
		try { // "SELECT LetterType_karakter, Tegel_X, Tegel_Y, BlancoLetterKarakter, beurt_ID FROM gelegdeletter, letter WHERE gelegdeletter.Letter_Spel_ID = ? AND gelegdeletter.Letter_ID = letter.ID AND gelegdeletter.beurt_ID = ? ORDER BY beurt_ID ASC;;";

			Future<ResultSet> worker = Db.run(new Query(getTurnQuery).set(
					gameId).set(turn_id));
			ResultSet rs = worker.get();
			while (rs.next()) {
				int x = rs.getInt("Tegel_X") - 1;// x
				int y = rs.getInt("Tegel_Y") - 1;// y
		
				if (rs.getString("LetterType_karakter").equals("?")) {
					boardModel.setValueAt(
							new Tile(rs.getString("BlancoLetterKarakter"), 0,
									Tile.NOT_MUTATABLE, rs.getInt("beurt_ID")),
							y, x);

				} else {
					Future<ResultSet> worker2 = Db.run(new Query(getTileValue)
							.set(rs.getString("LetterType_karakter")).set(
									letterSet));
					ResultSet tilewaarde = worker2.get();
					tilewaarde.next();
					boardModel.setValueAt(
							new Tile(rs.getString("LetterType_karakter"),
									tilewaarde.getInt(1), Tile.NOT_MUTATABLE,
									rs.getInt("beurt_ID")), y, x);
				}
			}
			boardModel.update();

		} catch (SQLException | InterruptedException | ExecutionException sql) {
			sql.printStackTrace();
		}
	}

	public boolean whosturn() {
		// dont use unless observing
		// use yourturn instead

		try {
			System.out.println(getGameId() + "  " + currentobserveturn);
			Future<ResultSet> worker = Db.run(new Query(whosTurnAtTurn).set(
					getGameId()).set(currentobserveturn));
			ResultSet res = worker.get();
			int numRows = Query.getNumRows(res);

			if (numRows == 0) {
				return true;
			}
			res.next();
			String lastturnplayername = res.getString(1);

			if (lastturnplayername.equals(challenger.getUsername())) {
				return false;
			} else {
				return true;
			}

		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		return false;

	}

	private int getNextTurnId() throws InterruptedException,
			ExecutionException, SQLException {
		return getCurrentTurnId() + 1;
	}

	private int getCurrentTurnId() throws InterruptedException,
			ExecutionException, SQLException {
		int nextTurn = 0;

		ResultSet rs = Db.run(new Query(getLastTurnQuery).set(gameId)).get();
		rs.first();
		nextTurn = rs.getInt(2);

		return nextTurn;
	}

	public String getNextTurnUsername() {
		String nextuser = "";
		String username;
		try {
			ResultSet rs = Db.run(new Query(getLastTurnQuery).set(gameId))
					.get();

			rs.next();
			username = rs.getString(1);

			if (username.equals(opponent.getUsername())) {
				nextuser = challenger.getUsername();
			} else if (username.equals(challenger.getUsername())) {
				nextuser = opponent.getUsername();
			}

		} catch (InterruptedException | ExecutionException | SQLException e) {
			e.printStackTrace();
		}
		return nextuser;
	}

	public boolean yourturn() {
		return getNextTurnUsername().equals(currentUser.getUsername());
	}

	public int getvalueforLetter(String letter) {

		try {
			Future<ResultSet> worker = Db.run(new Query(getTileValue).set(
					letter).set(letterSet));
			ResultSet tileValue = worker.get();

			tileValue.next();
			return tileValue.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public BoardPanel getBoardPanel() {
		return this.boardPanel;
	}

	public void updatelastturn() {
		currentobserveturn = getNumberOfTotalTurns();
	}

	// legwoord methodes //
	public void playWord(BoardModel newBoard) throws InvalidMoveException {
		if (!yourturn()) {
			throw new InvalidMoveException(Error.NOTYOURTURN);
		}

		try {
			ArrayList<Tile> currentStash = new ArrayList<Tile>(
					Arrays.asList(playerStash.getPlayerTiles()));
			Tile[][] newBoardData = newBoard.getTileData();
			if(!checkIfWordIsNotNull(getBoardFromDatabase(), newBoard)) {
				Tile[][] playedLetters = (Tile[][]) checkValidMove(
						getBoardFromDatabase(), newBoard);
				ArrayList<String> playedWords = new ArrayList<String>();
				ArrayList<ArrayList<Tile>> letterMatrix = checkValidWord(
						playedLetters, newBoardData);
				for (ArrayList<Tile> word : letterMatrix) {
					String tmpWord = "";
					for (Tile t : word) {
						tmpWord += t.getLetter();
					}
					playedWords.add(tmpWord);
				}
				checkWordsInDatabase(playedWords);
	
				int score = getScore(playedLetters, letterMatrix, newBoard);
	
				ArrayList<Tile> playedTiles = new ArrayList<Tile>();
	
				Query insertLetterQuery = new Query(
						"INSERT INTO gelegdeletter(Letter_ID, Spel_ID, Beurt_ID, Tegel_X, Tegel_Y, Tegel_Bord_naam)"
								+ "VALUES (?, ?, ?, ?, ?, ?);");
	
				int turnId = getNextTurnId();
	
				// Insert word in to database
				for (int y = 0; y < 15; y++) {
					for (int x = 0; x < 15; x++) {
						if (playedLetters[y][x] != null) {
	
							Tile tile = (Tile) playedLetters[y][x];
							int tileId = tile.getTileId();
	
							// Add the tile to the array,
							// These will be removed from the players stash
							playedTiles.add(tile);
	
							insertLetterQuery.set(tileId).set(gameId).set(turnId)
									.set(x + 1).set(y + 1).set("standard");
							insertLetterQuery.addBatch();
						}
					}
				}
				createTurn(Turn.WORD, score);
				Db.run(insertLetterQuery);
	
				updatePlayerStash(currentStash, playedTiles);
			}
			else {
				throw new InvalidMoveException(Error.NO_LETTERS_PUT);
			}
		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

	}

	private boolean checkIfWordIsNotNull(BoardModel oldBoard,
			BoardModel newBoard) {
		Tile[][] oldData = oldBoard.getTileData();
		Tile[][] newData = newBoard.getTileData();

		// First find out which letters where played
		Tile[][] playedLetters = compareFields(oldData, newData);
		if (playedLetters == null) {
			return false;
		}
		else {
			return true;
		}
	}

	/**
	 * Make a swap turn
	 * 
	 * @param nominees
	 *            ArrayList with Tiles to remove
	 */
	public void swap(ArrayList<Tile> nominees) {
		ArrayList<Tile> currentStash = getCurrentStash();

		try {
			createTurn(Turn.SWAP, 0);
			updatePlayerStash(currentStash, nominees);
		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Removes oldTiles from the currentStash, then refill the stash with random
	 * Tiles (if possible) It is important that the turn already has been
	 * created, else the random letters will be set for the current turn.
	 * 
	 * @param currentStash
	 * @param oldTiles
	 */
	private void updatePlayerStash(ArrayList<Tile> currentStash,
			ArrayList<Tile> oldTiles) {

		currentStash.removeAll(oldTiles);
		for (Tile t : currentStash) {
			playerStash.addTile(t);
		}

		playerStash.addRandomTiles();
	}

	private ArrayList<Tile> getCurrentStash() {
		return new ArrayList<Tile>(Arrays.asList(playerStash.getPlayerTiles()));
	}

	private void checkWordsInDatabase(ArrayList<String> words)
			throws InvalidMoveException, InterruptedException,
			ExecutionException, SQLException {
		for (String s : words) {
			String query = "INSERT INTO woordenboek(woord, `status`) VALUES(?,'Pending')";
			String getWordFromDatabase = "SELECT * FROM woordenboek WHERE woord = ?;";

			ResultSet wordresult = Db
					.run(new Query(getWordFromDatabase).set(s)).get();
			if (Query.getNumRows(wordresult) == 0) {
				Db.run(new Query(query).set(s));
				throw new InvalidMoveException(Error.PENDING);
			} else if (wordresult.next()) {
				String statusString = wordresult.getString("status");
				if (statusString.equals("Denied")) {
					throw new InvalidMoveException(Error.DENIED);
				} else if (statusString.equals("Pending")) {
					throw new InvalidMoveException(Error.ALREADY_PENDING);
				}
			}

		}
	}

	private ArrayList<ArrayList<Tile>> checkValidWord(Tile[][] playedLetters,
			Tile[][] newBoard) throws InvalidMoveException {
		// verticaal woord
		ArrayList<ArrayList<Tile>> verticalWords = new ArrayList<ArrayList<Tile>>();
		ArrayList<ArrayList<Tile>> horizontalWords = new ArrayList<ArrayList<Tile>>();
		for (int y = 0; y < 15; y++) {
			for (int x = 0; x < 15; x++) {
				if (playedLetters[y][x] != null) {
					// er is op dit coordinaat een letter neergelegd en de x en
					// de y worden opgeslagen.
					int counterY = y;
					int counterX = x;
					boolean beenLeft = false;
					ArrayList<Tile> verticalWord = new ArrayList<Tile>();
					ArrayList<Tile> horizontalWord = new ArrayList<Tile>();
					while (counterX >= 0) {
						// hij gaat een letter naar links tot hij een lege
						// plaats tegenkomt.
						if (counterX > 0
								&& newBoard[counterY][counterX - 1] != null
								&& (!beenLeft)) {
							counterX--;
						} else if (counterX < 14
								&& newBoard[counterY][counterX + 1] != null
								&& newBoard[counterY][counterX] != null) {
							beenLeft = true;
							// als hij nog niet terug rechts is slaat hij de
							// letter op in de array en telt hij en gaat hij
							// naar de volgende;
							horizontalWord.add(newBoard[counterY][counterX]);
							counterX++;
						} else if (newBoard[counterY][counterX] != null) {
							horizontalWord.add(newBoard[counterY][counterX]);
							break;
						}
					}
					counterY = y;
					counterX = x;
					boolean beenTop = false;
					while (counterY >= 0) {
						if (counterY > 0
								&& newBoard[counterY - 1][counterX] != null
								&& (!beenTop)) {
							counterY--;
						} else if (counterY < 14
								&& newBoard[counterY + 1][counterX] != null
								&& newBoard[counterY][counterX] != null) {
							beenTop = true;
							verticalWord.add(newBoard[counterY][counterX]);
							counterY++;
						} else if (newBoard[counterY][counterX] != null) {
							verticalWord.add(newBoard[counterY][counterX]);
							break;
						}
					}
					if (!verticalWords.contains(verticalWord)) {
						if (verticalWord.size() > 1) {
							verticalWords.add(verticalWord);
						}
					}
					if (!horizontalWords.contains(horizontalWord)) {
						if (horizontalWord.size() > 1) {
							horizontalWords.add(horizontalWord);
						}
					}
				}
			}
		}
		ArrayList<ArrayList<Tile>> comparableWords = new ArrayList<ArrayList<Tile>>();
		for (ArrayList<Tile> word : verticalWords) {
			comparableWords.add(word);
		}
		for (ArrayList<Tile> word : horizontalWords) {
			comparableWords.add(word);
		}
		if (comparableWords.size() < 2) {
			Point[] letterPositions = MatrixUtils.getCoordinates(playedLetters);
			if (comparableWords.size() == 0) {
				throw new InvalidMoveException(Error.TOSHORT);
			} else if (!isBoardEmpty()
					&& !(comparableWords.get(0).size() > letterPositions.length)) {
				throw new InvalidMoveException(Error.NOT_ATTACHED);
			}
		}
		return comparableWords;

	}

	public boolean isBoardEmpty() {
		try {
			ResultSet rs = Db.run(new Query(getWordMoveCount).set(gameId))
					.get();

			rs.next();
			int wordMoveCount = rs.getInt(1);
			return wordMoveCount == 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private int getScore(Tile[][] playedLetters,
			ArrayList<ArrayList<Tile>> words, BoardModel currentBoard) {
		int[][] multipliers = new int[15][15];
		int totalScore = 0;
		for (int vertical = 0; vertical < 15; vertical++) {
			for (int horizontal = 0; horizontal < 15; horizontal++) {
				multipliers[vertical][horizontal] = currentBoard
						.getMultiplier(new Point(horizontal, vertical));
			}
		}

		for (ArrayList<Tile> word : words) {
			int wordscore = 0;
			int wordMultiplier = 1;
			for (Tile t : word) {
				wordscore = wordscore + t.getValue();
				for (int vertical = 0; vertical < 15; vertical++) {
					for (int horizontal = 0; horizontal < 15; horizontal++) {
						if (playedLetters[vertical][horizontal] != null) {
							if (playedLetters[vertical][horizontal].equals(t)) {
								int multiplier = multipliers[vertical][horizontal];
								switch (multiplier) {
								case BoardModel.DL:
									wordscore = wordscore + t.getValue();
									break;
								case BoardModel.TL:
									wordscore = wordscore + (t.getValue() * 2);
									break;
								case BoardModel.DW:
									wordMultiplier = (wordMultiplier * 2);
									break;
								case BoardModel.TW:
									wordMultiplier = (wordMultiplier * 3);
									break;

								}
							}
						}
					}
				}
			}
			wordscore = (wordscore * wordMultiplier);
			totalScore = totalScore + wordscore;
		}
		return totalScore;
	}

	private Tile[][] checkValidMove(BoardModel oldBoard, BoardModel newBoard)
			throws InvalidMoveException {

		Tile[][] oldData = oldBoard.getTileData();
		Tile[][] newData = newBoard.getTileData();

		// First find out which letters where played
		Tile[][] playedLetters = compareFields(oldData, newData);
		Point[] letterPositions = MatrixUtils.getCoordinates(playedLetters);
		if (isBoardEmpty()) {

			boolean onStar = false;
			Point starCoord = oldBoard.getStartPoint();

			// Coords for all currently played letters

			for (Point letterPos : letterPositions) {
				if (starCoord.x == letterPos.x && starCoord.y == letterPos.y) {
					onStar = true;
					break;
				}
			}

			if (!onStar) {
				throw new InvalidMoveException(
						Error.NOT_ON_START);
			}
		}

		if (MatrixUtils.isEmpty(playedLetters)) {
			throw new InvalidMoveException(Error.NO_LETTERS_PUT);
		}

		if (!wordIsAlligned(playedLetters)) {
			throw new InvalidMoveException(Error.NOT_ALIGNED);
		}

		if (!lettersAreConnected(playedLetters, newBoard)) {
			throw new InvalidMoveException(Error.NOT_CONNECTED);
		}

		return playedLetters;
		// Everything went better than expected.jpg :)
	}

	private boolean lettersAreConnected(Tile[][] playedLetters,
			BoardModel newBoard) {
		Point[] letterPositions = MatrixUtils.getCoordinates(playedLetters);
		int lowY = 100;
		int maxY = 0;
		int lowX = 100;
		int maxX = 0;

		for (Point p : letterPositions) {
			if (p.x < lowX) {
				lowX = p.x;
			}
			if (p.x > maxX) {
				maxX = p.x;
			}
			if (p.y < lowY) {
				lowY = p.y;
			}
			if (p.y > maxY) {
				maxY = p.y;
			}
		}

		if (maxX - lowX == 0) {
			while (maxY >= lowY) {
				Tile[][] data = newBoard.getTileData();
				Tile tile = data[lowX][maxY];
				if (tile == null) {
					return false;
				}
				maxY--;
			}

		} else if (maxY - lowY == 0) {
			while (maxX >= lowX) {
				if (newBoard.getTileData()[maxX][lowY] == null) {
					return false;
				}
				maxX--;
			}
		} else {
			return false;
		}
		return true;
	}

	private boolean wordIsAlligned(Object[][] playedLetters) {
		Point[] letterPositions = MatrixUtils.getCoordinates(playedLetters);
		int holdX = (int) letterPositions[0].getX();
		int holdY = (int) letterPositions[0].getY();
		if (letterPositions.length == 1) {
			return true;
		}
		if (holdX == (int) letterPositions[1].getX()) {
			for (Point p : letterPositions) {
				if (p.getX() != holdX) {
					return false;
				}
			}
		} else if (holdY == (int) letterPositions[1].getY()) {
			for (Point p : letterPositions) {
				if (p.getY() != holdY) {
					return false;
				}
			}
		}
		return true;
	}

	public Tile[][] compareFields(Tile[][] oldField, Tile[][] newField) {
		Tile[][] playedLetters = new Tile[15][15];
		for (int vertical = 0; vertical < 15; vertical++) {
			for (int horizontal = 0; horizontal < 15; horizontal++) {
				if (oldField[vertical][horizontal] == null
						&& newField[vertical][horizontal] != null) {
					playedLetters[vertical][horizontal] = newField[vertical][horizontal];
				} else {
					playedLetters[vertical][horizontal] = null;
				}
			}
		}
		return playedLetters;
	}

	// einde legwoordmethodes//
}
