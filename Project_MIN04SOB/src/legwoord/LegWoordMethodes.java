package legwoord;

import java.awt.Dimension;
import java.awt.Point;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	
	public LegWoordMethodes(int gameId, AccountModel user, BoardModel boardModel,
			BoardPanel boardPanel, boolean observer) {
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
	
	
	
	
	
	
	
	
	public Object[][] checkValidMove(BoardModel oldBoard, BoardModel newBoard)
			throws InvalidMoveException {

		
		Object[][] oldData = oldBoard.getData();
		System.out.println("OLD " + Arrays.deepToString(oldData));
		Object[][] newData = newBoard.getData();
		System.out.println("NEW " +
				"" + Arrays.deepToString(newData));

		// First find out which letters where played
		Object[][] playedLetters =  compareFields(oldData, newData);
		System.out.println(Arrays.deepToString(playedLetters));
		Point[] letterPositions = MatrixUtils.getCoordinates(playedLetters);

		if (firstTurn()) {
			boolean onStar = false;
			Point starCoord = oldBoard.getStartPoint();
			System.out.println("LetterCount: " + letterPositions.length);
			System.out.println("Star at: X" + starCoord.x + " Y" + starCoord.y);

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

		if (!MatrixUtils.isEmpty(playedLetters)) {
			throw new InvalidMoveException(InvalidMoveException.NO_LETTERS_PUT);
		}

		playedLetters = MatrixUtils.crop(playedLetters);

		Dimension playedWordSize = MatrixUtils.getDimension(playedLetters);
		if (!MatrixUtils.isAligned(playedWordSize)) {
			throw new InvalidMoveException(InvalidMoveException.NOT_ALIGNED);
		}

		double max = -1;
		if (playedWordSize.getHeight() == 1) {

			// Check horizontally connected
			for (Point letterPos : letterPositions) {
				if (max != letterPos.getY() - 1 && max != -1) {
					throw new InvalidMoveException(
							InvalidMoveException.NOT_CONNECTED);
				}
				max = letterPos.getY();
			}
		} else {
			// Check vertically connected
			for (Point letterPos : letterPositions) {
				if (max != letterPos.getX() - 1 && max != -1) {
					throw new InvalidMoveException(
							InvalidMoveException.NOT_CONNECTED);
				}
				max = letterPos.getX();
			}
		}
		
		return playedLetters;
		// Everything went better than expected.jpg :)
	}

	
	
	
	
	

	
	////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	////////////////////												\\\\\\\\\\\\\\\\\\\\
	////////////////////												\\\\\\\\\\\\\\\\\\\\
	////////////////////												\\\\\\\\\\\\\\\\\\\\
	////////////////////												\\\\\\\\\\\\\\\\\\\\
	////////////////////	de code onder dit blok moet getest worden	\\\\\\\\\\\\\\\\\\\\
	////////////////////												\\\\\\\\\\\\\\\\\\\\
	////////////////////												\\\\\\\\\\\\\\\\\\\\
	////////////////////												\\\\\\\\\\\\\\\\\\\\
	////////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	

	
	
	

	
	////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	////////////////////										\\\\\\\\\\\\\\\\\\\\
	////////////////////										\\\\\\\\\\\\\\\\\\\\
	////////////////////										\\\\\\\\\\\\\\\\\\\\
	////////////////////										\\\\\\\\\\\\\\\\\\\\
	////////////////////	de code onder dit blok werkt		\\\\\\\\\\\\\\\\\\\\
	////////////////////										\\\\\\\\\\\\\\\\\\\\
	////////////////////										\\\\\\\\\\\\\\\\\\\\
	////////////////////										\\\\\\\\\\\\\\\\\\\\
	////////////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
	
	
	public Object[][] compareFields(Object[][] oldField, Object[][] newField){
		Object[][] playedLetters = new Object[15][15];
		for(int vertical = 0; vertical < 15; vertical++){
			for(int horizontal = 0; horizontal < 15; horizontal++){
				if(newField[vertical][horizontal] == null){
					playedLetters[vertical][horizontal] = null;
				}else if(newField[vertical][horizontal].equals(oldField[vertical][horizontal])){
					playedLetters[vertical][horizontal] = null;
				}else{
					playedLetters[vertical][horizontal] = newField[vertical][horizontal];
				}
			}
		}
		return playedLetters;
	}

	public boolean firstTurn(){
		if(getNextTurnId() == 3){
			return true;
		}else{
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
	
	public boolean thisPlayersTurn(){
		if(getNextTurnUsername().equals(currentUser.getUsername())){
			return true;
		}else {
			return false;
		}
		
	}
	
}
