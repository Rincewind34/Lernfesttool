package de.rincewind.sql.tables.entities;

import java.sql.PreparedStatement;

import de.rincewind.sql.Database;
import de.rincewind.sql.DatabaseConnection;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.abstracts.EntityTable;
import de.rincewind.sql.util.DatabaseUtils;

public class TableTeachers extends EntityTable {
	
	public static TableTeachers instance() {
		return (TableTeachers) Database.instance().getTable("teachers");
	}
	

	public TableTeachers() {
		super("teachers", "teacherId");
	}

	@Override
	protected void executeCreateQuery(DatabaseConnection connection) {
		connection.update("CREATE TABLE IF NOT EXISTS teachers (teacherId INT auto_increment, name VARCHAR(32), token VARCHAR(4), PRIMARY KEY(teacherId))");
	}
	
	@Override
	public SQLRequest<Integer> insertEmptyDataset() {
		return () -> {
			return this.getDatabase().getConnection().insert("INSERT INTO teachers (name, token) VALUES ('Lehrer', '???')");
		};
	}
	
	public SQLRequest<Void> update(int teacherId, String name, String token) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			
			PreparedStatement stmt = connection.prepare("UPDATE teachers SET name = ?, token = ? WHERE teacherId = ?");
			DatabaseUtils.setString(stmt, 1, name);
			DatabaseUtils.setString(stmt, 2, token);
			DatabaseUtils.setInt(stmt, 3, teacherId);
			connection.update(stmt);
			
			return null;
		};
	}
	
}
