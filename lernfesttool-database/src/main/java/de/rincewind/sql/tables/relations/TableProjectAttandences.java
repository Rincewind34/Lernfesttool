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
	
}