
package legwoord;

import java.awt.Dimension;
import java.awt.Point;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import nl.avans.min04sob.scrabble.controllers.BoardController;
import nl.avans.min04sob.scrabble.core.db.Db;
import nl.avans.min04sob.scrabble.core.db.Query;
import nl.avans.min04sob.scrabble.misc.InvalidMoveException;
import nl.avans.min04sob.scrabble.misc.MatrixUtils;
import nl.avans.min04sob.scrabble.models.AccountModel;
import nl.avans.min04sob.scrabble.models.BoardModel;
import nl.avans.min04sob.scrabble.models.CompetitionModel;
import nl.avans.min04sob.scrabble.models.StashModel;
import nl.avans.min04sob.scrabble.models.Tile;
import nl.avans.min04sob.scrabble.views.BoardPanel;

public class LegWoordMethodes {

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
	private StashModel stash;
	private int currentobserveturn;

	private BoardController boardcontroller;
	private BoardModel boardModel;
	private final boolean observer;

	public LegWoordMethodes(int gameId, AccountModel user,
			BoardModel boardModel, BoardPanel boardPanel, boolean observer) {
		this.observer = observer;
		this.boardModel = boardModel;
		this.boardPanel = boardPanel;
		currentUser = user;
		stash = new StashModel();

		try {
			String getGameQuery = "SELECT * FROM `spel` WHERE `ID` = ?";
			Future<ResultSet> worker = Db.run(new Query(getGameQuery)
					.set(gameId));

			ResultSet dbResult = worker.get();
			int numRows = Query.getNumRows(dbResult);

			if (numRows == 1) {
				dbResult.next();
				this.gameId = gameId;
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

			}

		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	//tijdelijk
	public void setboard(BoardModel boardmodel){
		this.boardModel = boardmodel;
	}
	//einde tijdelijk
	
	public ArrayList<ArrayList<Tile>> checkValidWord(Tile[][] playedLetters,
			Tile[][] newBoard) throws InvalidMoveException {
		// verticaal woord
		ArrayList<ArrayList<Tile>> verticalenWoorden = new ArrayList<ArrayList<Tile>>();
		ArrayList<ArrayList<Tile>> horizontalenWoorden = new ArrayList<ArrayList<Tile>>();
		for (int y = 0; y < 15; y++) {
			for (int x = 0; x < 15; x++) {
				if (playedLetters[y][x] != null) {
					// er is op dit coordinaat een letter neergelegd en de x en
					// de y worden opgeslagen.
					int counterY = y;
					int counterX = x;
					boolean beenLeft = false;
					ArrayList<Tile> verticaalWoord = new ArrayList<Tile>();
					ArrayList<Tile> horizontaalWoord = new ArrayList<Tile>();
					while (counterX >= 0) {
						// hij gaat een letter naar links tot hij een lege
						// plaats tegenkomt.
						if (counterX > 0 && newBoard[counterY][counterX - 1] != null
								&& (!beenLeft)) {
							counterX--;
						} else if (newBoard[counterY][counterX + 1] != null
								&& newBoard[counterY][counterX] != null) {
							beenLeft = true;
							// als hij nog niet terug rechts is slaat hij de
							// letter op in de array en telt hij en gaat hij
							// naar de volgende;
							horizontaalWoord.add(newBoard[counterY][counterX]);
							counterX++;
						} else if (newBoard[counterY][counterX] != null) {
							horizontaalWoord.add(newBoard[counterY][counterX]);
							break;
						}
					}
					counterY = y;
					counterX = x;
					boolean beenTop = false;
					while (counterY >= 0) {
						if (counterY > 0 && newBoard[counterY - 1][counterX] != null
								&& (!beenTop)) {
							counterY--;
						} else if (newBoard[counterY + 1][counterX] != null
								&& newBoard[counterY][counterX] != null) {
							beenTop = true;
							verticaalWoord.add(newBoard[counterY][counterX]);
							counterY++;
						} else if (newBoard[counterY][counterX] != null) {
							verticaalWoord.add(newBoard[counterY][counterX]);
							break;
						}
					}
					if (!verticalenWoorden.contains(verticaalWoord)) {
						if (verticaalWoord.size() > 1) {
							verticalenWoorden.add(verticaalWoord);
						}
					}
					if (!horizontalenWoorden.contains(horizontaalWoord)) {
						if (horizontaalWoord.size() > 1) {
							horizontalenWoorden.add(horizontaalWoord);
						}
					}
				}
			}
		}
		ArrayList<ArrayList<Tile>> teVergelijkenWoorden = new ArrayList<ArrayList<Tile>>();
		for (ArrayList<Tile> woord : verticalenWoorden) {
			teVergelijkenWoorden.add(woord);
		}
		for (ArrayList<Tile> woord : horizontalenWoorden) {
			teVergelijkenWoorden.add(woord);
		}
		if(teVergelijkenWoorden.size() <2){
			Point[] letterPositions = MatrixUtils.getCoordinates(playedLetters);
			if(teVergelijkenWoorden.get(0).size() > letterPositions.length){	
			}else if(!firstTurn()){
				throw new InvalidMoveException(InvalidMoveException.STATE_NOT_ATTACHED);
			}
		}
		return teVergelijkenWoorden;

	}

	public int getScore(Tile[][] playedLetters,
		ArrayList<ArrayList<Tile>> words, BoardModel currentBoard) {
		int[][] multipliers = new int[15][15];
		int totalScore = 0;
		for(int vertical = 0; vertical < 15; vertical++){
			for(int horizontal = 0; horizontal < 15; horizontal++){
				multipliers[vertical][horizontal] = currentBoard.getMultiplier(new Point(horizontal, vertical));
			}
		}
		
		for(ArrayList<Tile> word : words){
			int wordscore = 0;
			boolean doubleword1 = false;
			boolean doubleword2 = false;
			boolean tripleword1 = false;
			boolean tripleword2 = false;
			for(Tile t : word){
				wordscore = wordscore + t.getValue();
				for(int vertical=0;vertical<15;vertical++){
					for(int horizontal=0;horizontal<15;horizontal++){
						if(playedLetters[vertical][horizontal] != null){
							if(playedLetters[vertical][horizontal].equals(t)){
								int multiplier = multipliers[vertical][horizontal];
								switch(multiplier){
								case BoardModel.DL:
									wordscore = wordscore + (t.getValue()*2);
								case BoardModel.TL :
									wordscore = wordscore + (t.getValue() * 3);
								case BoardModel.DW :
									if(doubleword1 == false){
										doubleword1 = true;
									}else{
										doubleword2 = true;
									}
								case BoardModel.TW :
									if(tripleword1 == false){
										tripleword1 = true;
									}else{
										tripleword2 = true;
									}
								}
							}
						}
					}
				}
			}
			if(doubleword1){
				if(doubleword2){
					wordscore = wordscore * 4;
				}else{
					wordscore = wordscore * 2;
				}
			}
			if(tripleword1){
				if(tripleword2){
					wordscore = wordscore * 9;
				}else{
					wordscore = wordscore * 3;
				}
			}
			totalScore = totalScore + wordscore;
		}
		return totalScore;
	}

	public void playWord(BoardModel newBoard) throws InvalidMoveException {
		try {
			Tile[][] newBoardData = newBoard.getTileData();
			Tile[][] playedLetters = (Tile[][]) checkValidMove(boardModel,
					newBoard);
			ArrayList<String> teVergelijkenWoordenString = new ArrayList<String>();
			ArrayList<ArrayList<Tile>> teVergelijkenWoorden = checkValidWord(
					playedLetters, newBoardData);
			for (ArrayList<Tile> woord : teVergelijkenWoorden) {
				String tempwoord = "";
				for (Tile t : woord) {
					tempwoord += t.getLetter();
				}
				teVergelijkenWoordenString.add(tempwoord);
			}
			checkWordsInDatabase(teVergelijkenWoordenString);

			 int score = getScore(playedLetters, teVergelijkenWoorden,
			 newBoard);

			String createTurn = "INSERT INTO beurt(ID, Spel_ID, Account_naam, score ,Aktie_type) VALUES(?, ?, ?, ?, 'Word')";
			//create een nieuwe beurt in de database;
			int nextTurn = getNextTurnId();
			Db.run(new Query(createTurn).set((nextTurn)).set(gameId)
			.set(getNextTurnUsername()).set(score));
			System.out.println("works " + score);

			// leg het woord in de database
			for (int y = 0; y < 15; y++) {
				for (int x = 0; x < 15; x++) {
					if (playedLetters[y][x] != null) {
						Db.run(new Query(
								"INSERT INTO gelegdeletter(Letter_ID, Spel_ID, Beurt_ID, Tegel_X, Tegel_Y, Tegel_Bord_naam, BlancoLetterKarakter)"
										+ "VALUES (?, ?, ?, ?, ?, ?, ?);")
								.set(((Tile) playedLetters[y][x]).getTileId())
								.set(gameId).set(nextTurn).set(x + 1)
								.set(y + 1).set("Standard").set(""));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void checkWordsInDatabase(ArrayList<String> words)
			throws InvalidMoveException {
		for (String s : words) {
			String query = "INSERT INTO woordenboek(woord, `status`) VALUES(?,'Pending')";
			String getWordFromDatabase = "SELECT * FROM woordenboek WHERE woord = ?;";
			try {
				ResultSet wordresult = Db.run(
						new Query(getWordFromDatabase).set(s)).get();
				if (Query.getNumRows(wordresult) == 0) {
					Db.run(new Query(query).set(s));
					throw new InvalidMoveException(
							InvalidMoveException.STATE_SETPENDING);
				} else if (wordresult.next()) {
					String statusString = wordresult.getString("status");
					if (statusString.equals("Denied")) {
						throw new InvalidMoveException(
								InvalidMoveException.STATE_DENIED);
					} else if (statusString.equals("Pending")) {
						throw new InvalidMoveException(
								InvalidMoveException.STATE_PENDING);
					}
				}
			} catch (SQLException | InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}



	
	
	
	
	
	
	
	
	
	
	
	
	public void putCharactersInDatabase (int gameId){
		try {
			int totalcounter = 0;
			ResultSet rs = Db.run(new Query("SELECT * FROM lettertype")).get();
			while (rs.next()){
				int counter = 0;
				int maxofthisletter = rs.getInt("aantal");
				String letter = rs.getString("karakter");
				String setcode = rs.getString("LetterSet_code");
				while(counter < maxofthisletter){
					Db.run(new Query("INSERT INTO letter(ID, spel_id, lettertype_letterset_code, lettertype_karakter) VALUES (?, ?, ?, ?);").set(totalcounter).set(gameId).set(setcode).set(letter));
					counter++;
					totalcounter++;
				}
			}
		}catch (SQLException | InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	
	
	

	public Tile[][] compareFields(Tile[][] oldField, Tile[][] newField) {
		Tile[][] playedLetters = new Tile[15][15];
		for (int vertical = 0; vertical < 15; vertical++) {
			for (int horizontal = 0; horizontal < 15; horizontal++) {
				if(oldField[vertical][horizontal] == null && newField[vertical][horizontal] != null){
					playedLetters[vertical][horizontal] = newField[vertical][horizontal];
				} else {
					playedLetters[vertical][horizontal] = null;
				}
			}
		}
		return playedLetters;
	}

	public boolean firstTurn() {
		if (getNextTurnId() == 3) {
			return true;
		} else {
			return false;
		}
	}

	public int getNextTurnId() {
		int nextTurn = 0;
		String getLastTurnQuery = "SELECT Account_naam, ID FROM beurt WHERE Spel_ID = ? ORDER BY ID DESC LIMIT 0, 1";
		try {
			ResultSet rs = Db.run(new Query(getLastTurnQuery).set(gameId))
					.get();
			rs.first();
			nextTurn = rs.getInt(2) + 1;
		} catch (InterruptedException | ExecutionException | SQLException e) {
			e.printStackTrace();
		}

		return nextTurn;
	}

	public String getNextTurnUsername() {
		String nextuser = "";
		String getLastTurnQuery = "SELECT Account_naam, ID FROM beurt WHERE Spel_ID = ? ORDER BY ID DESC LIMIT 0, 1";
		try {
			ResultSet rs = Db.run(new Query(getLastTurnQuery).set(gameId))
					.get();
			rs.first();
			String username = rs.getString(1);

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

	public boolean thisPlayersTurn() {
		if (getNextTurnUsername().equals(currentUser.getUsername())) {
			return true;
		} else {
			return false;
		}

	}

	public Tile[][] checkValidMove(BoardModel oldBoard, BoardModel newBoard)
			throws InvalidMoveException {

		Tile[][] oldData = oldBoard.getTileData();
		Tile[][] newData = newBoard.getTileData();

		// First find out which letters where played
		Tile[][] playedLetters = compareFields(oldData, newData);
		Point[] letterPositions = MatrixUtils.getCoordinates(playedLetters);

		if (firstTurn()) {
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
						InvalidMoveException.NOT_ON_START);
			}
		}

		if (MatrixUtils.isEmpty(playedLetters)) {
			throw new InvalidMoveException(InvalidMoveException.NO_LETTERS_PUT);
		}

		if (!isAlligned(playedLetters)) {
			throw new InvalidMoveException(InvalidMoveException.NOT_ALIGNED);
		}

		/*double max = -1;
		int height = getHeight(playedLetters);
		if (height == 1) {

			// Check horizontally connected
			for (Point letterPos : letterPositions) {
				if (max != letterPos.getX() - 1 && max != -1) {
					throw new InvalidMoveException(
							InvalidMoveException.NOT_CONNECTED);
				}
				max = letterPos.getX();
			}
		} else {
			// Check vertically connected
			for (Point letterPos : letterPositions) {
				if (max != letterPos.getY() - 1 && max != -1) {
					throw new InvalidMoveException(
							InvalidMoveException.NOT_CONNECTED);
				}
				max = letterPos.getY();
			}
		}
		*/
		if(!lettersAreConnected(playedLetters, newBoard)){
			throw new InvalidMoveException(
					InvalidMoveException.NOT_CONNECTED);
		}
		
		return playedLetters;
		// Everything went better than expected.jpg :)
	}

	public boolean lettersAreConnected(Tile[][] playedLetters, BoardModel newBoard){
		Point[] letterPositions = MatrixUtils.getCoordinates(playedLetters);
		int lowY = 100;
		int maxY = 0;
		int lowX = 100;
		int maxX = 0;
		
		for(Point p :letterPositions){
			if(p.x < lowX){
				lowX =p.x;
			}
			if(p.x > maxX){
				maxX =p.x;
			}
			if(p.y < lowY){
				lowY =p.y;
			}
			if(p.y > maxY){
				maxY =p.y;
			}
		}
		
		if(maxX - lowX == 0){
			while(maxY >= lowY){
				Tile[][] data = newBoard.getTileData();
				Tile tile=data[lowX][maxY];
				if( tile== null){
					return false;
				}
				maxY--;
			}
			
		}else if(maxY - lowY ==0){
			while(maxX >= lowX){
				if(newBoard.getTileData()[maxX][lowY] == null){
					return false;
				}
				maxX--;
			}
		}else{
			return false;
		}
		return true;
	}
	
	public boolean isAlligned(Object[][] playedLetters) {
		Point[] letterPositions = MatrixUtils.getCoordinates(playedLetters);
		int holdX = (int) letterPositions[0].getX();
		int holdY = (int) letterPositions[0].getY();
		;
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

	public int getHeight(Object[][] playedLetters) {
		Point[] letterPositions = MatrixUtils.getCoordinates(playedLetters);
		int holdY = (int) letterPositions[0].getY();
		for (Point p : letterPositions) {
			if ((int) p.getY() != holdY) {
				return 0;
			}
		}
		return 1;
	}

}
