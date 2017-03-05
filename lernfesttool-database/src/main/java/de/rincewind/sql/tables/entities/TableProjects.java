package de.rincewind.sql.tables.entities;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.rincewind.sql.Database;
import de.rincewind.sql.DatabaseConnection;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.abstracts.EntityTable;
import de.rincewind.sql.util.DatabaseUtils;
import de.rincewind.sql.util.SQLResult;

public class TableProjects extends EntityTable {

	public static TableProjects instance() {
		return (TableProjects) Database.instance().getTable("projects");
	}

	public TableProjects() {
		super("projects", "projectId");
	}

	@Override
	protected void executeCreateQuery(DatabaseConnection connection) {
		connection.update("CREATE TABLE IF NOT EXISTS projects (projectId INT auto_increment, roomId INT, name TEXT, minClass TINYINT, maxClass TINYINT,"
				+ "minStudents TINYINT, maxStudents TINYINT, costs INT, description LONGTEXT, notes LONGTEXT, type TINYINT, requestEBoard BOOLEAN,"
				+ "requestHardware BOOLEAN, requestSports BOOLEAN, requestMusics BOOLEAN, category TINYINT, accepted BOOLEAN, PRIMARY KEY(projectId))");
	}

	@Override
	public SQLRequest<Integer> insertEmptyDataset() {
		return () -> {
			PreparedStatement stmt = this.getDatabase().getConnection()
					.prepare("INSERT INTO projects (roomId, name, minClass, maxClass, minStudents, maxStudents, costs, description,"
							+ "notes, type, requestEBoard, requestHardware, requestSports, requestMusics, category, accepted) VALUES"
							+ "(-1, '', 5, 12, 5, 10, 0, '', '', 2, false, false, false, false, 0, false)");
			return this.getDatabase().getConnection().insert(stmt);
		};
	}

	public SQLRequest<Void> update(int projectId, int roomId, String name, byte minClass, byte maxClass, byte minStudents, byte maxStudents, int costs,
			String description, String notes, byte type, boolean requestEBoard, boolean requestHardware, boolean requestSports, boolean requestMusics,
			byte category) {

		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();

			String sql = "UPDATE projects SET roomId = ?, name = ? minClass = ?, maxClass = ?, minStudents = ?, maxStudents = ?, costs = ?, description = ?, notes = ?,"
					+ "type = ?, requestEBoard = ?, requestHardware = ?, requestSports = ?, requestMusics = ?, category = ? WHERE projectId = ?";

			PreparedStatement stmt = connection.prepare(sql);
			DatabaseUtils.setInt(stmt, 1, roomId);
			DatabaseUtils.setString(stmt, 2, name);
			DatabaseUtils.setByte(stmt, 3, minClass);
			DatabaseUtils.setByte(stmt, 4, maxClass);
			DatabaseUtils.setByte(stmt, 5, minStudents);
			DatabaseUtils.setByte(stmt, 6, maxStudents);
			DatabaseUtils.setInt(stmt, 7, costs);
			DatabaseUtils.setString(stmt, 8, description);
			DatabaseUtils.setString(stmt, 9, notes);
			DatabaseUtils.setByte(stmt, 10, type);
			DatabaseUtils.setBoolean(stmt, 11, requestEBoard);
			DatabaseUtils.setBoolean(stmt, 12, requestHardware);
			DatabaseUtils.setBoolean(stmt, 13, requestSports);
			DatabaseUtils.setBoolean(stmt, 14, requestMusics);
			DatabaseUtils.setByte(stmt, 15, category);
			connection.update(stmt);

			return null;
		};
	}

	public SQLRequest<List<Integer>> getForClass(int classLevel) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();

			String sql = "SELECT projectId FROM projects WHERE minClass <= ? AND maxClass >= ?";

			PreparedStatement stmt = connection.prepare(sql);
			DatabaseUtils.setInt(stmt, 1, classLevel);
			DatabaseUtils.setInt(stmt, 2, classLevel);
			SQLResult result = connection.query(stmt);

			List<Integer> projectIds = new ArrayList<>();

			while (result.next()) {
				projectIds.add(result.current("projectId"));
			}
			
			result.close();
			return projectIds;
		};
	}
	
	public SQLRequest<Map<Integer, FieldMap>> getByRoom(int roomId, String... columns) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();

			PreparedStatement stmt = connection.prepare("SELECT projectId, " + this.buildSelection(columns) + " FROM projects WHERE roomId = ?");
			DatabaseUtils.setInt(stmt, 1, roomId);
			SQLResult result = connection.query(stmt);

			Map<Integer, FieldMap> projects = new HashMap<>();
			
			while (result.next()) {
				projects.put(result.current("projectId"), this.fillFieldMap(result, columns));
			}
			
			result.close();
			return projects;
		};
	}

}
