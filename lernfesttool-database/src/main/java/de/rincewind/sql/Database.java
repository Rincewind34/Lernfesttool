package de.rincewind.sql;

import java.util.concurrent.ExecutorService;

import de.rincewind.sql.connections.MySQLConnection;

public class Database {
	
	private static SQLDatabase instance;
	private static ExecutorService threadpool;
	
	public static SQLDatabase instance() {
		return Database.instance;
	}
	
	public static void initialize(ExecutorService threadpool) {
		Database.threadpool = threadpool;
		Database.instance = SQLDatabase.createNewInstance(threadpool, new MySQLConnection("192.168.1.30", 3306, "lernfest_beta", "torben", "torben"));
	}
	
	protected static ExecutorService threadpool() {
		return Database.threadpool;
	}
	
}
