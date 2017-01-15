package de.rincewind.sql.abstracts;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import de.rincewind.sql.DatabaseConnection;
import de.rincewind.sql.SQLDatabase;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.Table;

public class AbstractSQLDatabase implements SQLDatabase {

	private ExecutorService threadpool;
	
	private List<Table> tables;
	
	private DatabaseConnection connection;
	
	public AbstractSQLDatabase(DatabaseConnection connection, ExecutorService threadpool) {
		this.connection = connection;
		this.threadpool = threadpool;
		this.tables = new ArrayList<>();
	}
	
	@Override
	public Table registerTable(Class<? extends Table> tableClass) {
		Table table;
		
		try {
			table = tableClass.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		
		if (this.getTable(table.getName()) != null) {
			throw new RuntimeException("The table does already exist!");
		}
		
		((AbstractTable) table).setDatabase(this);
		this.tables.add(table);
		return table;
	}

	@Override
	public Table getTable(String name) {
		for (Table table : this.tables) {
			if (table.getName().equals(name)) {
				return table;
			}
		}
		
		return null;
	}

	@Override
	public DatabaseConnection getConnection() {
		return this.connection;
	}
	
	@Override
	public ExecutorService getThreadPool() {
		return this.threadpool;
	}
	
	@Override
	public SQLRequest<Void> setup() {
		if (!this.connection.isOpen()) {
			throw new RuntimeException("The connection has to be established!");
		}
		
		return () -> {
			for (Table table : this.tables) {
				table.create();
			}
			
			return null;
		};
	}

}
