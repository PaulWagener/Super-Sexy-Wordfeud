package wordfeud.models;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import wordfeud.controllers.ChallengeModel;
import wordfeud.core.Event;
import wordfeud.core.database.Db;
import wordfeud.core.database.Query;
import wordfeud.core.mvc.CoreModel;
import wordfeud.misc.DuplicateCompetitionException;

public class CompetitionModel extends CoreModel {

	private int compId;
	private String desc;
	private AccountModel owner;
	private Date start;
	private Date end;

	private final String joinQuery = "INSERT INTO `deelnemer` (`competitie_id`, `account_naam`) VALUES (?, ?)";
	private final String createQuery = "INSERT INTO `competitie` (`account_naam_eigenaar`, `start`, `einde`, `omschrijving`) VALUES (?,?,?,?)";
	private final String getCreatedCompID = "SELECT `id` FROM `competitie` WHERE `account_naam_eigenaar` = ? ORDER BY `einde` DESC";
	private final String initQuery = "SELECT * FROM `competitie` WHERE id = ?";
	private final String ratingQuery = "SELECT * FROM `comp_ranking` WHERE `competitie_id` = ? ORDER BY bayesian_rating DESC";

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
					CompetitionModel comp = new CompetitionModel(dbResult.getInt("id"));
					comp.addPlayer(user.getUsername());
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
	public static CompetitionModel[] getAllCompetitions() {
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

	public AccountModel[] getUsersFromCompetition(String username) {
		AccountModel[] accounts = new AccountModel[0];
		int x = 0;
		try {
			Future<ResultSet> worker = Db
					.run(new Query(
							"SELECT `account_naam` FROM `deelnemer` WHERE `competitie_id` = ? AND `account_naam` NOT LIKE ?")
							.set(compId).set(username));
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

	public void addPlayer(String username) {
		try {
			Db.run(new Query(joinQuery).set(compId).set(username));

		} catch (SQLException sql) {
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
