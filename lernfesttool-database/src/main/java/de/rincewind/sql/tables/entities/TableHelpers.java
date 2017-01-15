package de.rincewind.sql.tables.entities;

import java.sql.PreparedStatement;

import de.rincewind.sql.Database;
import de.rincewind.sql.DatabaseConnection;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.abstracts.EntityTable;
import de.rincewind.sql.util.DatabaseUtils;

public class TableHelpers extends EntityTable {
	
	public static TableHelpers instance() {
		return (TableHelpers) Database.instance().getTable("helpers");
	}
	
	
	public TableHelpers() {
		super("helpers", "helperId");
	}

	@Override
	protected void executeCreateQuery(DatabaseConnection connection) {
		connection.update("CREATE TABLE IF NOT EXISTS helpers (helperId INT auto_increment, name VARCHAR(32), studentId INT, PRIMARY KEY(helperId))");
	}
	
	@Override
	public SQLRequest<Integer> insertEmptyDataset() {
		return () -> {
			return this.getDatabase().getConnection().insert("INSERT INTO helpers (name, studentId) VALUES ('Helfer', -1)");
		};
	}
	
	public SQLRequest<Void> update(int helperId, String name, int studentId) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			
			PreparedStatement stmt = connection.prepare("UPDATE helpers SET name = ?, studentId = ? WHERE helperId = ?");
			DatabaseUtils.setString(stmt, 1, name);
			DatabaseUtils.setInt(stmt, 2, studentId);
			DatabaseUtils.setInt(stmt, 2, helperId);
			connection.update(stmt);
			
			return null;
		};
	}

}
