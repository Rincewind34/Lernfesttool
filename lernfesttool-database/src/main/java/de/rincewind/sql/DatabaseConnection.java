package de.rincewind.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;

import de.rincewind.sql.util.SQLResult;

public interface DatabaseConnection {
	
	public abstract void open();
	
	public abstract void close();
	
	public abstract void update(String sql);
	
	public abstract void update(PreparedStatement stmt);
	
	public abstract boolean isOpen();
	
	public abstract int insert(String sql);
	
	public abstract SQLResult query(String sql);
	
	public abstract SQLResult query(PreparedStatement stmt);
	
	public abstract PreparedStatement prepare(String sql);
	
	public abstract PreparedStatement prepare(String sql, int generatedKeys);
	
	public abstract Connection getJavaConnection();
	
}
