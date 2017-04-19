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

public class TableProjectAttandences extends AbstractTable {
	
	public static TableProjectAttandences instance() {
		return (TableProjectAttandences) Database.instance().getTable("projectattandences");
	}
	
	public TableProjectAttandences() {
		super("projectattandences");
	}
	
	@Override
	protected void executeCreateQuery(DatabaseConnection connection) {
		connection.update("CREATE TABLE IF NOT EXISTS projectattandences (id INT AUTO_INCREMENT, projectId INT, studentId INT, leads BOOLEAN, PRIMARY KEY(id))");
	}

	public SQLRequest<Void> add(int projectId, int studentId, boolean leading) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("INSERT INTO projectattandences (projectId, studentId, leads) VALUES (?, ?, ?)");
			DatabaseUtils.setInt(stmt, 1, projectId);
			DatabaseUtils.setInt(stmt, 2, studentId);
			DatabaseUtils.setBoolean(stmt, 3, leading);
			connection.update(stmt);
			return null;
		};
	}

	public SQLRequest<Void> addAll(List<Entry> entries) {
		return () -> {
			String query = "INSERT INTO projectattandences (projectId, studentId, leads) VALUES ";
			
			for (Entry entry : entries) {
				query = query + "(" + entry.projectId + ", " + entry.studentId + ", " + entry.leading + "), ";
			}
			
			this.getDatabase().getConnection().update(query.substring(0, query.length() - 2));
			return null;
		};
	}
	
	public SQLRequest<Void> clearProject(int projectId, boolean leading) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("DELETE FROM projectattandences WHERE projectId = ? AND leads = ?");
			DatabaseUtils.setInt(stmt, 1, projectId);
			DatabaseUtils.setBoolean(stmt, 2, leading);
			connection.update(stmt);
			return null;
		};
	}
	
	public SQLRequest<Void> clearStudent(int studentId, boolean leading) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("DELETE FROM projectattandences WHERE studentId = ? AND leads = ?");
			DatabaseUtils.setInt(stmt, 1, studentId);
			DatabaseUtils.setBoolean(stmt, 2, leading);
			connection.update(stmt);
			return null;
		};
	}
	
	public SQLRequest<Void> clearAll(boolean leading) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("DELETE FROM projectattandences WHERE leads = ?");
			DatabaseUtils.setBoolean(stmt, 1, leading);
			connection.update(stmt);
			return null;
		};
	}
	
	public SQLRequest<List<Integer>> getProjects(int studentId, boolean leading) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("SELECT projectId FROM projectattandences WHERE studentId = ? AND leads = ?");
			DatabaseUtils.setInt(stmt, 1, studentId);
			DatabaseUtils.setBoolean(stmt, 2, leading);
			SQLResult result = connection.query(stmt);
			
			List<Integer> projects = new ArrayList<>();
			
			while (result.next()) {
				projects.add(result.current("projectId", int.class));
			}
			
			result.close();
			return projects;
		};
	}
	
	public SQLRequest<List<Integer>> getStudents(int projectId, boolean leading) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("SELECT studentId FROM projectattandences WHERE projectId = ? AND leads = ?");
			DatabaseUtils.setInt(stmt, 1, projectId);
			DatabaseUtils.setBoolean(stmt, 2, leading);
			SQLResult result = connection.query(stmt);
			
			List<Integer> projects = new ArrayList<>();
			
			while (result.next()) {
				projects.add(result.current("studentId", int.class));
			}
			
			result.close();
			return projects;
		};
	}

	public SQLRequest<List<Entry>> getEntries() {
		return () -> {
			SQLResult result = this.getDatabase().getConnection().query("SELECT * FROM projectattandences");
			
			List<Entry> entries = new ArrayList<>();
			
			while (result.next()) {
				entries.add(new Entry(result.current("projectId"), result.current("studentId"), result.current("leads")));
			}
			
			result.close();
			return entries;
		};
	}
	
	public static class Entry {
		
		public boolean leading;
		public int projectId;
		public int studentId;
		
		public Entry(int projectId, int studentId, boolean leading) {
			this.projectId = projectId;
			this.studentId = studentId;
			this.leading = leading;
		}
		
	}
	
}