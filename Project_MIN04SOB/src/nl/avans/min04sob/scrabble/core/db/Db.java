package nl.avans.min04sob.scrabble.core.db;

import java.sql.ResultSet;
import java.util.concurrent.Future;

import nl.avans.min04sob.scrabble.misc.MainClass;


public class Db {

	public static Future<ResultSet> run(Query q){
		return MainClass.executor.submit(q);
	}
}
