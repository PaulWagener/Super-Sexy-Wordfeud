package nl.avans.min04sob.scrabble.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import nl.avans.min04sob.scrabble.controllers.ChallengeModel;
import nl.avans.min04sob.scrabble.core.Event;
import nl.avans.min04sob.scrabble.core.db.Db;
import nl.avans.min04sob.scrabble.core.db.Query;
import nl.avans.min04sob.scrabble.core.mvc.CoreModel;
import nl.avans.min04sob.scrabble.misc.Role;
import nl.avans.min04sob.scrabble.misc.State;
import nl.avans.min04sob.scrabble.views.BoardPanel;

public class AccountModel extends CoreModel {

	public static boolean checkUsernameAvailable(String username) {
		String query = "SELECT * FROM account WHERE naam = ?";
		try {
			Future<ResultSet> worker = Db.run(new Query(query).set(username));
			ResultSet rs = worker.get();
			return !rs.first(); // If a first row exists, return true.
		} catch (SQLException | InterruptedException | ExecutionException sql) {
			return false;
		}
	}

	public static void registerAccount(String username, char[] password,
			Role[] roles) {

		String createAccount = "INSERT INTO `account` (`naam`, `wachtwoord` ) VALUES (?, ?)";
		String setRole = "INSERT INTO `accountrol` (`account_naam`, `rol_type`) VALUES (?, ?)";
		try {

			Db.run(new Query(createAccount).set(username).set(password));
			for (Role role : roles) {
				Db.run(new Query(setRole).set(username).set(role));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private String username;

	private boolean isLoggedIn;

	private final String availableCompetitionQuery = "SELECT `Competitie_ID` FROM `deelnemer` WHERE `Competitie_ID` NOT IN (SELECT `Competitie_ID` FROM `deelnemer` WHERE `Account_naam` = ?) GROUP BY `Competitie_ID";

	private int gameCount;

	private int challengeCount;

	public AccountModel() {
		initialize();
	}

	public AccountModel(String username) {
		initialize();

		this.username = username;
	}

	public AccountModel(String username, char[] password) {
		initialize();
		login(username, password);
	}

	public void changePass(String newPass) {
		String query = "UPDATE account SET wachtwoord =? WHERE naam=?;";
		try {
			Db.run(new Query(query).set(newPass).set(username));
		} catch (SQLException sql) {
			sql.printStackTrace();
		}
	}

	public void changeAnotherPlayerPass(String newPass, String selectedplayer) {
		String query = "UPDATE account SET wachtwoord =? WHERE naam=?;";
		String[] splitStr = selectedplayer.split("\\s+");
		System.out.println(splitStr[0]);
		try {
			Db.run(new Query(query).set(newPass).set(splitStr[0]));
		} catch (SQLException sql) {
			sql.printStackTrace();
		}
	}

	public CompetitionModel[] getAvailableCompetitions(String username) {
		CompetitionModel[] comp_desc = new CompetitionModel[0];
		int x = 0;
		try {
			// deze query laat alleen de beschikbare competities zien die al
			// minimaal 1 deelnemer heeft
			Future<ResultSet> worker = Db.run(new Query(
					availableCompetitionQuery).set(username));
			ResultSet rs = worker.get();
			comp_desc = new CompetitionModel[Query.getNumRows(rs)];
			while (rs.next() && x < comp_desc.length) {
				comp_desc[x] = new CompetitionModel(rs.getInt("competitie_id"));
				x++;
			}
		} catch (SQLException | InterruptedException | ExecutionException sql) {
			sql.printStackTrace();
		}
		return comp_desc;
	}

	public CompetitionModel[] getCompetitions() {
		CompetitionModel[] compDesc = new CompetitionModel[0];
		int x = 0;
		try {
			Future<ResultSet> worker = Db
					.run(new Query(
							"SELECT `competitie_id` FROM `deelnemer` WHERE `account_naam` = ?")
							.set(username));
			ResultSet dbResult = worker.get();
			compDesc = new CompetitionModel[Query.getNumRows(dbResult)];
			while (dbResult.next() && x < compDesc.length) {
				compDesc[x] = new CompetitionModel(
						dbResult.getInt("competitie_id"));
				x++;
			}
		} catch (SQLException | InterruptedException | ExecutionException sql) {
			sql.printStackTrace();
		}
		return compDesc;
	}

	public String[] getPlayers() {
		String[] player = new String[0];
		int x = 0;
		try {
			Future<ResultSet> worker = Db.run(new Query(
					"SELECT * FROM `Account`;"));
			ResultSet dbResult = worker.get();
			player = new String[Query.getNumRows(dbResult)];
			while (dbResult.next() && x < player.length) {
				player[x] = dbResult.getString(1) + "  -   "
						+ dbResult.getString(2);
				x++;
			}
		} catch (SQLException | InterruptedException | ExecutionException sql) {
			sql.printStackTrace();
		}
		return player;
	}

	public ArrayList<GameModel> getObserverAbleGames() {
		ArrayList<GameModel> games = new ArrayList<GameModel>();
		String query = "SELECT DISTINCT `spel_id` FROM `beurt` JOIN `spel` ON `beurt`.`spel_id` = `spel`.`id` WHERE NOT `spel`.`toestand_type` = ?";
		try {
			Future<ResultSet> worker = Db.run(new Query(query)
					.set(State.REQUEST));
			ResultSet dbResult = worker.get();
			while (dbResult.next()) {
				games.add(new GameModel(dbResult.getInt(1), this,
						new BoardModel(), new BoardPanel(), true));
				// Add a new game with the gameId for this account
			}

		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return games;
	}

	public ArrayList<GameModel> getOpenGames() {
		ArrayList<GameModel> games = new ArrayList<GameModel>();
		String query = "SELECT `ID` FROM `spel` WHERE ( `Account_naam_uitdager` = ? OR `Account_naam_tegenstander` = ?) AND `Toestand_type` = ?";
		try {
			Future<ResultSet> worker = Db.run(new Query(query).set(username)
					.set(username).set(State.PLAY));
			ResultSet dbResult = worker.get();
			while (dbResult.next()) {
				games.add(new GameModel(dbResult.getInt(1), this,
						new BoardModel(), new BoardPanel(), false));
				// Add a new game with the gameId for this account
			}

		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return games;
	}

	public String getpass() {
		String query = "SELECT wachtwoord FROM account WHERE naam=?";
		String pass = "";
		try {
			Future<ResultSet> worker = Db.run(new Query(query).set(username));
			ResultSet check = worker.get();
			check.next();
			pass = check.getString(1);
		} catch (SQLException | InterruptedException | ExecutionException sql) {
			sql.printStackTrace();
		}
		return pass;
	}

	public String getUsername() {
		return username;
	}

	public void initialize() {
		username = "Onbekend";
		isLoggedIn = false;
		gameCount = 0;
	}

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	public boolean isRole(Role role) {
		String query = "SELECT `Rol_type` FROM `accountrol` WHERE `Account_naam` = ?";
		try {
			Future<ResultSet> worker = Db.run(new Query(query).set(username));

			// Do something else
			ResultSet rs = worker.get();
			while (rs.next()) {
				if (rs.getString(1).equalsIgnoreCase(role.toString())) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return false;
	}

	public void login(String user, char[] password) {
		try {
			String query = "SELECT `naam` FROM `account` WHERE `naam` = ? AND `wachtwoord` = ?";
			Future<ResultSet> worker = Db.run(new Query(query).set(user).set(
					password));

			ResultSet result = worker.get();

			if (Query.getNumRows(result) == 1) {
				result.next();
				username = result.getString(1);
				isLoggedIn = true;

				firePropertyChange(Event.LOGIN, null, this);
			} else {
				firePropertyChange(Event.LOGINFAIL, null, this);
			}
		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	public void logout() {
		isLoggedIn = false;
		firePropertyChange(Event.LOGOUT, null, this);
	}

	@Override
	public String toString() {
		return username;
	}

	@Override
	public void update() {
		// Check new games
		
		if(getOpenGames().size() != gameCount){
			gameCount = getOpenGames().size();
			firePropertyChange(Event.NEWGAME, null, getOpenGames());
		}

		// Check new challenges
		int newChallengeCount = getChallenges().length;

		firePropertyChange(Event.NEWCHALLENGE, challengeCount,
				newChallengeCount);
		challengeCount = newChallengeCount;
	}

	public CompetitionModel[] getOwnedCompetitions() {
		ArrayList<CompetitionModel> competitions = null;
		try {
			String query = "SELECT `id` FROM  `competitie` 	WHERE  `Account_naam_eigenaar` =  ? AND `einde` > NOW()";
			Future<ResultSet> worker = Db.run(new Query(query).set(username));
			ResultSet result = worker.get();
			competitions = new ArrayList<CompetitionModel>();
			while (result.next()) {
				competitions.add(new CompetitionModel(result.getInt(1)));
			}
		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return competitions.toArray(new CompetitionModel[competitions.size()]);
	}

	public ChallengeModel[] getChallenges() {
		ChallengeModel[] challenges = new ChallengeModel[0];
		int i = 0;
		try {
			Future<ResultSet> worker = Db
					.run(new Query(
							"SELECT `ID` FROM `Spel` WHERE `account_naam_tegenstander` = ? AND `toestand_type` = ? AND `reaktie_type` = ?")
							.set(username).set(ChallengeModel.REQUEST)
							.set(ChallengeModel.OPEN));
			ResultSet res = worker.get();
			challenges = new ChallengeModel[Query.getNumRows(res)];
			while (res.next()) {
				challenges[i] = new ChallengeModel(res.getInt("ID"));
				i++;
			}
		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return challenges;
	}

}
