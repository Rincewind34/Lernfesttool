package de.rincewind.sql;

import java.util.concurrent.ExecutorService;

import de.rincewind.sql.connections.MySQLConnection;

public class Database {
	
	private static SQLDatabase instance;
	private static ExecutorService threadpool;
	
	public static SQLDatabase instance() {
		return Database.instance;
	}
	
	public static void initialize(ExecutorService threadpool, String host, int port, String database, String username, String password) {
		Database.threadpool = threadpool;
		Database.instance = SQLDatabase.createNewInstance(threadpool, new MySQLConnection(host, port, database, username, password));
	}
	
	protected static ExecutorService threadpool() {
		return Database.threadpool;
	}
	
}
