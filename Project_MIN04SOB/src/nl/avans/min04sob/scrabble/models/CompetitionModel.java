package nl.avans.min04sob.scrabble.models;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import nl.avans.min04sob.scrabble.core.Event;
import nl.avans.min04sob.scrabble.core.db.Db;
import nl.avans.min04sob.scrabble.core.db.Query;
import nl.avans.min04sob.scrabble.core.mvc.CoreModel;
import nl.avans.min04sob.scrabble.misc.DuplicateCompetitionException;

public class CompetitionModel extends CoreModel {

	private int compId;
	private String desc;
	private AccountModel owner;
	private Date start;
	private Date end;

	private final String joinQuery = "INSERT INTO `deelnemer` (`competitie_id`, `account_naam`) VALUES (?, ?)";
	private final String removeQuery = "DELETE FROM `deelnemer` WHERE `competitie_id` =? AND `account_naam` =? ";
	private final String chatsToRemove = "SELECT `id` FROM `spel` WHERE (`Account_naam_uitdager` = ? OR `Account_naam_tegenstander` = ?) AND `competitie_id` = ?";
	private final String removeChats = "DELETE FROM `chatregel` WHERE `spel_id` = ?";
	private final String removeScores = "DELETE FROM `beurt` WHERE `spel_id` = ?";
	private final String removeGames = "DELETE FROM `spel` WHERE (`Account_naam_uitdager` = ? OR `Account_naam_tegenstander` = ?) AND `competitie_id` = ?";
	private final String createQuery = "INSERT INTO `competitie` (`account_naam_eigenaar`, `start`, `einde`, `omschrijving`) VALUES (?,?,?,?)";
	private final String getCreatedCompID = "SELECT `id` FROM `competitie` WHERE `account_naam_eigenaar` = ? ORDER BY `einde` DESC";
	private final String initQuery = "SELECT * FROM `competitie` WHERE id = ?";
	private final String ratingQuery = "SELECT * FROM `comp_ranking` WHERE `competitie_id` = ? ORDER BY bayesian_rating DESC";

	@Deprecated
	public CompetitionModel() {

	}

