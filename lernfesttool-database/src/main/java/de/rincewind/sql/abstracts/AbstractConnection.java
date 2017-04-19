package de.rincewind.sql.abstracts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.rincewind.sql.DatabaseConnection;
import de.rincewind.sql.util.SQLResult;

public abstract class AbstractConnection implements DatabaseConnection {
	
	private Connection connection;
	
	public AbstractConnection() {
		this.connection = null;
	}
	
	@Override
	public void close() {
		if (!this.isOpen()) {
			return;
		}
		
		try {
			this.connection.close();
		} catch (SQLException ex) {
			this.connection = null;
			throw new RuntimeException(ex);
		}
		
		this.connection = null;
	}
	
	@Override
	public void update(String sql) {
		this.update(this.prepare(sql));
	}
	
	@Override
	public void update(PreparedStatement stmt) {
		if  (!this.isOpen()) {
			throw new RuntimeException("The connection is not opened!");
		}
		
		try {
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException ex) {
			System.out.println(ex);
			
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public int insert(String sql) {
		return this.insert(this.prepare(sql, Statement.RETURN_GENERATED_KEYS));
	}
	
	@Override
	public SQLResult query(String sql) {
		return this.query(this.prepare(sql));
	}
	
	@Override
	public SQLResult query(PreparedStatement stmt) {
		if  (!this.isOpen()) {
			throw new RuntimeException("The connection is not opened!");
		}
		
		try {
			return new SQLResult(stmt.executeQuery(), stmt);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public boolean isOpen() {
		return this.connection != null;
	}
	
	@Override
	public PreparedStatement prepare(String sql) {
		if  (!this.isOpen()) {
			throw new RuntimeException("The connection is not opened!");
		}
		
		try {
			return this.connection.prepareStatement(sql);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public PreparedStatement prepare(String sql, int generatedKeys) {
		if  (!this.isOpen()) {
			throw new RuntimeException("The connection is not opened!");
		}
		
		try {
			return this.connection.prepareStatement(sql, generatedKeys);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public Connection getJavaConnection() {
		return this.connection;
	}
	
	public int insert(PreparedStatement stmt) {
		if  (!this.isOpen()) {
			throw new RuntimeException("The connection is not opened!");
		}
		
		try {
			int affectedRows = stmt.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Creating user failed, no rows affected.");
			}

			ResultSet generatedKeys = stmt.getGeneratedKeys();
			long datasetId;
			
			if (generatedKeys.next()) {
				datasetId = generatedKeys.getLong(1);
			} else {
				throw new SQLException("Creating user failed, no ID obtained.");
			}
			
			stmt.close();
			return (int) datasetId;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	protected void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	protected Connection getConnection() {
		return this.connection;
	}
	
}
