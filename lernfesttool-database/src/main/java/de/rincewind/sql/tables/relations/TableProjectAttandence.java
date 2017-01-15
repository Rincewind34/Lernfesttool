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

public class TableProjectAttandence extends AbstractTable {
	
	public static TableProjectAttandence instance() {
		return (TableProjectAttandence) Database.instance().getTable("projectattandence");
	}
	
	public TableProjectAttandence() {
		super("projectattandence");
	}
	
	@Override
	protected void executeCreateQuery(DatabaseConnection connection) {
		connection.update("CREATE TABLE IF NOT EXISTS projectattandence (projectId INT, studentId INT)");
	}
	
	public SQLRequest<Void> add(int projectId, int studentId) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("INSERT INTO projectattandence (projectId, studentId) VALUES (?, ?)");
			DatabaseUtils.setInt(stmt, 1, projectId);
			DatabaseUtils.setInt(stmt, 2, studentId);
			connection.update(stmt);
			return null;
		};
	}
	
	public SQLRequest<Void> clearProject(int projectId) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("DELETE FROM projectattandence WHERE projectId = ?");
			DatabaseUtils.setInt(stmt, 1, projectId);
			connection.update(stmt);
			return null;
		};
	}
	
	public SQLRequest<Void> clearStudent(int studentId) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("DELETE FROM projectattandence WHERE studentId = ?");
			DatabaseUtils.setInt(stmt, 1, studentId);
			connection.update(stmt);
			return null;
		};
	}
	
	public SQLRequest<List<Integer>> getStudents(int projectId) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("SELECT studentId FROM projectattandence WHERE projectId = ?");
			DatabaseUtils.setInt(stmt, 1, projectId);
			connection.update(stmt);
			SQLResult result = connection.query(stmt);
			
			List<Integer> students = new ArrayList<>();
			
			while (result.next()) {
				students.add(result.current("studentId", int.class));
			}
			
			result.close();
			return students;
		};
	}
	
	public SQLRequest<List<Integer>> getProjects(int studentId) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("SELECT projectId FROM projectattandence WHERE studentId = ?");
			DatabaseUtils.setInt(stmt, 1, studentId);
			SQLResult result = connection.query(stmt);
			
			List<Integer> projects = new ArrayList<>();
			
			while (result.next()) {
				projects.add(result.current("projectId", int.class));
			}
			
			result.close();
			return projects;
		};
	}

}