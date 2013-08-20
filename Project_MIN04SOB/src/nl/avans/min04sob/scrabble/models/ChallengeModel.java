package nl.avans.min04sob.scrabble.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import nl.avans.min04sob.scrabble.core.db.Db;
import nl.avans.min04sob.scrabble.core.db.Query;
import nl.avans.min04sob.scrabble.core.mvc.CoreModel;

/**
 * Describes one challenge, all accepted challenges are considered a game and
 * not a challenge any more.
 * 
 * @author patrick
 */
public class ChallengeModel extends CoreModel {

	private int gameId;
	private AccountModel challenger;
	private CompetitionModel comp;
	private String state;
	private String response;

	private static final String ACCEPTED = "Accepted";
	private static final String REJECTED = "Rejected";
	public static final String OPEN = "Unknown";
	public static final String REQUEST = "Request";
	private static final String PLAYING = "Playing";
	public static final String FINISHED = "Finished";
	private static final String BOARD = "standard";
	private static final String LANGSET = "NL";

	private final String respondQuery = "UPDATE `Spel` SET `Toestand_type`= ?, `Reaktie_type`= ?, `moment_reaktie`= NOW() WHERE `ID` = ?";

	public ChallengeModel(int gameId) {
		String q = "SELECT `Competitie_ID`, `Toestand_type`, `Account_naam_uitdager`, `Reaktie_type` FROM `spel` WHERE `id` = ?";
		try {
			Future<ResultSet> worker = Db.run(new Query(q).set(gameId));

			ResultSet res = worker.get();
			if (res.first()) {
				this.gameId = gameId;
				comp = new CompetitionModel(res.getInt("Competitie_ID"));
				state = res.getString("Toestand_type");
				challenger = new AccountModel(
						res.getString("Account_naam_uitdager"));
				response = res.getString("Reaktie_type");

				if (!state.equals(REQUEST)) {
					throw new IllegalStateException("gameId \"" + gameId
							+ "\" is not a challenge,"
							+ "it already has been accepted or denied");
				}
			} else {
				throw new IllegalArgumentException(
						"Invalid gameId in challengeModel");
			}
		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		return challenger.getUsername() + " in " + comp.toString();
	}

	@Override
	public void update() {
		// Do nothing
	}

	public void accept() {
		respond(ACCEPTED);
	}

	public void decline() {
		respond(REJECTED);
	}

	private void respond(String anwser) {
		Query accept;
		try {
			if (anwser.equals(ACCEPTED)) {
				accept = new Query(respondQuery).set(PLAYING).set(ACCEPTED)
						.set(gameId);
			} else {
				accept = new Query(respondQuery).set(REQUEST).set(REJECTED)
						.set(gameId);
			}

			if (response.equals(OPEN)) {
				Db.run(accept);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a challenge with the default board and default language/letter set
	 * 
	 * @param challenger
	 * @param opponent
	 * @param comp
	 */
	public static void create(AccountModel challenger, AccountModel opponent,
			CompetitionModel comp) {
		create(challenger, opponent, comp, BOARD, LANGSET);
	}

	/**
	 * Create a challenge
	 * 
	 * @param challenger
	 * @param opponent
	 * @param comp
	 * @param board
	 * @param letterSet
	 */
	public static void create(AccountModel challenger, AccountModel opponent,
			CompetitionModel comp, String board, String letterSet) {

		int compId = comp.getCompId();
		String challengerName = challenger.getUsername();
		String opponentName = opponent.getUsername();
		int gameId;
		
		try {
			gameId = createGame(compId, challengerName, opponentName, board, letterSet);
			createLetters(gameId, letterSet);
			createTurns(gameId, challengerName, opponentName);
			
		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Run the query to create the game/challenge
	 * 
	 * @param compId
	 * @param challengerName
	 * @param opponentName
	 * @return int the generated gameId for the created challenge.
	 * @throws SQLException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	private static int createGame(int compId, String challengerName,
			String opponentName, String board, String letterSet)
			throws SQLException, InterruptedException, ExecutionException {

		String create = "INSERT INTO `Spel` (`Competitie_ID`,`Toestand_type`,`Account_naam_uitdager`,`Account_naam_tegenstander`,`moment_uitdaging`,`Reaktie_type`,`Bord_naam`,`LetterSet_naam`) VALUES (?,?,?,?,NOW() ,?,?,?)";
		// Create Query
		Future<ResultSet> worker1 =  Db.run(new Query(create).set(compId).set(REQUEST).set(challengerName)
				.set(opponentName).set(OPEN).set(board).set(letterSet));
		ResultSet rs = worker1.get();
		rs.next();
		int lastCreatedId = rs.getInt(1);
		return lastCreatedId;
	}

	/**
	 * Create the letters in the database for the game
	 * 
	 * @param gameId
	 * @throws SQLException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private static void createLetters(int gameId, String letterSet)
			throws SQLException, InterruptedException, ExecutionException {
		String insertLetters = "INSERT INTO `letter` (`ID`,`Spel_ID`,`LetterType_LetterSet_Code`,`LetterType_Karakter`) VALUES (?,?,?,?)";
		String letterAmount = "SELECT `karakter`, `aantal` FROM `lettertype` WHERE `LetterSet_code` = ?";

		Future<ResultSet> worker = Db.run(new Query(letterAmount)
				.set(letterSet));

		ResultSet res = worker.get();
		int letterId = 0;

		Query query = new Query(insertLetters);
		while (res.next()) {
			String character = res.getString("karakter");
			int amount = res.getInt("aantal");

			for (int i = 0; i < amount; i++) {
				query.set(letterId).set(gameId).set(letterSet).set(character);
				query.addBatch();
				letterId++;
			}
		}

		Db.run(query);
	}

	private static void createTurns(int gameId, String challenger,
			String opponent) throws SQLException {
		String addTurn = "INSERT INTO beurt(ID, spel_id, account_naam, score, aktie_type) VALUES(?, ?, ?, ?, ?)";
		Query query = new Query(addTurn);

		query.set(1).set(gameId).set(challenger).set(0).set("Begin");
		query.addBatch();
		query.set(2).set(gameId).set(opponent).set(0).set("Begin");
		query.addBatch();

		Db.run(query);
	}
}
