package de.rincewind.sql.tables.relations;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import de.rincewind.sql.Database;
import de.rincewind.sql.DatabaseConnection;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.abstracts.AbstractTable;
import de.rincewind.sql.util.DatabaseUtils;
import de.rincewind.sql.util.SQLResult;

public class TableProjectChoosing extends AbstractTable {
	
	public static TableProjectChoosing instance() {
		return (TableProjectChoosing) Database.instance().getTable("projectchoosing");
	}
	
	public TableProjectChoosing() {
		super("projectchoosing");
	}

	@Override
	protected void executeCreateQuery(DatabaseConnection connection) {
		connection.update("CREATE TABLE IF NOT EXISTS projectchoosing (id INT AUTO_INCREMENT, projectId INT, studentId INT, chooseIndex INT, PRIMARY KEY(id))");
	}
	
	public SQLRequest<Void> add(int projectId, int studentId, int chooseIndex) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("INSERT INTO projectchoosing (projectId, studentId, chooseIndex) VALUES (?, ?, ?)");
			DatabaseUtils.setInt(stmt, 1, projectId);
			DatabaseUtils.setInt(stmt, 2, studentId);
			DatabaseUtils.setInt(stmt, 3, chooseIndex);
			connection.update(stmt);
			return null;
		};
	}
	
	public SQLRequest<Void> clearStudent(int studentId) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("DELETE FROM projectchoosing WHERE studentId = ?");
			DatabaseUtils.setInt(stmt, 1, studentId);
			connection.update(stmt);
			return null;
		};
	}
	
	public SQLRequest<List<Entry>> getEntries() {
		return () -> {
			SQLResult result = this.getDatabase().getConnection().query("SELECT * FROM projectchoosing");
			
			List<Entry> entries = new ArrayList<>();
			
			while (result.next()) {
				entries.add(new Entry(result.current("projectId"), result.current("studentId"), result.current("chooseIndex")));
			}
			
			result.close();
			return entries;
		};
	}
	
	public static class Entry {
		
		public int projectId;
		public int studentId;
		public int chooseIndex;
		
		public Entry(int projectId, int studentId, int chooseIndex) {
			this.projectId = projectId;
			this.studentId = studentId;
			this.chooseIndex = chooseIndex;
		}
		
	}
	
}
