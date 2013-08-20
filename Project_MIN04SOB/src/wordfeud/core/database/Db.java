package wordfeud.core.database;

import java.sql.ResultSet;
import java.util.concurrent.Future;

import wordfeud.misc.MainClass;

public class Db {

	public static Future<ResultSet> run(Query q) {
		return MainClass.executor.submit(q);
	}
}
