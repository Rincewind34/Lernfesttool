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

public class TableProjectHelping extends AbstractTable {
	
	public static TableProjectHelping instance() {
		return (TableProjectHelping) Database.instance().getTable("projecthelping");
	}
	
	public TableProjectHelping() {
		super("projecthelping");
	}
	
	@Override
	protected void executeCreateQuery(DatabaseConnection connection) {
		connection.update("CREATE TABLE IF NOT EXISTS projecthelping (id INT AUTO_INCREMENT, projectId INT, guideId INT, guideType TINYINT, PRIMARY KEY(id))");
	}
	
	public SQLRequest<Void> add(int projectId, int guideId, byte guideType) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("INSERT INTO {table} (projectId, guideId, guideType) VALUES (?, ?, ?)");
			DatabaseUtils.setInt(stmt, 1, projectId);
			DatabaseUtils.setInt(stmt, 2, guideId);
			DatabaseUtils.setByte(stmt, 3, guideType);
			
			connection.update(stmt);
			return null;
		};
	}
	
	public SQLRequest<List<Integer>> getProjects(int guideId, byte guideType) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("SELECT projectId FROM projecthelping WHERE guideId = ? AND guideType = ?");
			DatabaseUtils.setInt(stmt, 1, guideId);
			DatabaseUtils.setByte(stmt, 2, guideType);
			SQLResult result = connection.query(stmt);
			
			List<Integer> projects = new ArrayList<>();
			
			while (result.next()) {
				projects.add(result.current("projectId", int.class));
			}
			
			
			result.close();
			return projects;
		};
	}
	
	public SQLRequest<Void> clearProject(int projectId) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("DELETE FROM projecthelping WHERE projectId = ?");
			DatabaseUtils.setInt(stmt, 1, projectId);
			connection.update(stmt);
			
			return null;
		};
	}
	
	public SQLRequest<Void> clearGuide(int guideId, byte guideType) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("DELETE FROM projecthelping WHERE guideId = ? AND guideType = ?");
			DatabaseUtils.setInt(stmt, 1, guideId);
			DatabaseUtils.setByte(stmt, 1, guideType);
			connection.update(stmt);
			
			return null;
		};
	}
	
	public SQLRequest<List<Integer>> getGuides(int projectId, byte guideType) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();
			PreparedStatement stmt = connection.prepare("SELECT guideId FROM projecthelping WHERE projectId = ? AND guideId = ?");
			DatabaseUtils.setInt(stmt, 1, projectId);
			DatabaseUtils.setByte(stmt, 2, guideType);
			SQLResult result = connection.query(stmt);
			
			List<Integer> guides = new ArrayList<>();
			
			while (result.next()) {
				guides.add(result.current("guideId", int.class));
			}
			
			
			result.close();
			return guides;
		};
	}
}