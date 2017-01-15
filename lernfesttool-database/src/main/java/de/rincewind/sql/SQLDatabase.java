package de.rincewind.sql;

import java.util.concurrent.ExecutorService;

import de.rincewind.sql.abstracts.AbstractSQLDatabase;

public interface SQLDatabase {
	
	public static SQLDatabase createNewInstance(ExecutorService threadpool, DatabaseConnection connection) {
		return new AbstractSQLDatabase(connection, threadpool);
	}
	
	public abstract Table registerTable(Class<? extends Table> tableClass);
	
	public abstract Table getTable(String name);
	
	public abstract ExecutorService getThreadPool();
	
	public abstract DatabaseConnection getConnection();
	
	public abstract SQLRequest<Void> setup();
	
}
