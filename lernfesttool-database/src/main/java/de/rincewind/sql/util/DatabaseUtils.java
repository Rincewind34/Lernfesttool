package de.rincewind.sql.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseUtils {
	
	public static void setString(PreparedStatement stmt, int position, String param) {
		try {
			stmt.setString(position, param);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static void setInt(PreparedStatement stmt, int position, int param) {
		try {
			stmt.setInt(position, param);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static void setLong(PreparedStatement stmt, int position, long param) {
		try {
			stmt.setLong(position, param);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static void setByte(PreparedStatement stmt, int position, byte param) {
		try {
			stmt.setByte(position, param);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static void setBoolean(PreparedStatement stmt, int position, boolean param) {
		try {
			stmt.setBoolean(position, param);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static void setFloat(PreparedStatement stmt, int position, float param) {
		try {
			stmt.setFloat(position, param);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static void setDouble(PreparedStatement stmt, int position, double param) {
		try {
			stmt.setDouble(position, param);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
}
