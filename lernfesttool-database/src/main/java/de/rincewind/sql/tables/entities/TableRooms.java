package de.rincewind.sql.tables.entities;

import java.sql.PreparedStatement;

import de.rincewind.sql.Database;
import de.rincewind.sql.DatabaseConnection;
import de.rincewind.sql.SQLRequest;
import de.rincewind.sql.abstracts.EntityTable;
import de.rincewind.sql.util.DatabaseUtils;

public class TableRooms extends EntityTable {

	public static TableRooms instance() {
		return (TableRooms) Database.instance().getTable("rooms");
	}

	public TableRooms() {
		super("rooms", "roomId");
	}

	@Override
	protected void executeCreateQuery(DatabaseConnection connection) {
		connection.update("CREATE TABLE IF NOT EXISTS rooms (roomId INT auto_increment, name VARCHAR(32), eBoard BOOLEAN, hardware BOOLEAN, sports BOOLEAN,"
				+ "musics BOOLEAN, size TINYINT, PRIMARY KEY(roomId))");
	}

	@Override
	public SQLRequest<Integer> insertEmptyDataset() {
		return () -> {
			return this.getDatabase().getConnection()
					.insert("INSERT INTO rooms (name, eBoard, hardware, sports, musics, size) VALUES ('Raum', false, false, false, false, 25)");
		};
	}

	public SQLRequest<Void> update(int roomId, String name, boolean eBoard, boolean hardware, boolean sports, boolean musics, byte size) {
		return () -> {
			DatabaseConnection connection = this.getDatabase().getConnection();

			PreparedStatement stmt = connection
					.prepare("UPDATE rooms SET name = ?, eBoard = ?, hardware = ?, sports = ?, musics = ?, size = ? WHERE roomId = ?");
			DatabaseUtils.setString(stmt, 1, name);
			DatabaseUtils.setBoolean(stmt, 2, eBoard);
			DatabaseUtils.setBoolean(stmt, 3, hardware);
			DatabaseUtils.setBoolean(stmt, 4, sports);
			DatabaseUtils.setBoolean(stmt, 5, musics);
			DatabaseUtils.setByte(stmt, 6, size);
			DatabaseUtils.setInt(stmt, 7, roomId);
			connection.update(stmt);

			return null;
		};
	}
	
	
	
}