	public CompetitionModel(int compId) {
		try {
			Future<ResultSet> worker = Db.run(new Query(initQuery).set(compId));
			ResultSet res = worker.get();
			if (Query.getNumRows(res) == 1) {
				res.next();
				this.compId = res.getInt("id");
				owner = new AccountModel(res.getString("account_naam_eigenaar"));

				start = res.getDate("start");
				end = res.getDate("einde");
				desc = res.getString("omschrijving");
			} else {
				throw new IllegalArgumentException("Invalid competition ID");
			}
		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	public void createCompetition(AccountModel user, String desc)
			throws DuplicateCompetitionException {

		if (user.getOwnedCompetitions().length == 0) {
			int compId = 0;
			try {
				Calendar cal = Calendar.getInstance();
				DateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date today = new Date(cal.getTimeInMillis());
				String currentDate = dateFormat.format(today);
				cal.add(Calendar.MONTH, 1);
				Date nextMonth = new Date(cal.getTimeInMillis());
				String endDate = dateFormat.format(nextMonth);

				Db.run(new Query(createQuery).set(user.getUsername())
						.set(currentDate).set(endDate).set(desc));

				Future<ResultSet> worker = Db.run(new Query(getCreatedCompID)
						.set(user.getUsername()));
				ResultSet dbResult = worker.get();
				if (dbResult.next()) {
					compId = dbResult.getInt("id");
					join(compId, user.getUsername());
					firePropertyChange(Event.NEWCOMPETITION, null, this);
				}

			} catch (SQLException | InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		} else {
			throw new DuplicateCompetitionException();
		}
	}

	// geef alle competities ooit aangemaakt
	public CompetitionModel[] getAllCompetitions() {
		CompetitionModel[] allComps = new CompetitionModel[0];
		int x = 0;
		try {
			Future<ResultSet> worker = Db.run(new Query(
					"SELECT DISTINCT(`competitie_id`) FROM `deelnemer`"));
			ResultSet dbResult = worker.get();
			allComps = new CompetitionModel[Query.getNumRows(dbResult)];
			while (dbResult.next() && x < allComps.length) {
				allComps[x] = new CompetitionModel(
						dbResult.getInt("competitie_id"));
				x++;
			}
		} catch (SQLException | InterruptedException | ExecutionException sql) {
			sql.printStackTrace();
		}
		return allComps;

	}

	// geeft alle openstaande competities
	public CompetitionModel[] getAllOpenCompetitions() {
		CompetitionModel[] allComps = new CompetitionModel[0];
		int x = 0;
		try {
			Future<ResultSet> worker = Db
					.run(new Query(
							"SELECT DISTINCT(`ID`) FROM `competitie` WHERE `einde` > now();"));
			ResultSet dbResult = worker.get();
			allComps = new CompetitionModel[Query.getNumRows(dbResult)];
			while (dbResult.next() && x < allComps.length) {
				allComps[x] = new CompetitionModel(
						dbResult.getInt("competitie_id"));
				x++;
			}
		} catch (SQLException | InterruptedException | ExecutionException sql) {
			sql.printStackTrace();
		}
		return allComps;
	}

	public int getCompetitionID(String desc) {
		int id = 0;
		try {
			Future<ResultSet> worker = Db.run(new Query(
					"SELECT `id` FROM `competitie` WHERE `omschrijving` = ?")
					.set(desc));
			ResultSet dbResult = worker.get();
			while (dbResult.next()) {
				id = dbResult.getInt("id");
			}
		} catch (SQLException | InterruptedException | ExecutionException sql) {
			sql.printStackTrace();
		}
		return id;

	}

	public int getCompId() {
		return compId;
	}

	public String getDesc() {
		return desc;
	}

	public Date getEndData() {
		return end;
	}

	public AccountModel getOwner() {
		return owner;
	}

	public Date getStartDate() {
		return start;
	}

	public AccountModel[] getUsersFromCompetition(int competition_id,
			String username) {
		AccountModel[] accounts = new AccountModel[0];
		int x = 0;
		try {
			Future<ResultSet> worker = Db
					.run(new Query(
							"SELECT `account_naam` FROM `deelnemer` WHERE `competitie_id` = ? AND `account_naam` NOT LIKE ?")
							.set(competition_id).set(username));
			ResultSet dbResult = worker.get();
			accounts = new AccountModel[Query.getNumRows(dbResult)];
			while (dbResult.next() && x < accounts.length) {
				accounts[x] = new AccountModel(
						dbResult.getString("account_naam"));
				x++;
			}
		} catch (SQLException | InterruptedException | ExecutionException sql) {
			sql.printStackTrace();
		}
		return accounts;
	}

	// Eigenlijk eigenschap van account, niet van speler
	// Cringe... WTF. Groetjes
	public void join(int competitionID, String username) {
		try {
			Db.run(new Query(joinQuery).set(competitionID).set(username));

		} catch (SQLException sql) {
			sql.printStackTrace();
		}
	}

	@Deprecated
	public void remove(int competitionID, String username) {
		ArrayList<Integer> spel_ids = new ArrayList<Integer>();
		try {
			Future<ResultSet> worker = Db.run(new Query(chatsToRemove)
					.set(username).set(username).set(competitionID));
			ResultSet dbResult = worker.get();
			while (dbResult.next()) {
				spel_ids.add(dbResult.getInt("spel_id"));
				for (Integer id : spel_ids) {
					Db.run(new Query(removeChats).set(id));
					Db.run(new Query(removeScores).set(id));
				}
			}
			Db.run(new Query(removeGames).set(username).set(username)
					.set(competitionID));
			Db.run(new Query(removeQuery).set(competitionID).set(username));
		} catch (SQLException | InterruptedException | ExecutionException sql) {
			sql.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return desc + " (" + owner + ")";
	}

	@Override
	public void update() {

	}

	public ArrayList<Object[]> getRanking() {
		Object[] row;
		ArrayList<Object[]> data = new ArrayList<>();
		try {
			Future<ResultSet> worker = Db.run(new Query(ratingQuery)
					.set(compId));

			ResultSet rs = worker.get();

			while (rs.next()) {
				row = new Object[7];
				row[0] = rs.getString("account_naam");
				row[1] = rs.getString("this_num_games");
				row[2] = rs.getString("totalscore");
				row[3] = rs.getString("avgscore");
				row[4] = rs.getString("wins");
				row[5] = rs.getString("los");
				row[6] = rs.getString("bayesian_rating");
				data.add(row);
			}

		} catch (SQLException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return data;
	}

	public AccountModel[] getChallengeAblePlayers(AccountModel currentUser) {
		AccountModel[] accounts = new AccountModel[0];
		String username = currentUser.getUsername();
		int x = 0;
		try {
			Future<ResultSet> worker = Db
					.run(new Query(
							"SELECT `account_naam` FROM `deelnemer` WHERE `competitie_id` = ? AND `account_naam` <> ? AND `account_naam` NOT IN (SELECT `account_naam_tegenstander` FROM `spel` WHERE `account_naam_uitdager` = ? AND `competitie_id` = ? AND `toestand_type` <> ?)")
							.set(compId).set(username).set(username)
							.set(compId).set(ChallengeModel.FINISHED));
			ResultSet res = worker.get();
			accounts = new AccountModel[Query.getNumRows(res)];
			while (res.next() && x < accounts.length) {
				accounts[x] = new AccountModel(res.getString("account_naam"));
				x++;
			}
		} catch (SQLException | InterruptedException | ExecutionException sql) {
			sql.printStackTrace();
		}
		return accounts;
	}
}
