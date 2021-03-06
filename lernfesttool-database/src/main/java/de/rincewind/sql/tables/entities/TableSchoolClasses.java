package de.rincewind.sql.tables.entities;

import java.sql.PreparedStatement;
import java.util.Map.Entry;

import de.rincewind.sql.Database;
import de.rincewind.sql.DatabaseConnection;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.abstracts.EntityTable;
import de.rincewind.sql.util.CustomMapEntry;
import de.rincewind.sql.util.DatabaseUtils;
import de.rincewind.sql.util.SQLResult;

public class TableSchoolClasses extends EntityTable {

	public static TableSchoolClasses instance() {
		return (TableSchoolClasses) Database.instance().getTable("schoolclasses");
	}

	public TableSchoolClasses() {
		super("schoolclasses", "classId");
	}

	@Override
	protected void executeCreateQuery(DatabaseConnection connection) {
		connection.update("CREATE TABLE IF NOT EXISTS schoolclasses (classId INT auto_increment, classLevel TINYINT, classData VARCHAR(8), teacherId INT,"
				+ "roomId INT, PRIMARY KEY(classId))");
	}

	@Override
	public SQLRequest<Integer> insertEmptyDataset() {
		return () -> {
			return this.getDatabase().getConnection().insert("INSERT INTO schoolclasses (classLevel, classData, teacherId, roomId) VALUES (1, 'a', -1, -1)");
		};
	}

	public SQLRequest<Void> update(int classId, int classLevel, String classData, int teacherId, int roomId) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();

			PreparedStatement stmt = connection.prepare("UPDATE schoolclasses SET classLevel = ?, classData = ?, teacherId = ?, roomId = ? WHERE classId = ?");
			DatabaseUtils.setInt(stmt, 1, classLevel);
			DatabaseUtils.setString(stmt, 2, classData);
			DatabaseUtils.setInt(stmt, 3, teacherId);
			DatabaseUtils.setInt(stmt, 4, roomId);
			DatabaseUtils.setInt(stmt, 5, classId);
			connection.update(stmt);

			return null;
		};
	}

	public SQLRequest<Entry<Integer, FieldMap>> getByRoomId(int roomId, String... columns) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();

			PreparedStatement stmt = connection.prepare("SELECT classId, " + this.buildSelection(columns) + " FROM schoolclasses WHERE roomId = ?");
			DatabaseUtils.setInt(stmt, 1, roomId);
			SQLResult result = connection.query(stmt);

			Entry<Integer, FieldMap> entry = null;

			if (result.next()) {
				entry = new CustomMapEntry<>(result.current("classId", int.class), this.fillFieldMap(result, columns));
			}

			result.close();
			return entry;
		};
	}

}
