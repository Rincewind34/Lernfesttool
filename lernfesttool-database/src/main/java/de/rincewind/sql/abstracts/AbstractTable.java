package de.rincewind.sql.abstracts;

import de.rincewind.sql.DatabaseConnection;
import de.rincewind.sql.SQLDatabase;
import de.rincewind.sql.Table;

public abstract class AbstractTable implements Table {
	
	private boolean created;
	
	private String name;
	
	private SQLDatabase database;
	
	public AbstractTable(String name) {
		this.name = name;
		this.created = false;
	}
	
	protected abstract void executeCreateQuery(DatabaseConnection connection);
	
	@Override
	public final void create() {
		try {
			this.executeCreateQuery(this.getDatabase().getConnection());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
		this.created = true;
	}
	
	@Override
	public final boolean isCreated() {
		return this.created;
	}

	@Override
	public final String getName() {
		return this.name;
	}
	
	@Override
	public final SQLDatabase getDatabase() {
		return this.database;
	}
	
	@Override
	public String toString() {
		return this.getClass().getName() + "(Name: " + this.name + ")";
	}
	
	protected final void setDatabase(SQLDatabase database) {
		if (this.database != null) {
			throw new RuntimeException("The database is in the table '" + this.name + "' already set!");
		}
		
		this.database = database;
	}

}
