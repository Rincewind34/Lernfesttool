package de.rincewind.sql.tables.entities;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import de.rincewind.sql.Database;
import de.rincewind.sql.DatabaseConnection;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.abstracts.EntityTable;
import de.rincewind.sql.util.DatabaseUtils;
import de.rincewind.sql.util.SQLResult;

public class TableStudents extends EntityTable {

	public static TableStudents instance() {
		return (TableStudents) Database.instance().getTable("students");
	}

	public TableStudents() {
		super("students", "studentId");
	}

	@Override
	protected void executeCreateQuery(DatabaseConnection connection) {
		connection.update("CREATE TABLE IF NOT EXISTS students (studentId INT auto_increment, lastname VARCHAR(32), firstname VARCHAR(32), classId INT,"
				+ "accessLevel TINYINT, state TINYINT, password VARCHAR(32), PRIMARY KEY(studentId))");
	}

	@Override
	public SQLRequest<Integer> insertEmptyDataset() {
		return () -> {
			return this.getDatabase().getConnection()
					.insert("INSERT INTO students (lastname, firstname, classId, accessLevel, state, password) VALUES ('Max', 'Mustermann', -1, 0, 1, '')");
		};
	}

	public SQLRequest<Void> update(int studentId, String lastname, String firstname, int classId, byte accessLevel, byte state, String password) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();

			PreparedStatement stmt = connection
					.prepare("UPDATE students SET lastname = ?, firstname = ?, classId = ?, accessLevel = ?, state = ?," + "password = ? WHERE studentId = ?");

			DatabaseUtils.setString(stmt, 1, lastname);
			DatabaseUtils.setString(stmt, 2, firstname);
			DatabaseUtils.setInt(stmt, 3, classId);
			DatabaseUtils.setByte(stmt, 4, accessLevel);
			DatabaseUtils.setByte(stmt, 5, state);
			DatabaseUtils.setString(stmt, 6, password);
			DatabaseUtils.setInt(stmt, 7, studentId);
			connection.update(stmt);

			return null;
		};
	}

	public SQLRequest<List<Integer>> getByClass(int schoolClassId) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();

			PreparedStatement stmt = connection.prepare(this.format("SELECT %key FROM %table WHERE classId = ?"));
			DatabaseUtils.setInt(stmt, 1, schoolClassId);
			SQLResult result = connection.query(stmt);

			List<Integer> values = new ArrayList<>();

			while (result.next()) {
				values.add(result.current(this.getPrimaryKey()));
			}

			result.close();
			return values;
		};
	}

}
