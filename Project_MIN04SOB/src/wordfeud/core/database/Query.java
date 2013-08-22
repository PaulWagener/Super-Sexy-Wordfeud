package wordfeud.core.database;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.concurrent.Callable;

import wordfeud.misc.constants.Role;
import wordfeud.misc.constants.State;
import wordfeud.misc.constants.Turn;

import com.mysql.jdbc.Statement;

public class Query implements Callable<ResultSet> {

	public static int getNumRows(ResultSet res) {
		int numRows = 0;
		try {
			res.last();
			numRows = res.getRow();
			res.beforeFirst();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return numRows;
	}

	private PreparedStatement statement;
	private int index;
	private DatabasePool pool;
	private Connection conn;
	private boolean isBatch;

	public Query(String q) throws SQLException {
		pool = DatabasePool.getInstance();
		conn = pool.checkOut();
		index = 1;
		isBatch = false;

		statement = conn.prepareStatement(q, Statement.RETURN_GENERATED_KEYS);
	}

	public Query set(Blob value) throws SQLException {
		statement.setBlob(index, value);
		index++;
		return this;
	}
	
	public Query set() throws SQLException {
		statement.setString(index, "");
		index++;
		return this;
	}

	public Query set(boolean value) throws SQLException {
		statement.setBoolean(index, value);
		index++;
		return this;
	}

	public Query set(char value) throws SQLException {
		statement.setString(index, value + ""); // Cast to string
		index++;
		return this;
	}

	public Query set(char[] value) throws SQLException {
		statement.setString(index, new String(value)); // Cast to string
		index++;
		return this;
	}

	public Query set(Date value) throws SQLException {
		statement.setDate(index, value);
		index++;
		return this;
	}

	public Query set(int value) throws SQLException {
		statement.setInt(index, value);
		index++;
		return this;
	}

	public Query set(Role role) throws SQLException {
		return set(role.toString());
	}

	public Query set(State state) throws SQLException {
		return set(state.toString());
	}

	public Query set(Turn turn) throws SQLException {
		return set(turn.toString());
	}

	public Query set(String value) throws SQLException {
		statement.setString(index, value);
		index++;
		return this;
	}

	public Query set(Time value) throws SQLException {
		statement.setTime(index, value);
		index++;
		return this;
	}

	public void addBatch() throws SQLException {
		index = 1;
		isBatch = true;
		statement.addBatch();
	}

	@Override
	public ResultSet call() throws Exception {

		if (isBatch) {

			// run the batch as a single transaction
			conn.setAutoCommit(false);
			statement.executeBatch();
			conn.setAutoCommit(true);
		} else if (statement.execute()) {
			pool.checkIn(conn);
			return statement.getResultSet();
		}

		pool.checkIn(conn);
		return statement.getGeneratedKeys();
	}
}
