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

public class TableProjectLeading extends AbstractTable {
	
	public static TableProjectLeading instance() {
		return (TableProjectLeading) Database.instance().getTable("projectleading");
	}
	
	public TableProjectLeading() {
		super("projectleading");
	}
	
	@Override
	protected void executeCreateQuery(DatabaseConnection connection) {
		connection.update("CREATE TABLE IF NOT EXISTS projectleading (projectId INT, studentId INT)");
	}

	public SQLRequest<Void> add(int projectId, int studentId) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("INSERT INTO projectleading (projectId, studentId) VALUES (?, ?)");
			DatabaseUtils.setInt(stmt, 1, projectId);
			DatabaseUtils.setInt(stmt, 2, studentId);
			connection.update(stmt);
			return null;
		};
	}
	
	public SQLRequest<List<Integer>> getProjects(int studentId) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("SELECT projectId FROM projectleading WHERE studentId = ?");
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
	
	public SQLRequest<List<Integer>> getStudents(int projectId) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("SELECT studentId FROM projectleading WHERE projectId = ?");
			DatabaseUtils.setInt(stmt, 1, projectId);
			SQLResult result = connection.query(stmt);
			
			List<Integer> projects = new ArrayList<>();
			
			while (result.next()) {
				projects.add(result.current("studentId", int.class));
			}
			
			result.close();
			return projects;
		};
	}
	
	public SQLRequest<Void> clearProject(int projectId) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("DELETE FROM projectleading WHERE projectId = ?");
			DatabaseUtils.setInt(stmt, 1, projectId);
			connection.update(stmt);
			return null;
		};
	}
	
	public SQLRequest<Void> clearStudent(int studentId) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("DELETE FROM projectleading WHERE studentId = ?");
			DatabaseUtils.setInt(stmt, 1, studentId);
			connection.update(stmt);
			return null;
		};
	}
	
//	public SQLRequest<Boolean> contains(int projectId, int studentId) {
//		return new SQLRequest<Boolean>() {
//
//			@Override
//			public Boolean sync() {
//				SQLResult rs = TableProjectleading.this.query("SELECT COUNT(*) AS total FROM {table} WHERE pID = {0} AND sID = {1}", projectId, studentId).sync();
//				
//				if (rs.next()) {
//					if (rs.current("total", long.class) >= 1) {
//						return true;
//					}
//				}
//				
//				return false;
//			}
//			
//		};
//	}
//	
}